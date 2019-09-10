package nl.interjel.management.util.converter;

import nl.interjel.management.util.EntityManagerWrapper;

import javax.persistence.TypedQuery;

/**
 * <b>T should always be a Container holding X</b>
 *
 * @author Finn Bon
 */
public class PageToListConverter<T, X> implements ParameterConverter<T> {

    public static final int RESULTS_PER_PAGE = 100;
    // this type is NOT the type of the parameter, this is the type specified in the Paginate annotation
    private final Class<X> type;
    private EntityManagerWrapper entityManager;

    public PageToListConverter(Class<X> aClass, EntityManagerWrapper entityManager) {
        this.type = aClass;
        this.entityManager = entityManager;
    }

    @Override
    public T fromString(String s) {
        int page = stringToInt(s);
        return (T) entityManager.transaction(em -> {
            TypedQuery<X> query = em.createQuery(String.format("SELECT x FROM %s x", type.getSimpleName()), type);
            query.setFirstResult(page * RESULTS_PER_PAGE);
            query.setMaxResults(RESULTS_PER_PAGE);
            return query.getResultList();
        });
    }

    @Override
    public String toString(T t) {
        return null;
    }

}
