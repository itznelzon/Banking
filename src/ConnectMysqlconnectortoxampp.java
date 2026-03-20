import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectMysqlconnectortoxampp {
	private static final String HOST = "localhost";
	private static final String PORT = "3306";
	private static final String DATABASE = "jameskylebank";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "";

	private static final String URL =
		"jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
			+ "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	private static final String SERVER_URL =
		"jdbc:mysql://" + HOST + ":" + PORT
			+ "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

	private ConnectMysqlconnectortoxampp() {
		// Utility class
	}

	public static Connection getConnection() throws SQLException {
		// Optional for compatibility with older JDBC loading behavior.
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException exception) {
			throw new SQLException("MySQL JDBC driver not found in classpath.", exception);
		}

		initializeDatabaseIfMissing();

		return DriverManager.getConnection(URL, USERNAME, PASSWORD);
	}

	private static void initializeDatabaseIfMissing() throws SQLException {
		String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS " + DATABASE;

		try (Connection connection = DriverManager.getConnection(SERVER_URL, USERNAME, PASSWORD);
			 java.sql.Statement statement = connection.createStatement()) {
			statement.executeUpdate(createDatabaseSql);
		}
	}

	public static boolean testConnection() {
		try (Connection connection = getConnection()) {
			return connection != null && !connection.isClosed();
		} catch (SQLException exception) {
			System.out.println("MySQL connection failed to database '" + DATABASE + "': " + exception.getMessage());
			return false;
		}
	}
}
