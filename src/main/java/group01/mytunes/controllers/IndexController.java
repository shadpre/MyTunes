package group01.mytunes.controllers;

import group01.mytunes.dialogs.DropDownTextDialog;
import group01.mytunes.entities.Artist;
import group01.mytunes.entities.Playlist;
import group01.mytunes.entities.PlaylistSong;
import group01.mytunes.entities.Song;
import group01.mytunes.audio.IAudioHandler;
import group01.mytunes.audio.SingleFileAudioHandler;
import group01.mytunes.dao.ArtistDatabaseDAO;
import group01.mytunes.dao.PlaylistDatabaseDAO;
import group01.mytunes.dao.SongDatabaseDAO;
import group01.mytunes.dao.interfaces.IArtistDAO;
import group01.mytunes.dao.interfaces.IPlaylistDAO;
import group01.mytunes.dao.interfaces.ISongDAO;
import group01.mytunes.datamodels.IndexDataModel;
import group01.mytunes.dialogs.AddSongDialog;
import group01.mytunes.nextsong.INextSongStrategy;
import group01.mytunes.nextsong.NextSongFromPlaylistLinearStrategy;
import group01.mytunes.nextsong.NextSongLinearStrategy;
import group01.mytunes.utility.MyTunesUtility;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaException;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for Index.fxml
 */
public class IndexController implements Initializable {
    private IndexDataModel indexDataModel;
    private IAudioHandler audioHandler;

    @FXML private Label lblSongPlaying;
    @FXML private TextField txtFieldSearchbar;
    @FXML private Label lblCurrentSelectedPlaylist;
    @FXML private Label lblCurrentSongTime, lblSongLength;
    @FXML private Button btnPlayPause, btnPreviousSong;
    @FXML private ToggleButton shuffleToggleButton;
    @FXML private Slider sliderSongTimeline, sliderSoundVolume;
    @FXML private ListView<PlaylistSong> listViewPlaylistSongs;
    @FXML private ListView<Playlist> listViewPlayLists;
    @FXML private TableView<Song> listViewSongs;
    @FXML private TableColumn tableColumnTitle, tableColumnArtist;

    /*
        Menu Bar
     */
    @FXML private MenuItem menuAddSong, menuEditSong, menuDeleteSong;
    @FXML private MenuItem menuAddArtist, menuEditArtist, menuDeleteArtist;
    @FXML private MenuItem menuAddAlbum, menuEditAlbum, menuDeleteAlbum;
    @FXML private MenuItem menuAddPlaylist, menuEditPlaylist;
    @FXML private MenuItem menuQuit;

    private INextSongStrategy nextSongStrategy;

