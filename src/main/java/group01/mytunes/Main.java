package group01.mytunes;

import group01.mytunes.entities.User;
import group01.mytunes.database.DatabaseConnectionHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main extends Application {

    public static User currentUser = new User(1, "User 1");

    /**
     * Reads the Congig file and makes the connection to DB
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        var url = Main.class.getResource("config.properties");

        Properties props = new Properties();
        if(url != null)
            try(InputStream input = url.openStream()) {
            props.load(input);
        }

        //Gets the DB Value from a config file
        var dbIp = props.getProperty("DB_IP");
        var dbPort = Integer.parseInt(props.getProperty("DB_PORT"));
        var dbName= props.getProperty("DB_NAME");
        var dbUsername = props.getProperty("DB_USERNAME");
        var dbPassword = props.getProperty("DB_PASSWORD");

        DatabaseConnectionHandler.init(dbIp, dbPort, dbName, dbUsername, dbPassword);

        launch(args);
    }

    /**
     * Launches the FXML program and index file
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Index.fxml"));

        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setTitle("MyTunes");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }
}