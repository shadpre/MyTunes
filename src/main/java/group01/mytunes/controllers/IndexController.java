package group01.mytunes.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class IndexController implements Initializable {
    @FXML private ListView listViewPlaylistList;
    @FXML private ListView listViewPlayLIst;
    @FXML private ListView listViewSongs;
    @FXML private Label labelSongPlaying;
    @FXML private TextField txtFieldSearchbar;
    @FXML private Button btnPlaylistEdit;
    @FXML private Button btnPlaylistNew;
    @FXML private Button btnPlaylistDelete;
    @FXML private Button btnPlaylistUp;
    @FXML private Button btnPlaylistDown;
    @FXML private Button btnSongEdit;
    @FXML private Button btnSongNew;
    @FXML private Button btnSongDelete;
    @FXML private Button btnMoveSongToPlaylist;
    @FXML private Button btnSearch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Controller initialized");
    }

    private void displayError(Throwable t)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("!!ERROR!!");
        alert.setHeaderText("Something went wrong, \n ERROR:      " + t.getMessage());
        alert.showAndWait();
    }


    public void editPlaylistWindowOpen(ActionEvent actionEvent) {
    }

    public void newPlaylistWindowOpen(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../PlayListCreate.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    public void deleteSelectedPlaylist(ActionEvent actionEvent) {
    }

    public void scrollUpInPlaylist(ActionEvent actionEvent) {
    }

    public void scrollDownInPlaylist(ActionEvent actionEvent) {
    }

    public void editSongWindowOpen(ActionEvent actionEvent) {
    }

    public void makeNewSongWindowOpen(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../songCreator.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    public void deleteSelectedSong(ActionEvent actionEvent) {
    }

    public void InsertSongToPlaylist(ActionEvent actionEvent) {
    }

    public void searchForSong(ActionEvent actionEvent) {
    }

}
