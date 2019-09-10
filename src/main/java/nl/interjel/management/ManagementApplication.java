package nl.interjel.management;

import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.interjel.management.auth.JwtAuthenticator;
import nl.interjel.management.auth.User;
import nl.interjel.management.filter.CORSFilter;
import nl.interjel.management.model.anonymous.AnonymousObject;
import nl.interjel.management.rest.*;
import nl.interjel.management.rest.MemberResource;
import nl.interjel.management.rest.account.AccountResource;
import nl.interjel.management.util.EntityManagerWrapper;
import nl.interjel.management.util.GsonJsonProvider;
import nl.interjel.management.util.converter.ParameterConverterProvider;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Finn Bon
 */
public class ManagementApplication extends Application<ManagementConfiguration> {

    private final boolean testing;

    public ManagementApplication(boolean test) {
        this.testing = test;
    }

    public static void main(String[] args) {
        try {
            ManagementApplication app = new ManagementApplication(args.length > 1 && args[0].equals("test"));
            app.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Interjel Management";
    }

    @Override
    public void initialize(Bootstrap<ManagementConfiguration> bootstrap) {
    }

    public void run(ManagementConfiguration config, Environment environment) {

        // Enable CORS headers
        FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        JerseyEnvironment jersey = environment.jersey();

        byte[] key = config.getJwtTokenSecret();
        String databaseFile = config.getDatabaseFile();

        EntityManagerWrapper factory = new EntityManagerWrapper(setupDatabase(databaseFile));

        jersey.register(new ParameterConverterProvider(factory));

        registerAuthentication(jersey, key);
        registerResources(jersey, factory, key);

        jersey.register(new GsonJsonProvider());

        jersey.register(new CORSFilter());
    }

    private Map<String, String> readDatabaseCredentials(String url) {
        if (url == null)
            return null;
        try (Stream<String> stream = Files.lines(Paths.get(url))) {
            JsonElement element = new JsonParser().parse(stream.collect(Collectors.joining()));
            if (!element.isJsonObject())
                return null;
            JsonObject json = element.getAsJsonObject();
            JsonElement username = json.get("username");
            JsonElement password = json.get("password");
            JsonElement dbUrl = json.get("url");
            if (username == null || !username.isJsonPrimitive())
                return null;
            if (password == null || !password.isJsonPrimitive())
                return null;
            if (dbUrl == null || !dbUrl.isJsonPrimitive())
                return null;
            return AnonymousObject
                    .createRoot()
                    .set("javax.persistence.jdbc.user", username.getAsString())
                    .set("javax.persistence.jdbc.password", password.getAsString())
                    .set("javax.persistence.jdbc.url", dbUrl.getAsString())
                    .set("hibernate.connection.url", dbUrl.getAsString())
                    .build(String.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void registerAuthentication(JerseyEnvironment jersey, byte[] key) {
        JwtConsumer consumer = new JwtConsumerBuilder()
                .setAllowedClockSkewInSeconds(30)
                .setRequireExpirationTime()
                .setVerificationKey(new HmacKey(key))
                .setRelaxVerificationKeyValidation()
                .build();

        jersey.register(new AuthDynamicFeature(
                new JwtAuthFilter.Builder<User>()
                        .setJwtConsumer(consumer)
                        .setRealm("realm")
                        .setPrefix("Bearer")
                        .setAuthenticator(new JwtAuthenticator())
                        .buildAuthFilter()
        ));

        jersey.register(new AuthValueFactoryProvider.Binder<>(User.class));
        jersey.register(RolesAllowedDynamicFeature.class);
    }

    private void registerResources(JerseyEnvironment jersey, EntityManagerWrapper factory, byte[] key) {
        RestResource resource;

        resource = new MemberResource(factory, key);
        jersey.register(resource);

        resource = new SeasonResource(factory, key);
        jersey.register(resource);

        resource = new AuthResource(factory, key);
        jersey.register(resource);

        resource = new AccountResource(factory, key);
        jersey.register(resource);

        resource = new InstanceResource(factory, key);
        jersey.register(resource);
    }

    public EntityManagerFactory setupDatabase(String databaseFile) {
        Map<String, String> databaseCredentials = readDatabaseCredentials(databaseFile);
        if (databaseCredentials == null) {
            databaseCredentials = new HashMap<>();

            System.out.println("Please enter the database url:");
            databaseCredentials.put("javax.persistence.jdbc.url", new Scanner(System.in).nextLine());
            databaseCredentials.put("hibernate.connection.url", databaseCredentials.get("javax.persistence.jdbc.url"));

            System.out.println("Please enter the database user:");
            databaseCredentials.put("javax.persistence.jdbc.user", new Scanner(System.in).nextLine());

            System.out.println("Please enter the database password:");
            databaseCredentials.put("javax.persistence.jdbc.password", new Scanner(System.in).nextLine());
        }

        return Persistence.createEntityManagerFactory("interjel", databaseCredentials);
    }
}
