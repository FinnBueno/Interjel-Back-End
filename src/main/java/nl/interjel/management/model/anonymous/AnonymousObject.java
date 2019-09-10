package nl.interjel.management.model.anonymous;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Finn Bon
 */
public class AnonymousObject {
    private final Map<String, Object> fields;
    private final AnonymousObject parent;

    private boolean allowNormalObjects;

    private AnonymousObject(AnonymousObject parent, boolean allowNormalObjects) {
        this.fields = new HashMap<>();
        this.parent = parent;
        this.allowNormalObjects = allowNormalObjects;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public AnonymousObject createChild(String key) {
        AnonymousObject newObj = new AnonymousObject(this, allowNormalObjects);
        set(key, newObj);
        return newObj;
    }

    public AnonymousObject set(String key, Object value) {
        this.fields.put(key, value);
        return this;
    }

    public AnonymousObject insertObject(Object obj) {
        return insertObject(obj, null);
    }

    public AnonymousObject insertObject(Object obj, String key) {
        List<Field> fields = Arrays.asList(obj.getClass().getDeclaredFields());
        AnonymousObject writeOn = key == null ? this : createChild(key);
        fields.forEach(field -> {
            try {
                field.setAccessible(true);
                if (!Modifier.isTransient(field.getModifiers()))
                    writeOn.set(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return this;
    }

    public <T> Map<String, T> build(Class<T> type) {
        return (Map<String, T>) build(false);
    }

    public Map<String, Object> build() {
        return build(false);
    }

    public Map<String, Object> build(boolean allowUnsafeSerialization) {
        if (!allowUnsafeSerialization && !isRoot()) {
            throw new UnsupportedOperationException("Cannot serialize child object. If you wish to do this anyway, set 'allowUnsafeSerialization' to true.");
        }
        return build(this);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    private Object write(Object o) {
        if (o instanceof AnonymousObject) { // found another AnonymousObject
            return build((AnonymousObject) o);
        } else if (o instanceof Collection<?> || o.getClass().isArray()) { // found collection
            if (o.getClass().isArray())
                o = Arrays.asList((Object[]) o);
            List<Object> coll = new LinkedList<>();
            for (Object element : (Collection) o) {
                coll.add(write(element));
            }
            return coll;
        } else { // found a normal object
            return o;
        }
    }

    private Map<String, Object> build(AnonymousObject obj) {
        Map<String, Object> output = new TreeMap<>();
        for (Map.Entry<String, Object> stringObjectEntry : obj.fields.entrySet()) {
            if (stringObjectEntry.getValue() == null)
                continue;
            output.put(stringObjectEntry.getKey(), write(stringObjectEntry.getValue()));
        }
        return output;
    }

    public static AnonymousObject createRoot() {
        return createRoot(true);
    }

    public static AnonymousObject createRoot(boolean allowNormalObjects) {
        return new AnonymousObject(null, allowNormalObjects);
    }

}
