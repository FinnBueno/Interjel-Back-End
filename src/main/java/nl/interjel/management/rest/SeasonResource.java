package nl.interjel.management.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.dropwizard.auth.Auth;
import nl.interjel.management.auth.User;
import nl.interjel.management.model.anonymous.AnonymousObject;
import nl.interjel.management.model.entity.Member;
import nl.interjel.management.model.entity.Season;
import nl.interjel.management.model.entity.SeasonPayment;
import nl.interjel.management.model.view.MemberFull;
import nl.interjel.management.util.EntityManagerWrapper;

import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Finn Bon
 */
@Path("season")
@Produces(MediaType.APPLICATION_JSON)
public class SeasonResource extends RestResource {

    public SeasonResource(EntityManagerWrapper factory, byte[] key) {
        super(factory, key);
    }

    @GET
    @Timed
    public Response getActiveSeason(@Auth User user) {
        return transaction(em -> {
            TypedQuery<Season> seasonQuery = em.createQuery("SELECT s FROM Season s ORDER BY s.startDate DESC", Season.class);
            seasonQuery.setMaxResults(1);
            List<Season> seasons = seasonQuery.getResultList();
            if (seasons.size() == 0) {
                return badRequest("No active season found");
            }
            Season season = seasons.get(0);

            TypedQuery<MemberFull> unpaidQuery = em.createQuery(""+
                    "SELECT mf " +
                    "FROM MemberFull mf " +
                    "LEFT JOIN SeasonPayment sp" +
                    "    ON (" +
                    "      mf.memberId = sp.member.memberId AND" +
                    "      sp.paymentDate = (" +
                    "        SELECT MAX(sp2.paymentDate)" +
                    "        FROM SeasonPayment sp2" +
                    "      )" +
                    "    )" +
                    "WHERE" +
                    "  sp.paymentDate < (" +
                    "    SELECT MAX(s.startDate)" +
                    "    FROM Season s" +
                    "  )", MemberFull.class);
            List<MemberFull> list = unpaidQuery.getResultList();

            AnonymousObject obj = AnonymousObject.createRoot(true);
            obj.set("startDate", season.getStartDate());
            obj.set("openPayments", list);

            return ok(obj.build());
        });
    }

    @POST
    @Timed
    public Response startNewSeason(@Auth User user) {
        return transaction(em -> {
            Season season = new Season(LocalDate.now());
            em.persist(season);
            return ok(AnonymousObject.createRoot().set("startTime", season.getStartDate()).build());
        });
    }

    @PUT
    @Timed
    public Response payContribution(@Auth User user, String body) {
        JsonObject json = parseBody(body);
        JsonElement member = json.get("id");
        if (member == null || !member.isJsonPrimitive() || !member.getAsJsonPrimitive().isNumber())
            return badRequest("id field is not present or not of proper type.");

        int id = member.getAsInt();
        return transaction(em -> {
            Member memberObj = em.find(Member.class, id);
            if (memberObj == null)
                return badRequest(String.format("No member with id %d was found.", id));
            TypedQuery<Season> season = em.createQuery("SELECT s FROM Season s ORDER BY s.startDate DESC", Season.class);
            season.setMaxResults(1);
            if (season.getResultList().size() == 0) {
                return badRequest("There's no active season to pay contribution for.");
            }
            TypedQuery<SeasonPayment> payments = em.createQuery(""+
                    "SELECT sp " +
                    "FROM SeasonPayment sp " +
                    "WHERE sp.member = :memberId " +
                    "AND sp.paymentDate >= (" +
                    "SELECT MAX(s.startDate) " +
                    "FROM Season s " +
                    ")", SeasonPayment.class);
            payments.setParameter("memberId", memberObj);
            if (payments.getResultList().size() > 0)
                return badRequest("Member has already paid contribution for this season.");
            SeasonPayment sp = new SeasonPayment(LocalDate.now(), memberObj);
            em.persist(sp);
            return ok();
        });
    }

}
