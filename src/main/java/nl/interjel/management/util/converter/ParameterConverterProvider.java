package nl.interjel.management.util.converter;

import nl.interjel.management.util.EntityManagerWrapper;
import nl.interjel.management.util.annotation.Findable;
import nl.interjel.management.util.annotation.Paginate;

import javax.validation.constraints.NotNull;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * @author Finn Bon
 */
public class ParameterConverterProvider implements ParamConverterProvider {

    private EntityManagerWrapper entityManager;

    public ParameterConverterProvider(EntityManagerWrapper entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> aClass, Type type, Annotation[] annotations) {
        if (hasAnnotation(annotations, Findable.class)) {
            return new IdToEntityConverter<>(
                    aClass,
                    hasAnnotation(annotations, NotNull.class),
                    entityManager
            );
        }
        if (hasAnnotation(annotations, Paginate.class)) {
            List<Annotation> annotationList = Arrays.asList(annotations);
            Paginate paginate = null;
            for (Annotation annotation : annotationList) {
                if (annotation instanceof Paginate)
                    paginate = (Paginate) annotation;
            }
            if (paginate == null)
                return null;
            return new PageToListConverter<>(
                    paginate.value(),
                    entityManager
            );
        }
        return null;
    }

    private boolean hasAnnotation(Annotation[] annotations, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(annotations).anyMatch(annotation -> annotation.annotationType().equals(annotationClass));
    }
}
