package group01.mytunes.controllers;

import group01.mytunes.Models.Playlist;
import group01.mytunes.Models.Song;
import group01.mytunes.audio.IAudioHandler;
import group01.mytunes.audio.SingleFileAudioHandler;
import group01.mytunes.dao.ArtistDatabaseDAO;
import group01.mytunes.dao.PlaylistDatabaseDAO;
import group01.mytunes.dao.SongDatabaseDAO;
import group01.mytunes.dao.interfaces.ISongDAO;
import group01.mytunes.datamodels.IndexDataModel;
import group01.mytunes.dialogs.AddSongDialog;
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
    private IAudioHandler audioHandler;

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

    @FXML private MenuItem menuQuit;
    @FXML private MenuItem menuAddSong;

    @FXML private MenuItem menuAddArtist, menuEditArtist, menuDeleteArtist;
    @FXML private MenuItem menuAddPlaylist, menuEditPlaylist, menuDeletePlaylist;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ISongDAO songDAO = new SongDatabaseDAO();

        audioHandler = new SingleFileAudioHandler(songDAO);

        this.indexDataModel = new IndexDataModel(new PlaylistDatabaseDAO(), songDAO, new ArtistDatabaseDAO());

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

        // Playlist list view
        listViewPlaylistSongs.setItems(indexDataModel.getSongPlaylistInfoObservableList());

        lblCurrentSelectedPlaylist.textProperty().bind(
            Bindings.when(indexDataModel.getSelectedPlaylistObservable().isNull())
                .then("No playlist selected")
                .otherwise(indexDataModel.getSelectedPlaylistObservable().asString())
        );

        initMenuBar();

        System.out.println("Controller initialized");
    }

    /**
     * Initializes the buttons in the menu bar.
     */
    private void initMenuBar() {
        menuQuit.setOnAction(event -> System.exit(0));

        menuAddSong.setOnAction(event -> makeNewSongWindowOpen());

        menuAddArtist.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add artist");
            dialog.setHeaderText("Add an artist");
            dialog.setContentText("Artist name:");
            dialog.setGraphic(null);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(artist -> indexDataModel.addArtist(artist));
        });
        menuEditArtist.setOnAction(event -> indexDataModel.editArtist());
        menuDeleteArtist.setOnAction(event -> indexDataModel.deleteArtist());

        menuAddPlaylist.setOnAction(event -> newPlaylistHandler());
        menuEditPlaylist.setOnAction(event -> editPlaylistHandler());
    }

    public void newPlaylistHandler() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New playlist");
        dialog.setHeaderText("Create a new playlist");
        dialog.setContentText("Playlist name:");
        dialog.setGraphic(null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(playlist -> indexDataModel.addPlaylist(playlist));
    }

    public void editPlaylistHandler() {
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
        AddSongDialog dialog = new AddSongDialog(listViewSongs.getScene().getWindow());
        dialog.showAndWait().ifPresent(song -> {
            if(song == null) return;
            indexDataModel.addSong(song);
        });
    }

    public void deleteSelectedSong(ActionEvent actionEvent) {
    }

    public void insertSongToPlaylist() {
        var selectedPlaylist = indexDataModel.getSelectedPlaylistObservable().getValue();
        var selecedSong = listViewSongs.getSelectionModel().getSelectedItem();

        indexDataModel.addSongToPlaylist(selecedSong, selectedPlaylist);
    }

    public void searchForSong(ActionEvent actionEvent) {
    }

}
