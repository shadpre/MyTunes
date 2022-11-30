package group01.mytunes.controllers;

import group01.mytunes.dao.PlaylistDatabaseDAO;
import group01.mytunes.datamodels.PlaylistDataModel;
import group01.mytunes.dialogs.NewSongDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class IndexController implements Initializable {

    private PlaylistDataModel playlistDataModel;

    @FXML private ListView listViewPlaylistSongs;
    @FXML private ListView listViewPlayLists;
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
        this.playlistDataModel = new PlaylistDataModel(new PlaylistDatabaseDAO());

        listViewPlayLists.setItems(playlistDataModel.getPlaylists());

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
        NewSongDialog dialog = new NewSongDialog(listViewSongs.getScene().getWindow());
        dialog.showAndWait().ifPresent(song -> {
            if(song == null) return;

            System.out.println(song.getTitle());
            System.out.println(song.getData().length);
        });
    }

    public void deleteSelectedSong(ActionEvent actionEvent) {
    }

    public void InsertSongToPlaylist(ActionEvent actionEvent) {
    }

    public void searchForSong(ActionEvent actionEvent) {
    }

}
