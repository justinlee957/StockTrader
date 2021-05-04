
public class Utils {
	// Local MySQL connecters
	public static String driver = "com.mysql.cj.jdbc.Driver";
	public static String connecter = "jdbc:mysql://localhost:3306/Assignment4?user=root&password=root";
	
	// Google Cloud connecters
	// Uncomment below and comment above when deploying
//	public static String driver = "com.mysql.jdbc.GoogleDriver";
//	public static String connecter = "jdbc:mysql:///Assignment4?cloudSqlInstance="
//	+ "stocktrader-312602:us-central1:stocktrader&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=test";
}
