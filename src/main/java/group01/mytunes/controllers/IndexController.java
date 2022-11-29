package group01.mytunes.controllers;

import group01.mytunes.Models.Playlist;
import group01.mytunes.dao.PlaylistDatabaseDAO;
import group01.mytunes.datamodels.IndexDataModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class IndexController implements Initializable {

    private IndexDataModel playlistDataModel;

    @FXML private ListView listViewPlaylistSongs;
    @FXML private ListView<Playlist> listViewPlayLists;
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

        this.playlistDataModel = new IndexDataModel(new PlaylistDatabaseDAO());

        listViewPlayLists.setItems(playlistDataModel.getPlaylists());

        listViewPlayLists.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                System.out.println(listViewPlayLists.getSelectionModel().getSelectedItem());
            }
        });

        System.out.println("Controller initialized");
    }

    private void displayError(Throwable t)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("!!ERROR!!");
        alert.setHeaderText("Something went wrong, \n ERROR:      " + t.getMessage());
        alert.showAndWait();
    }

    public void newPlaylistHandler(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New playlist");
        dialog.setHeaderText("Create a new playlist");
        dialog.setContentText("Playlist name:");
        dialog.setGraphic(null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(playlist -> playlistDataModel.addPlaylist(playlist));
    }

    public void editPlaylistHandler(ActionEvent actionEvent) {
        Playlist selectedPlaylist = listViewPlayLists.getSelectionModel().getSelectedItem();
        if(selectedPlaylist == null) return;

        TextInputDialog dialog = new TextInputDialog(selectedPlaylist.getName());
        dialog.setTitle("Edit playlist");
        dialog.setHeaderText("Edit playlist");
        dialog.setContentText("New playlist name:");
        dialog.setGraphic(null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            playlistDataModel.editPlaylist(selectedPlaylist, newName);
        });
    }

    public void deleteSelectedPlaylistHandler(ActionEvent actionEvent) {
        Playlist selectedPlaylist = listViewPlayLists.getSelectionModel().getSelectedItem();
        if(selectedPlaylist != null) playlistDataModel.deletePlaylist(selectedPlaylist);
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