    /**
     * Initializes everything needed by the controller.
     * Runs when the controller is created.
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ISongDAO songDAO = new SongDatabaseDAO();
        IPlaylistDAO playlistDAO = new PlaylistDatabaseDAO();
        IArtistDAO artistDAO = new ArtistDatabaseDAO();

        this.indexDataModel = new IndexDataModel(playlistDAO, songDAO, artistDAO);

        audioHandler = new SingleFileAudioHandler(songDAO);

        initMenuBar();

        initListViewPlaylists();

        initListViewSongs();

        initListViewPlaylistSong();

        initSoundVolumeSlider();

        initPlayPrevious();

        initShuffleToggleButton();

        lblCurrentSelectedPlaylist.textProperty().bind(
            Bindings.when(indexDataModel.getSelectedPlaylistObservable().isNull())
                .then("No playlist selected")
                .otherwise(indexDataModel.getSelectedPlaylistObservable().asString())
        );

        lblSongPlaying.setText("Now Playing: No song available");

        System.out.println("Controller initialized");
    }

    /**
     * Initializes the shuffle button.
     */
    private void initShuffleToggleButton() {
        shuffleToggleButton.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            // TODO: Needs to be implemented!
            System.out.println("Old: " + oldValue);
            System.out.println("New: " + newValue);
        }));
    }

    /***
     * Initializes the list view playlists.
     * Adds mouse listener to be able to select a playlist.
     */
    private void initListViewPlaylists() {
        listViewPlayLists.setItems(indexDataModel.getPlaylistsObservableList());
        listViewPlayLists.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                indexDataModel.setSelectedPlaylistObservable(listViewPlayLists.getSelectionModel().getSelectedItem());
            }
        });
    }

    /**
     * Initializes the song list view.
     * Adds mouse listener to be able to play a song.
     */
    private void initListViewSongs() {
        listViewSongs.widthProperty().addListener((source, oldWidth, newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) listViewSongs.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> header.setReordering(false));
        });

        listViewSongs.setItems(indexDataModel.getSongInfoObservableList());

        tableColumnArtist.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>)
                param -> new SimpleStringProperty(indexDataModel.getArtistsForSong(param.getValue()))
        );

        listViewSongs.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                var songId = listViewSongs.getSelectionModel().getSelectedIndex();
                if(nextSongStrategy == null || !nextSongStrategy.getClass().equals(NextSongLinearStrategy.class)) {
                    nextSongStrategy = new NextSongLinearStrategy(
                            indexDataModel.getSongInfoObservableList(),
                            songId
                    );
                } else {
                    nextSongStrategy.changeSong(songId);
                }

                playSong();
            }
        });
    }

    /**
     * Initializes the listview for viewing the songs in the selected playlist.
     * Adds mouse listener to be able to play the song.
     */
    private void initListViewPlaylistSong()  {
        listViewPlaylistSongs.setItems(indexDataModel.getSongPlaylistInfoObservableList());
        listViewPlaylistSongs.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                var song = listViewPlaylistSongs.getSelectionModel().getSelectedIndex();
                if(nextSongStrategy == null || !nextSongStrategy.getClass().equals(NextSongFromPlaylistLinearStrategy.class)) {
                    nextSongStrategy = new NextSongFromPlaylistLinearStrategy(
                        indexDataModel.getSongPlaylistInfoObservableList(),
                        song
                    );
                } else {
                    nextSongStrategy.changeSong(song);
                }
                playSong();
            }
        });
    }

    /**
     * Initializes the buttons in the menu bar.
     * Adds functionality to the buttons in the menu.
     */
    private void initMenuBar() {
        /*
           Song tab
         */
        // Add song
        menuAddSong.setOnAction(event -> makeNewSongWindowOpen());

        // Delete song
        menuDeleteSong.setOnAction(event -> {
            Dialog<Song> deleteSongDialog = new ChoiceDialog<>(null, indexDataModel.getSongInfoObservableList());
            deleteSongDialog.setGraphic(null);
            deleteSongDialog.setHeaderText(null);
            deleteSongDialog.setTitle("Delete a song");
            deleteSongDialog.setContentText("Delete song: ");
            var result = deleteSongDialog.showAndWait();
            result.ifPresent(selectedSong -> indexDataModel.deleteSong(selectedSong));
        });

        /*
            Artist tab
         */
        // Add artist
        menuAddArtist.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setGraphic(null);
            dialog.setHeaderText(null);
            dialog.setTitle("Add artist");
            dialog.setContentText("Artist name:");
            var result = dialog.showAndWait();
            result.ifPresent(artist -> indexDataModel.addArtist(artist));
        });

        // Edit artist
        menuEditArtist.setOnAction(event -> {
            DropDownTextDialog<Artist> dialog = new DropDownTextDialog<>(listViewSongs.getScene().getWindow(), "Edit Artist","New name:","New name", indexDataModel.getArtistList());
            dialog.showAndWait().ifPresent(result ->
                    indexDataModel.editArtist(result.getFirst(), result.getSecond()));
            listViewSongs.refresh();
        });

        // Delete artist
        menuDeleteArtist.setOnAction(event -> {
            Dialog<Artist> deleteArtistDialog = new ChoiceDialog<>(null, indexDataModel.getAllArtists());
            deleteArtistDialog.setGraphic(null);
            deleteArtistDialog.setHeaderText(null);
            deleteArtistDialog.setContentText("Delete artist:");
            deleteArtistDialog.setTitle("Delete an artist");
            Optional<Artist> result = deleteArtistDialog.showAndWait();
            result.ifPresent(artist -> indexDataModel.deleteArtist(artist));
        });

        /*
            Playlist tab
         */
        // Add playlist
        menuAddPlaylist.setOnAction(event -> newPlaylistHandler());

        // Edit playlist
        menuEditPlaylist.setOnAction(event -> {
            DropDownTextDialog<Playlist> dialog = new DropDownTextDialog<>(
                    listViewPlayLists.getScene().getWindow(),
                    "Edit Playlist",
                    "New name",
                    "New name",
                    indexDataModel.getPlaylistsObservableList()
            );

            dialog.showAndWait().ifPresent(result -> indexDataModel.editPlaylist(result.getFirst(), result.getSecond()));
        });

        /*
            Quit button
         */
        menuQuit.setOnAction(event -> System.exit(0));
    }

    /**
     * Initializes the song volume slider.
     */
    private void initSoundVolumeSlider() {
        sliderSoundVolume.setMin(0.0d); // Min bound: 0.0 = 0%
        sliderSoundVolume.setMax(1.0d); // Max bound: 1.0 = 100%
        sliderSoundVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            double volume = sliderSoundVolume.getValue();
            audioHandler.changeVolume(volume);
        });
    }

    /**
     * Handles button click when want to create a new playlist.
     * Opens a text input dialog to get the name to the new playlist.
     */
    public void newPlaylistHandler() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New playlist");
        dialog.setHeaderText("Create a new playlist");
        dialog.setContentText("Playlist name:");
        dialog.setGraphic(null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(playlist -> indexDataModel.addPlaylist(playlist));
    }

    /**
     * Plays the next song. and display the song tittle
     */
    private void playSong() {
        try {
            var songToPlay = nextSongStrategy.getNextSong();
            audioHandler.playSong(songToPlay);
            bindSongSlider();
            updatePlayPauseButtons();
            lblSongPlaying.setText("Now Playing: " + songToPlay.getTitle());


            audioHandler.getMediaPlayer().setOnEndOfMedia(this::playSong);
        } catch(MediaException me) {
            showErrorAlert("Can not play this song!");
            lblSongPlaying.setText("Now Playing: No song available");
        }
    }

    /**
     * Handles button functionality when want to edit a playlist.
     */
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
            indexDataModel.editPlaylist(selectedPlaylist, newName);
        });
    }

    /**
     * Handles button click when want to delete a playlist.
     */
    public void deleteSelectedPlaylistHandler() {
        Playlist selectedPlaylist = listViewPlayLists.getSelectionModel().getSelectedItem(); //gets selected item

        if (selectedPlaylist == null){ //If selectedPlaylist is not selected, the method returns
            return;
        }

        // Makes a pop-up box with 2 buttons Confirm and cancel.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setTitle("Delete");
        alert.setContentText("Do you want to delete the selected playlist? \n" + selectedPlaylist.getName());
        ButtonType OkButton = new ButtonType("Ok", ButtonBar.ButtonData.YES); //MakesConfirm button, with a yes Value
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE); //Makes the cancel button
        alert.getButtonTypes().setAll(OkButton, cancelButton); //Sets buttons in window
        alert.showAndWait().ifPresent(type -> {
            if (type == OkButton) { //if confirm button i pressed delete playlist
                indexDataModel.deletePlaylist(selectedPlaylist);
            }
        });

    }

    /**
     * Handles button click when want to edit a song.
     */
    public void editSongHandler() {
        // TODO: Need to make a new FXML to edit song. Must be able to edit [Song title, artist on song]
        Song selectedSong = listViewSongs.getSelectionModel().getSelectedItem();
        if(selectedSong == null) return;

        TextInputDialog dialog = new TextInputDialog(selectedSong.getTitle());
        dialog.setTitle("Edit Song");
        dialog.setHeaderText("Edit Song");
        dialog.setContentText("New Song name:");
        dialog.setGraphic(null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            indexDataModel.editSong(selectedSong, newName);
        });
    }

    /**
     * Opens the window to make a new song.
     */
    public void makeNewSongWindowOpen() {
        AddSongDialog dialog = new AddSongDialog(listViewSongs.getScene().getWindow());
        dialog.showAndWait().ifPresent(song -> indexDataModel.addSong(song));
    }

    /**
     * Handles button press when wanting to delete the selected song.
     * Prompts with an alert to make sure the button is not hit by mistake.
     */
    public void deleteSelectedSongHandler() {
        Song song = listViewSongs.getSelectionModel().getSelectedItem();
        if(song == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.setTitle("Delete a song");
        alert.setContentText("Are you sure you want to delete %s".formatted(song.getTitle()));
        ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);
        alert.showAndWait().ifPresent(type -> {
            if(type == okButton) {
                indexDataModel.deleteSong(song);
            }
        });
    }

    /**
     * Handles button click when want to add the selected song to a playlist.
     */
    public void insertSongToPlaylistHandler() {
        var selectedSong = listViewSongs.getSelectionModel().getSelectedItem();
        if(selectedSong == null) return;

        indexDataModel.addSongToPlaylist(selectedSong, getSelectedPlaylist(), getSelectedPlaylist());
    }

    /**
     * Gets the selected playlist from index data model
     * @return The selected playlist
     */
    private Playlist getSelectedPlaylist() {
        return indexDataModel.getSelectedPlaylistObservable().getValue();
    }

    /**
     * Filters the songs in the song list table.
     */
    public void searchForSong() {
        indexDataModel.searchForSong(txtFieldSearchbar.getText());
    }

    /**
     * Plays or pauses the song playing.
     */
    public void playOrPauseSong() {
        audioHandler.playPause();
        updatePlayPauseButtons();
    }

    /**
     * Updates the play/pause button.
     */
    private void updatePlayPauseButtons() {
        if (audioHandler.isPlaying()) {
            btnPlayPause.setText("II");
        } else {
            btnPlayPause.setText("⪢");
        }
    }

    /**
     * Binds the data to the song slider.
     */
    private void bindSongSlider() {
        audioHandler.getMediaPlayer().setOnReady(() -> {
            var player = audioHandler.getMediaPlayer();
            sliderSongTimeline.maxProperty().bind(Bindings.createDoubleBinding( // sets  song length
                    () -> player.getTotalDuration().toSeconds(),
                    player.totalDurationProperty()));

            sliderSongTimeline.valueProperty().bind(Bindings.createDoubleBinding( // binds slider progress to mediaPlayer
                    () -> player.getCurrentTime().toSeconds(),
                    player.currentTimeProperty()));

            lblCurrentSongTime.textProperty().bind(Bindings.createStringBinding(
                    () -> {
                        if(audioHandler.getMediaPlayer() == null) return "00:00";
                        return MyTunesUtility.timeFormatConverter(audioHandler.getMediaPlayer().getCurrentTime().toSeconds());
                    }, // displays current time
                    player.currentTimeProperty()));

            lblSongLength.setText(MyTunesUtility.timeFormatConverter((audioHandler.getMediaPlayer().getTotalDuration().toSeconds())));
        });
    }

    /**
     * Initializes the play previous song button.
     */
    public void initPlayPrevious() {
        btnPreviousSong.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                audioHandler.restartSong();
            }
            if (event.getClickCount() == 2) {
                audioHandler.playPreviousSong();
                bindSongSlider();
            }
        });
    }

    /**
     * Move song down in playlist with a datamodel function
     */
    public void moveSongDownInPlaylist() {
        try {
            indexDataModel.moveSongDownInPlaylist(getSelectedPlaylist(),listViewPlaylistSongs.getSelectionModel().getSelectedItem());
            int selectedIndex = listViewPlaylistSongs.getSelectionModel().getSelectedIndex();
            listViewPlaylistSongs.getSelectionModel().select(selectedIndex + 1);
        } catch (SQLException e) {
        }
    }

    /**
     * Move song up in playlist with a datamodel function
     */
    public void moveSongUpInPlaylist() {
        try {
            indexDataModel.moveSongUpInPlaylist(getSelectedPlaylist(),listViewPlaylistSongs.getSelectionModel().getSelectedItem());
            int selectedIndex = listViewPlaylistSongs.getSelectionModel().getSelectedIndex();
            listViewPlaylistSongs.getSelectionModel().select(selectedIndex - 1);
        } catch (SQLException e) {
        }
    }

    /**
     *  For handling the song time slider. when being dragged
     */
    public void songStop(MouseEvent mouseEvent) { // on drag detected stops music
        audioHandler.stop();

        sliderSongTimeline.valueProperty().unbind();
    }

    public void continueSlider(MouseEvent mouseEvent) { // resume music after drag
        audioHandler.setTime(sliderSongTimeline.getValue());

        var player = audioHandler.getMediaPlayer();

        sliderSongTimeline.valueProperty().bind(Bindings.createDoubleBinding( // binds slider progress to mediaPlayer
                () -> player.getCurrentTime().toSeconds(),
                player.currentTimeProperty()));

        audioHandler.start();

        System.out.println(sliderSongTimeline.getValue());
    }

    private void showErrorAlert(String errorMsg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle("ERROR!");
        alert.setContentText(errorMsg);
        ButtonType OkButton = new ButtonType("Ok", ButtonBar.ButtonData.YES); //MakesConfirm button, with a yes Value
        alert.getButtonTypes().setAll(OkButton); //Sets buttons in window
        alert.show();
    }

    /**
     * play next song in playlist when this btn is pressed
     * @param actionEvent
     */
    public void nextSongHandler(ActionEvent actionEvent) {
        playSong();
    }
}
