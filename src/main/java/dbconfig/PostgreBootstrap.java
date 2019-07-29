package dbconfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreBootstrap {
    static public Connection bootstrap(String url, String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection postgresql = DriverManager.getConnection(url, user, password);
        return postgresql;
    }
}
