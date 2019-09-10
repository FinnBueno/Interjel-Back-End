package nl.interjel.management.util;

import com.google.gson.*;
import nl.interjel.management.util.annotation.JsonName;
import nl.interjel.management.util.annotation.Optionally;
import nl.interjel.management.util.annotation.SkipAutoGeneration;
import nl.interjel.management.util.deserializer.JsonDeserializerManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Finn Bon
 */
public class ReflectionUtil {

    public static <T> Pair<T, String> jsonToObject(JsonObject json, Class<T> type) {
        try {
            T object = type.getConstructor().newInstance();
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                // check if this field should be skipped
                if (field.getAnnotation(SkipAutoGeneration.class) != null || Modifier.isTransient(field.getModifiers()))
                    continue;
                // check if field has custom json name
                JsonName customJsonNameAnnotation = field.getAnnotation(JsonName.class);
                String jsonName = customJsonNameAnnotation == null ? field.getName() : customJsonNameAnnotation.key();
                // get the value from the json
                JsonElement value = json.get(jsonName);
                // if the value was not found in the JSON, check if the field is mandatory
                if (value == null) { // element not found
                    // if not, skip it. If it is, return an error
                    if (field.getAnnotation(Optionally.class) != null)
                        continue;
                    else
                        return new Pair<>(null, String.format("Field %s did not have a value but is required.", jsonName));
                }
                Optional<String> potentialError = setField(object, field, value);
                if (potentialError.isPresent())
                    return new Pair<>(null, potentialError.get());
            }
            return new Pair<>(object, null);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return new Pair<>(null, "An error occurred");
    }

    /* This primitiveJsonMethods maps classes to corresponding GSON methods */
    private static final Map<Class<?>, Function<JsonPrimitive, Object>> primitiveJsonMethods = new HashMap<>();

    static {
        primitiveJsonMethods.put(Integer.TYPE, JsonPrimitive::getAsInt);
        primitiveJsonMethods.put(Integer.class, JsonPrimitive::getAsInt);
        primitiveJsonMethods.put(Long.TYPE, JsonPrimitive::getAsLong);
        primitiveJsonMethods.put(Long.class, JsonPrimitive::getAsLong);
        primitiveJsonMethods.put(Short.TYPE, JsonPrimitive::getAsShort);
        primitiveJsonMethods.put(Short.class, JsonPrimitive::getAsShort);
        primitiveJsonMethods.put(Double.TYPE, JsonPrimitive::getAsDouble);
        primitiveJsonMethods.put(Double.class, JsonPrimitive::getAsDouble);
        primitiveJsonMethods.put(Float.TYPE, JsonPrimitive::getAsFloat);
        primitiveJsonMethods.put(Float.class, JsonPrimitive::getAsFloat);
        primitiveJsonMethods.put(String.class, JsonPrimitive::getAsString);
    }

    public static Optional<String> setField(Object object, Field field, JsonElement value) {
        // cannot use null values
        if (value == null)
            return Optional.of(String.format("Attempted to use a null value for field %s.", field.getName()));

        // arrays are not supported nor required
        if (value.isJsonArray())
            return Optional.of("Arrays are not supported.");

        // assure field is accessible
        field.setAccessible(true);

        // if the primitiveJsonMethods contains the field's type, that means the field's type is primitive, and we can simply assign the value
        if (primitiveJsonMethods.containsKey(field.getType())) {
            // if the JSON value is not primitive, that means these types are not compatible
            if (!value.isJsonPrimitive())
                return Optional.of(String.format("Field %s is not of type %s.", field.getName(), field.getType().getSimpleName()));
            try {
                // assign the value
                field.set(object, primitiveJsonMethods.get(field.getType()).apply(value.getAsJsonPrimitive()));
                return Optional.empty();
            } catch (ClassCastException | NumberFormatException e) {
                // if one of these exceptions is thrown, it means the types were not compatible (string to number for example)
                return Optional.of(String.format("Field %s is not of type %s.", field.getName(), field.getType().getSimpleName()));
            } catch (IllegalAccessException e) {
                // unknown access problem
                return Optional.of("Something went wrong while performing this task.");
            }
            // check if the type is an enum, because then we accept string values!
        } else if (field.getType().isEnum()) {
            if (!value.isJsonPrimitive() && !value.getAsJsonPrimitive().isString()) {
                return Optional.of(String.format("Field %s is not of type string.", field.getName()));
            }
            try {
                for (Object o : field.getType().getEnumConstants()) {
                    if (o.toString().equalsIgnoreCase(value.getAsString())) {
                        field.set(object, o);
                        return Optional.empty();
                    }
                }
                return Optional.of(String.format("Given value did not match any enum entries for field %s", field.getName()));
            } catch (IllegalAccessException e) {
                return Optional.of("Something went wrong while performing this task.");
            }
            // else, the type is not primitive, but an object
        } else {
            // if the JSON is not an object, we got incompatible types
            if (!value.isJsonObject()) {
                return Optional.of(String.format("Field %s is not of type object.", field.getName()));
            }
            // find the JsonDeserializer that belongs to this particular type
            JsonDeserializer deserializer = JsonDeserializerManager.getInstance().getDeserializerByType(field.getType());
            // if the deserializer is null, that means this type is unknown to the deserializer manager and an error is returned
            if (deserializer == null) {
                return Optional.of(String.format("No serializer found for type %s.", field.getType()));
            }
            try {
                // generate new value
                Object newValue = deserializer.deserialize(value, field.getType(), null);
                // assign it
                field.set(object, newValue);
                return Optional.empty();
            } catch (NullPointerException | ClassCastException | IllegalStateException | NumberFormatException e) {
                // not all required fields were met. This is simply determined by things such as null pointers or casting exceptions coming from the deserializer
                return Optional.of(String.format("Json object at field %s does not contain all required fields for class %s.", field.getName(), field.getType().getSimpleName()));
            } catch (IllegalAccessException e) {
                // unknown access exception
                return Optional.of("Something went wrong while performing this task.");
            }
        }
    }

}
