import group01.mytunes.Main;
import group01.mytunes.database.DatabaseConnectionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestDBConnection {

    @BeforeEach
    public void setup() throws IOException {

        var url = Main.class.getResource("config.properties");

        Properties props = new Properties();
        try(InputStream input = url.openStream()) {
            props.load(input);
        }

        var dbIp = props.getProperty("DB_IP");
        var dbPort = Integer.parseInt(props.getProperty("DB_PORT"));
        var dbName= props.getProperty("DB_NAME");
        var dbUsername = props.getProperty("DB_USERNAME");
        var dbPassword = props.getProperty("DB_PASSWORD");

        DatabaseConnectionHandler.init(dbIp, dbPort, dbName, dbUsername, dbPassword);
    }

    @Test
    public void TestDatabaseConnection() throws SQLException {
        var connection = DatabaseConnectionHandler.getInstance().getConnection();

        assertTrue(!connection.isClosed());
    }

}
