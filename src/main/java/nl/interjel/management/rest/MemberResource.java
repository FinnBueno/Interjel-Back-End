package nl.interjel.management.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.dropwizard.auth.Auth;
import nl.interjel.management.auth.User;
import nl.interjel.management.model.entity.Member;
import nl.interjel.management.model.view.MemberFull;
import nl.interjel.management.util.EntityManagerWrapper;
import nl.interjel.management.util.Pair;
import nl.interjel.management.util.ReflectionUtil;
import nl.interjel.management.util.annotation.Findable;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static nl.interjel.management.util.converter.PageToListConverter.RESULTS_PER_PAGE;

/**
 * @author Finn Bon
 */
@Path("member")
@Produces(MediaType.APPLICATION_JSON)
public class MemberResource extends RestResource {

	public MemberResource(EntityManagerWrapper factory, byte[] key) {
		super(factory, key);
	}

	@GET
	@Timed
	public List<MemberFull> getMembers(
		@Auth User user,
		@QueryParam("page")
		@NotNull(message = "The page index must be an integer greater than or equal to 0.")
			int page) {
		return transaction(em -> {
			// TODO: Change back to @Paginate annotation since it's a GET request
			TypedQuery<MemberFull> query = em.createQuery("SELECT mf FROM MemberFull mf", MemberFull.class);
			query.setFirstResult(page * RESULTS_PER_PAGE);
			query.setMaxResults(RESULTS_PER_PAGE);
			return query.getResultList();
		});
	}

	@GET
	@Timed
	@Path("{id}")
	public Response getMember(
		@Auth User user,
		@PathParam("id")
		@Findable
		@NotNull(message = "The id must be an integer greater than or equal to 0.")
			MemberFull member) {
		return ok(member);
	}

	@POST
	@Timed
	public Response createMember(
		@Auth User user,
		String body) {
		return transaction(em -> {
			Pair<Member, String> pair = ReflectionUtil.jsonToObject(parseBody(body), Member.class);
			if (pair.getValue() != null) {
				return badRequest(pair.getValue());
			}
			try {
				em.persist(pair.getKey());
			} catch (PersistenceException e) {
				e.printStackTrace();
				return badRequest("Combination of firstname and lastname already exists.");
			}
			return ok(pair.getKey());
		});
	}

	@PUT
	@Timed
	@Path("{id}")
	// TODO: Upgrade these PUT methods
	public Response editMember(
		@Auth User user,
		String body,
		@PathParam("id") @NotNull int memberId) {
		return transaction(em -> {
			Member member = em.find(Member.class, memberId);
			JsonObject json = parseBody(body);
			String targetField;
			JsonElement newValue;
			try {
				targetField = json.get("field").getAsString();
				newValue = json.get("value");
			} catch (NullPointerException e) {
				return badRequest("Malformed syntax. Body must contain 'field' and 'value'.");
			}

			Field field;
			try {
				field = member.getClass().getDeclaredField(targetField);
			} catch (NoSuchFieldException e) {
				return badRequest("Field '%s' does not exist on 'member'", targetField);
			}

			Optional<String> potentialError = ReflectionUtil.setField(member, field, newValue);
			if (potentialError.isPresent()) {
				return badRequest(potentialError.get());
			}
			em.persist(member);
			return ok();
		});
	}

	@DELETE
	@Timed
	@Path("{id}")
	public Response deleteMember(
		@Auth User user,
		@PathParam("id")
		@NotNull(message = "Member id must be specified.")
			int memberId) {
		return transaction(em -> {
			Member member = em.find(Member.class, memberId);
			member.setArchived(!member.isArchived());
			em.persist(member);
			return ok(member);
		});
	}

}
