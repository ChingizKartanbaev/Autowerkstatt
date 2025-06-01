package pti.datenbank.autowerk.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL =
            "jdbc:sqlserver://localhost:1433;"
                    + "databaseName=Autowerkstatt;"
                    + "encrypt=false";

    private static final String USERNAME = "sa";
    private static final String PASSWORD = "msserver";

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load JDBC driver: " + DRIVER_CLASS);
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }


    public static void close(AutoCloseable... resources) {
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception e) {
                    System.err.println("Failed to close resource: " + e.getMessage());
                }
            }
        }
    }
}
