package nl.interjel.management.rest;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import nl.interjel.management.auth.User;
import nl.interjel.management.model.anonymous.AnonymousObject;
import nl.interjel.management.model.entity.Instance;
import nl.interjel.management.model.entity.Member;
import nl.interjel.management.model.key.MemberVisitPK;
import nl.interjel.management.model.relationship.MemberVisit;
import nl.interjel.management.util.EntityManagerWrapper;
import nl.interjel.management.util.Pair;
import nl.interjel.management.util.ReflectionUtil;
import nl.interjel.management.util.annotation.Findable;
import nl.interjel.management.util.annotation.Paginate;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Finn Bon
 */
@Path("instance")
@Produces(MediaType.APPLICATION_JSON)
public class InstanceResource extends RestResource {

    public InstanceResource(EntityManagerWrapper factory, byte[] key) {
        super(factory, key);
    }

    @GET
    @Timed
    public List<Instance> getInstances(
            @Auth User user,
            @QueryParam("page")
            @NotNull(message = "The page index must be an integer greater than or equal to 0.")
            @Paginate(Instance.class)
                    List<Instance> instances) {
        return instances;
    }

    @GET
    @Timed
    @Path("{id}")
    public Response getInstance(
            @Auth User user,
            @PathParam("id")
            @Findable
            @NotNull(message = "The id must be an integer greater than or equal to 0.")
                    Instance instance) {
        return ok(
                AnonymousObject
                        .createRoot()
                        .set("instanceId", instance.getInstanceId())
                        .set("startDate", instance.getStartDate())
                        .set("endDate", instance.getEndDate())
                        .set("notes", instance.getNotes())
                        .set("visitors", instance.getVisitorsAsMembers())
                        .build()
        );
    }

    @POST
    @Timed
    public Response startInstance(
            @Auth User user,
            String body) {
        return transaction(em -> {
            Pair<Instance, String> potentialEvening = ReflectionUtil.jsonToObject(parseBody(body), Instance.class);
            if (potentialEvening.getValue() != null)
                return badRequest(potentialEvening.getValue());
            Instance instance = potentialEvening.getKey();
            instance.assureStartDate();

            em.persist(potentialEvening.getKey());
            return ok(potentialEvening.getKey());
        });
    }

    @PUT
    @Timed
    @Path("{id}")
    public Response closeInstance(
            @Auth User user,
            @PathParam("id")
            @NotNull(message = "The id must be an integer greater than or equal to 0.")
                    int eveningId) {
        return transaction(em -> {
            Instance evening = em.find(Instance.class, eveningId);
            evening.setEndDate(LocalDateTime.now());
            em.persist(evening);
            return ok(evening.getEndDate());
        });
    }

    @DELETE
    @Timed
    @Path("{id}")
    public Response deleteInstance(
            @Auth User user,
            @PathParam("id")
            @NotNull(message = "The id must be an integer greater than or equal to 0.")
                    int eveningId) {
        return transaction(em -> {
            Instance evening = em.find(Instance.class, eveningId);
            evening.getVisitors().forEach(em::remove);
            em.remove(evening);
            return ok(evening);
        });
    }

    @POST
    @Timed
    @Path("{id}/{member}")
    public Response addVisitor(
            @Auth User user,
            @PathParam("id")
            @NotNull(message = "The id must be an integer greater than or equal to 0.")
                    int eveningId,
            @PathParam("member")
            @NotNull(message = "The id must be an integer greater than or equal to 0.")
                    int memberId) {
        return transaction(em -> {
            Instance evening = em.find(Instance.class, eveningId);
            Member member = em.find(Member.class, memberId);

            if (evening.isClosed())
                return badRequest("Can't add visitor to closed evening!");
            MemberVisit mv = em.find(MemberVisit.class, new MemberVisitPK(evening.getInstanceId(), member.getMemberId()));
            if (mv == null)
                em.persist(evening.addVisitor(member));
            else
                return badRequest("This member has already visited this instance.");
            return ok();
        });
    }

    @DELETE
    @Timed
    @Path("{id}/{member}")
    public Response removeVisitor(
            @Auth User user,
            @PathParam("id")
            @Findable
            @NotNull(message = "The id must be an integer greater than or equal to 0.")
                    Instance evening,
            @PathParam("member")
            @Findable
            @NotNull(message = "The id must be an integer greater than or equal to 0.")
                    Member member) {
        return transaction(em -> {
            if (evening.isClosed())
                return badRequest("Can't add visitor to closed evening!");
            MemberVisit mv = em.find(MemberVisit.class, new MemberVisitPK(evening.getInstanceId(), member.getMemberId()));
            if (mv == null)
                return badRequest("Player with id %d has not visited instance with id %d, could not remove.", member.getMemberId(), evening.getInstanceId());
            em.remove(mv);
            return ok();
        });
    }

}
