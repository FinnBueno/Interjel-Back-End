package nl.interjel.management;

import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.interjel.management.auth.JwtAuthenticator;
import nl.interjel.management.auth.User;
import nl.interjel.management.filter.CORSFilter;
import nl.interjel.management.rest.*;
import nl.interjel.management.rest.account.AccountResource;
import nl.interjel.management.util.EntityManagerWrapper;
import nl.interjel.management.util.GsonJsonProvider;
import nl.interjel.management.util.converter.ParameterConverterProvider;
import nl.interjel.management.util.database.DatabaseCredentialsFetcher;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.Map;

/**
 * @author Finn Bon
 */
public class ManagementApplication extends Application<ManagementConfiguration> {

    public static void main(String[] args) {
        try {
            ManagementApplication app = new ManagementApplication();
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

        EntityManagerWrapper factory = new EntityManagerWrapper(setupDatabase());

        jersey.register(new ParameterConverterProvider(factory));

        registerAuthentication(jersey, key);
        registerResources(jersey, factory, key);

        jersey.register(new GsonJsonProvider());

        jersey.register(new CORSFilter());
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

    public EntityManagerFactory setupDatabase() {
        DatabaseCredentialsFetcher fetcher = new DatabaseCredentialsFetcher();
        Map<String, String> databaseCredentials = fetcher.fetch();
        return Persistence.createEntityManagerFactory("interjel", databaseCredentials);
    }
}
