package nl.interjel.management.util.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Finn Bon
 */
public class DatabaseCredentialsFromJson extends DatabaseCredentialsFetcherStrategy {
	@Override
	public boolean fetch() {
		try (Stream<String> stream = Files.lines(Paths.get("database-credentials.json"))) {
			JsonElement element = new JsonParser().parse(stream.collect(Collectors.joining()));
			if (!element.isJsonObject())
				return false;
			JsonObject json = element.getAsJsonObject();
			JsonElement username = json.get("username");
			JsonElement password = json.get("password");
			JsonElement dbUrl = json.get("url");
			if (username == null || !username.isJsonPrimitive())
				return false;
			if (password == null || !password.isJsonPrimitive())
				return false;
			if (dbUrl == null || !dbUrl.isJsonPrimitive())
				return false;
			this.setUsername(username.getAsString());
			this.setPassword(password.getAsString());
			this.setURL(dbUrl.getAsString());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
