package nl.interjel.management.util.database;

/**
 * @author Finn Bon
 */
public class DatabaseCredentialsFromEnvironment extends DatabaseCredentialsFetcherStrategy {
	@Override
	public boolean fetch() {
		String foundUsername = System.getenv("DATABASE_USERNAME");
		String foundPassword = System.getenv("DATABASE_PASSWORD");
		String foundUrl = System.getenv("DATABASE_URL");
		if (foundUrl == null || foundPassword == null || foundUsername == null) {
			return false;
		}
		setUsername(foundUsername);
		setPassword(foundPassword);
		setURL(foundUrl);
		return true;
	}
}
