package nl.interjel.management.util.database;

import nl.interjel.management.model.anonymous.AnonymousObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Finn Bon
 */
public class DatabaseCredentialsFetcher {

	private DatabaseCredentialsFetcherStrategy strategies[] = new DatabaseCredentialsFetcherStrategy[] {
		new HerokuUrlFetcher(),
		new DatabaseCredentialsFromEnvironment(),
		new DatabaseCredentialsFromJson(),
	};

	public Map<String, String> fetch() {
		for (DatabaseCredentialsFetcherStrategy strategy : strategies) {
			if (!strategy.fetch()) {
				continue;
			}
			return AnonymousObject
				.createRoot()
				.set("javax.persistence.jdbc.user", strategy.getUsername())
				.set("javax.persistence.jdbc.password", strategy.getPassword())
				.set("javax.persistence.jdbc.url", strategy.getDatabase())
				.set("hibernate.connection.url", strategy.getDatabase())
				.set("hibernate.default_schema", strategy.getSchema() != null ? strategy.getSchema() : "interjel")
				.set("hibernate.show_sql", false)
				.set("hibernate.format_sql", false)
				.build(String.class);
		}
		HashMap<String, String> databaseCredentials = new HashMap<>();

		System.out.println("Please enter the database url:");
		databaseCredentials.put("javax.persistence.jdbc.url", new Scanner(System.in).nextLine());
		databaseCredentials.put("hibernate.connection.url", databaseCredentials.get("javax.persistence.jdbc.url"));

		System.out.println("Please enter the database user:");
		databaseCredentials.put("javax.persistence.jdbc.user", new Scanner(System.in).nextLine());

		System.out.println("Please enter the database password:");
		databaseCredentials.put("javax.persistence.jdbc.password", new Scanner(System.in).nextLine());

		return databaseCredentials;
	}

}
