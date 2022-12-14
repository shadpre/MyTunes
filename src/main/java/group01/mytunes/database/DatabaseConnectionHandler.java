package group01.mytunes.database;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionHandler {

    private static DatabaseConnectionHandler instance;

    private SQLServerDataSource source;

    private DatabaseConnectionHandler(String ip, int port, String dbName, String username, String password) {
        source = new SQLServerDataSource();
        source.setServerName(ip);
        source.setPortNumber(port);
        source.setUser(username);
        source.setPassword(password);
        source.setDatabaseName(dbName);
        source.setTrustServerCertificate(true);

        if(!isConnectionWorking()) {
            System.exit(-1);
        }
    }

    public boolean isConnectionWorking() {
        try {
            if (getConnection() != null && !getConnection().isClosed()) {
                return true;
            } else {
                System.err.println("No connection to database!");
            }
        } catch (SQLException e) {
            System.err.println("No connection to database!");
        }
        return false;
    }

    public static void init(String ip, int port, String dbName, String username, String password) {
        if(instance != null) return;

        instance = new DatabaseConnectionHandler(ip, port, dbName, username, password);
    }

    public static DatabaseConnectionHandler getInstance() {
        if(instance == null) {
            System.err.println("Please initialize DatabaseConnectionHandler.java before using it!");
            System.exit(-1);
        }

        return instance;
    }

    public Connection getConnection() throws SQLServerException {
        return source.getConnection();
    }
}