package nl.interjel.management.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.dropwizard.auth.Auth;
import nl.interjel.management.auth.BCrypt;
import nl.interjel.management.auth.User;
import nl.interjel.management.model.anonymous.AnonymousObject;
import nl.interjel.management.model.entity.Account;
import nl.interjel.management.util.EntityManagerWrapper;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

/**
 * @author Finn Bon
 */
@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource extends RestResource {

    public AuthResource(EntityManagerWrapper factory, byte[] key) {
        super(factory, key);
    }

    @POST
    @Timed
    public Response generateValidToken(String body, @Context HttpHeaders headers) {
        return transaction(em -> {
            Map<String, Cookie> cookies = headers.getCookies();
            String str = cookies.entrySet()
                .stream()
                .map(e -> e.getKey() + " = " + e.getValue().getValue())
                .collect(Collectors.joining("<br/>"));
            System.out.println(str);

            JsonObject json = parseBody(body);

            JsonElement email = json.get("email");
            JsonElement password = json.get("password");
            if (email == null || !email.isJsonPrimitive() || !email.getAsJsonPrimitive().isString() ||
                    password == null || !password.isJsonPrimitive() || !password.getAsJsonPrimitive().isString()) {
                return badRequest("Authentication body must have email and password specified");
            }

            TypedQuery<Account> query = em.createQuery("SELECT a FROM Account a WHERE email = :email", Account.class);
            query.setParameter("email", email.getAsString());

            List<Account> accounts = query.getResultList();
            if (accounts.size() == 0) {
                return badRequest("Could not find account with specified credentials.");
            }

            Account account = accounts.get(0);
            if (!BCrypt.checkpw(password.getAsString(), account.getPassword())) {
                return badRequest("Could not find account with specified credentials.");
            }

            JwtClaims claims = new JwtClaims();
            claims.setClaim("root", account.isRoot());
            claims.setClaim("email", account.getEmail());
//            claims.setExpirationTimeMinutesInTheFuture(60 * 24);
            claims.setIssuedAtToNow();

            JsonWebSignature signature = new JsonWebSignature();
            signature.setPayload(claims.toJson());
            signature.setAlgorithmHeaderValue(HMAC_SHA256);
            signature.setKey(getKey());

            try {
                String token = signature.getCompactSerialization();

                return Response.ok(
                    AnonymousObject
                        .createRoot()
                        .set("token", token)
                        .build()
                ).cookie(new NewCookie("Interjel-Token", token)).build();
            } catch (JoseException e) {
                return serverError("Something went wrong while generating the token.");
            }
        });
    }

    @GET
    public Response getCurrentUser(
        @Auth() User user,
        @Context() HttpHeaders headers
    ) {
        System.out.println("Headers");
        for (Map.Entry<String, List<String>> stringListEntry : headers.getRequestHeaders().entrySet()) {
            if (!stringListEntry.getKey().equalsIgnoreCase("authorization")) continue;
            System.out.println("Key: " + stringListEntry.getKey());
            for (String s : stringListEntry.getValue()) {
                System.out.println(" - " + s);
            }
        }
        // TODO: Pass along root perm of user
        return ok(AnonymousObject.createRoot().set("user", true).build());
    }
}
