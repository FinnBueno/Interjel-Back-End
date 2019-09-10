package nl.interjel.management.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Response;
import java.util.function.Function;

/**
 * @author Finn Bon
 */
public class EntityManagerWrapper {

    private final EntityManagerFactory factory;

    public EntityManagerWrapper(EntityManagerFactory entityManagerFactory) {
        this.factory = entityManagerFactory;
    }

    public <T> T transaction(Function<EntityManager, T> function) {
        T result = null;
        EntityManager entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            result = function.apply(entityManager);
            if (entityManager.getTransaction().getRollbackOnly() ||
                    (result instanceof Response && ((Response) result).getStatus() != Response.Status.OK.getStatusCode())) {
                entityManager.getTransaction().rollback();
            } else {
                entityManager.getTransaction().commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.close();
        }
        return result;
    }

}
