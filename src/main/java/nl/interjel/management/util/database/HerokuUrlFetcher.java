package nl.interjel.management.util.database;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Finn Bon
 */
public class HerokuUrlFetcher extends DatabaseCredentialsFetcherStrategy {

	@Override
	public boolean fetch() {
		String databaseUrl = System.getenv("DATABASE_URL");
		if (databaseUrl == null)
			return false;
		try {
			URI dbUri = new URI(databaseUrl);

			String username = dbUri.getUserInfo().split(":")[0];
			String password = dbUri.getUserInfo().split(":")[1];
			String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
			setUsername(username);
			setPassword(password);
			setURL(dbUrl);
			return true;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}

}
