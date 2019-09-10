package nl.interjel.management.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.interjel.management.model.anonymous.AnonymousObject;
import nl.interjel.management.util.EntityManagerWrapper;
import org.jose4j.keys.HmacKey;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import java.util.function.Function;

/**
 * @author Finn Bon
 */
public abstract class RestResource {

    private static final JsonParser JSON_PARSER = new JsonParser();

    private EntityManagerWrapper factory;
    private byte[] key;

    public RestResource(EntityManagerWrapper factory, byte[] key) {
        this.factory = factory;
        this.key = key;
    }

    public HmacKey getKey() {
        return new HmacKey(key);
    }

    protected <T> T transaction(Function<EntityManager, T> function) {
        return factory.transaction(function);
    }

    protected JsonObject parseBody(String body) {
        if (body == null || body.length() == 0) {
            return new JsonObject();
        }
        JsonElement element = JSON_PARSER.parse(body);
        return element.getAsJsonObject();
    }

    protected Response badRequest(String s, Object... args) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(AnonymousObject.createRoot().set("error", String.format(s, args)).build())
                .build();
    }

    protected Response serverError(String msg) {
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(
                        AnonymousObject
                                .createRoot()
                                .set("error", String.format("Something went wrong while %s.", msg))
                ).build();
    }

    protected Response ok(Object obj) {
        return Response.ok().entity(obj).build();
    }

    protected Response ok() {
        return Response.ok().build();
    }

}
