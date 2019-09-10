package nl.interjel.management.util.converter;

import nl.interjel.management.model.entity.OnSelect;
import nl.interjel.management.util.EntityManagerWrapper;

/**
 * @author Finn Bon
 */
public class IdToEntityConverter<T> implements ParameterConverter<T> {

    private final Class<T> type;
    private final boolean notNull;
    private EntityManagerWrapper entityManager;

    public IdToEntityConverter(Class<T> aClass, boolean notNull, EntityManagerWrapper entityManager) {
        this.type = aClass;
        this.notNull = notNull;
        this.entityManager = entityManager;
    }

    @Override
    public T fromString(String s) {
        int id;
        try {
            id = stringToInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The id must be a positive integer!");
        }
        T entity =  entityManager.transaction(em -> {
            T result = em.find(type, id);
            if (result instanceof OnSelect)
                ((OnSelect) result).select();
            return result;
        });
        if (entity == null && notNull) {
            throw new IllegalArgumentException(String.format("No entity for id %d was found.", id));
        }
        return entity;
    }

    @Override
    public String toString(T t) {
        return t.toString();
    }
}
