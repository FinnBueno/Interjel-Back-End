package nl.interjel.management.util.database;

/**
 * @author Finn Bon
 */
public abstract class DatabaseCredentialsFetcherStrategy {

	private String password;
	private String username;
	private String url;

	public abstract boolean fetch();

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDatabase() {
		return url;
	}

	protected void setURL(String url) {
		this.url = url;
	}
}
