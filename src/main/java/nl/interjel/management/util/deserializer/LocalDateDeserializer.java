package nl.interjel.management.util.deserializer;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 * @author Finn Bon
 */
@JsonAdapter(value = LocalDate.class)
public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = element.getAsJsonObject();
         return LocalDate.of(json.get("year").getAsInt(), json.get("month").getAsInt(), json.get("day").getAsInt());
    }

}
