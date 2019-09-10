package nl.interjel.management.rest.account;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import nl.interjel.management.auth.User;
import nl.interjel.management.model.anonymous.AnonymousObject;
import nl.interjel.management.model.entity.Account;
import nl.interjel.management.rest.RestResource;
import nl.interjel.management.util.EntityManagerWrapper;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Finn Bon
 */
@Path("account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource extends RestResource {

    public AccountResource(EntityManagerWrapper factory, byte[] key) {
        super(factory, key);
    }

    @GET
    @Timed
    public Response check(@Auth User user) {
        return ok(AnonymousObject
                .createRoot()
                .set("message", "Successfully authenticated")
                .set("root", user.isRoot()).build());
    }

    @POST
    @Timed
    // TODO: Try to write a param converter for Account to automatically create it
    public Response createAccount(@Auth User user, AccountBody body) {
        return transaction(em -> {
            Account account = new Account(body);
            em.persist(account);
            return ok();
        });
    }

}
