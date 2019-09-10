package nl.interjel.management.util.deserializer;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * @author Finn Bon
 */
@JsonAdapter(value = LocalDateTime.class)
public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = element.getAsJsonObject();
        JsonObject date = json.get("date").getAsJsonObject();
        JsonObject time = json.get("time").getAsJsonObject();
        return LocalDateTime.of(
                date.get("year").getAsInt(), date.get("month").getAsInt(), date.get("day").getAsInt(),
                time.get("hour").getAsInt(), time.get("minute").getAsInt(), time.get("second").getAsInt());
    }
}
