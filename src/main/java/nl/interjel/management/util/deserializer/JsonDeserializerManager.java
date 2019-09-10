package nl.interjel.management.util.deserializer;

import com.google.gson.JsonDeserializer;
import com.google.gson.annotations.JsonAdapter;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Finn Bon
 */
public class JsonDeserializerManager {

    private static final JsonDeserializerManager INSTANCE = new JsonDeserializerManager();
    private final Map<Class<?>, JsonDeserializer<?>> serializers;

    public static JsonDeserializerManager getInstance() {
        return INSTANCE;
    }

    private JsonDeserializerManager() {
        serializers = new HashMap<>();
        Reflections reflections = new Reflections("nl.interjel.management");
        Set<Class<? extends JsonDeserializer>> list = reflections.getSubTypesOf(JsonDeserializer.class);
        for (Class<? extends JsonDeserializer> aClass : list) {
            JsonAdapter adapter = aClass.getAnnotation(JsonAdapter.class);
            if (adapter == null)
                continue;
            try {
                serializers.put(adapter.value(), aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> JsonDeserializer<T> getDeserializerByType(Class<T> type) {
        return (JsonDeserializer<T>) this.serializers.get(type);
    }

}
