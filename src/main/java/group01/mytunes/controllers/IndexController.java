package group01.mytunes.controllers;

import group01.mytunes.Models.Playlist;
import group01.mytunes.Models.Song;
import group01.mytunes.audio.AudioHandler;
import group01.mytunes.dao.PlaylistDatabaseDAO;
import group01.mytunes.dao.SongDatabaseDAO;
import group01.mytunes.dao.interfaces.ISongDAO;
import group01.mytunes.datamodels.IndexDataModel;
import group01.mytunes.dialogs.NewSongDialog;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class IndexController implements Initializable {

    private IndexDataModel indexDataModel;
    private AudioHandler audioHandler;

    @FXML private ListView listViewPlaylistSongs;
    @FXML private ListView<Playlist> listViewPlayLists;
    @FXML private ListView<Song> listViewSongs;
    @FXML private Label labelSongPlaying;
    @FXML private Label lblCurrentSelectedPlaylist;
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

        ISongDAO songDAO = new SongDatabaseDAO();

        audioHandler = new AudioHandler(songDAO);

        this.indexDataModel = new IndexDataModel(new PlaylistDatabaseDAO(), songDAO);

        // Playlist list view
        listViewPlayLists.setItems(indexDataModel.getPlaylistsObservableList());
        listViewPlayLists.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                indexDataModel.setSelectedPlaylistObservable(listViewPlayLists.getSelectionModel().getSelectedItem());
            }
        });

        // Song list view
        listViewSongs.setItems(indexDataModel.getSongInfoObservableList());
        listViewSongs.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Song songToPlay = listViewSongs.getSelectionModel().getSelectedItem();
                audioHandler.playSong(songToPlay);
            }
        });

        lblCurrentSelectedPlaylist.textProperty().bind(
            Bindings.when(indexDataModel.getSelectedPlaylistObservable().isNull())
                .then("No playlist selected")
                .otherwise(indexDataModel.getSelectedPlaylistObservable().asString())
        );

        System.out.println("Controller initialized");
    }

    private void displayError(Throwable t) {
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
        result.ifPresent(playlist -> indexDataModel.addPlaylist(playlist));
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
            System.out.println(selectedPlaylist + " " + newName);
            indexDataModel.editPlaylist(selectedPlaylist, newName);
        });
    }

    public void deleteSelectedPlaylistHandler(ActionEvent actionEvent) {
        Playlist selectedPlaylist = listViewPlayLists.getSelectionModel().getSelectedItem();
        if(selectedPlaylist != null) indexDataModel.deletePlaylist(selectedPlaylist);
    }

    public void scrollUpInPlaylist(ActionEvent actionEvent) {
    }

    public void scrollDownInPlaylist(ActionEvent actionEvent) {
    }

    public void editSongWindowOpen(ActionEvent actionEvent) {
    }

    public void makeNewSongWindowOpen() {
        NewSongDialog dialog = new NewSongDialog(listViewSongs.getScene().getWindow());
        dialog.showAndWait().ifPresent(song -> {
            if(song == null) return;
            indexDataModel.addSong(song);
        });
    }

    public void deleteSelectedSong(ActionEvent actionEvent) {
    }

    public void InsertSongToPlaylist(ActionEvent actionEvent) {
    }

    public void searchForSong(ActionEvent actionEvent) {
    }

}
