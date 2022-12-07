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
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class IndexController implements Initializable {

    private IndexDataModel indexDataModel;
    private IAudioHandler audioHandler;

    @FXML private Label lblCurrentTime;
    @FXML private Label lblTimeLength;

    @FXML private Button btnPlayPause;

    @FXML private Button btnPreviousSong;
    @FXML private Slider sliderSong;
    @FXML private Slider sliderSoundLevel;
    @FXML private ListView<PlaylistSong> listViewPlaylistSongs;
    @FXML private ListView<Playlist> listViewPlayLists;
    @FXML private TableView<Song> listViewSongs;
    @FXML private TableColumn tableColumnTitle, tableColumnArtist;
    @FXML private Label lblCurrentSelectedPlaylist;
    @FXML private TextField txtFieldSearchbar;
    @FXML private MenuItem menuQuit;
    @FXML private MenuItem menuAddSong;
    @FXML private MenuItem menuAddArtist, menuEditArtist, menuDeleteArtist;
    @FXML private MenuItem menuAddPlaylist, menuEditPlaylist;
    @FXML private ToggleButton shuffleToggleButton;

    private INextSongStrategy nextSongStrategy;

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

        initSliderSoundLevelSlider();

        initPlayPrevious();

        initShuffleToggleButton();

        lblCurrentSelectedPlaylist.textProperty().bind(
            Bindings.when(indexDataModel.getSelectedPlaylistObservable().isNull())
                .then("No playlist selected")
                .otherwise(indexDataModel.getSelectedPlaylistObservable().asString())
        );

        System.out.println("Controller initialized");
    }

    private void initShuffleToggleButton() {
        shuffleToggleButton.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            // TODO: Needs to be implemented!
            System.out.println("Old: " + oldValue);
            System.out.println("New: " + newValue);
        }));
    }

    private void initListViewPlaylists() {
        listViewPlayLists.setItems(indexDataModel.getPlaylistsObservableList());
        listViewPlayLists.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                indexDataModel.setSelectedPlaylistObservable(listViewPlayLists.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void initListViewSongs() {
        listViewSongs.setItems(indexDataModel.getSongInfoObservableList());
        tableColumnArtist.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>) param -> {
            return new SimpleStringProperty(indexDataModel.getArtistsForSong(param.getValue()));
        });

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
        /*listViewSongs.setCellFactory(lv -> {
            ListCell<Song> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();
            MenuItem editSong = new MenuItem();
            editSong.setText("Edit song");
            editSong.setOnAction(event -> indexDataModel.editSong(cell.getItem()));

            Menu addToPlaylist = new Menu("Add to playlist");


            for(Playlist p : indexDataModel.getPlaylistsObservableList()) {
                var menuItem = new MenuItem(p.getName());
                addToPlaylist.getItems().add(menuItem);
                menuItem.setOnAction(event -> indexDataModel.addSongToPlaylist(cell.getItem(), p, getSelectedPlaylist()));
            }

            contextMenu.getItems().addAll(editSong, addToPlaylist);

            cell.emptyProperty().addListener(((observable, wasEmpty, isNowEmpty) -> {
                if(isNowEmpty) cell.setContextMenu(null);
                else cell.setContextMenu(contextMenu);
            }));

            StringBinding stringBinding = new StringBinding(){
                {
                    super.bind(cell.itemProperty().asString());
                }
                @Override
                protected String computeValue() {
                    if(cell.itemProperty().getValue() == null) return "";
                    return cell.itemProperty().getValue().getTitle();
                }
            };

            cell.textProperty().bind(stringBinding);

            return cell;
        });*/
    }

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
        menuEditArtist.setOnAction(event -> {
            DropDownTextDialog<Artist> dialog = new DropDownTextDialog<>(listViewSongs.getScene().getWindow(), "Edit Artist", "New namne", indexDataModel.getArtistList());
            dialog.showAndWait().ifPresent(result ->
                    indexDataModel.editArtist(result.getFirst(), result.getSecond()));
            listViewSongs.refresh();
        });
        menuDeleteArtist.setOnAction(event -> {
            Dialog<Artist> deleteArtistDialog = new ChoiceDialog<>(null, indexDataModel.getAllArtists());
            deleteArtistDialog.setGraphic(null);
            deleteArtistDialog.setHeaderText(null);
            deleteArtistDialog.setContentText("Delete artist:");
            deleteArtistDialog.setTitle("Delete an artist");
            Optional<Artist> result = deleteArtistDialog.showAndWait();
            result.ifPresent(artist -> {
                indexDataModel.deleteArtist(artist);
            });
        });

        menuAddPlaylist.setOnAction(event -> newPlaylistHandler());
        menuEditPlaylist.setOnAction(event -> editPlaylistHandler());
    }

    private void initSliderSoundLevelSlider() {
        sliderSoundLevel.setMin(0.0d);
        sliderSoundLevel.setMax(1.0d);
        sliderSoundLevel.valueProperty().addListener((observable, oldValue, newValue) -> {
            double volume = sliderSoundLevel.getValue();
            audioHandler.changeVolume(volume);
        });
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

    private void playSong() {
        var songToPlay = nextSongStrategy.getNextSong();
        audioHandler.playSong(songToPlay);
        bindSongSlider();
        updatePlayPauseButtons();
        audioHandler.getMediaPlayer().setOnEndOfMedia(() -> {
            playSong();
        });
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
            indexDataModel.editPlaylist(selectedPlaylist, newName);
        });
    }

    public void deleteSelectedPlaylistHandler() {
        Playlist selectedPlaylist = listViewPlayLists.getSelectionModel().getSelectedItem(); //gets selected item

        if (selectedPlaylist == null){ //If selectedPlaylist is not selected, the method stops here
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

    public void editSongWindowOpen() {
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

    public void makeNewSongWindowOpen() {
        AddSongDialog dialog = new AddSongDialog(listViewSongs.getScene().getWindow());
        dialog.showAndWait().ifPresent(song -> indexDataModel.addSong(song));
    }

    public void deleteSelectedSong() {
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

    public void insertSongToPlaylist() {
        var selectedSong = listViewSongs.getSelectionModel().getSelectedItem();

        indexDataModel.addSongToPlaylist(selectedSong, getSelectedPlaylist(), getSelectedPlaylist());
    }

    private Playlist getSelectedPlaylist() {
        return indexDataModel.getSelectedPlaylistObservable().getValue();
    }

    public void searchForSong() {
        indexDataModel.searchForSong(txtFieldSearchbar.getText());
    }

    public void playOrPauseSong() {
        audioHandler.playPause();
        updatePlayPauseButtons();
    }

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
            sliderSong.maxProperty().bind(Bindings.createDoubleBinding( //sets  Song Leangthh
                    () -> player.getTotalDuration().toSeconds(),
                    player.totalDurationProperty()));

            sliderSong.valueProperty().bind(Bindings.createDoubleBinding( //Binds slider progress tto mediaPlayer
                    () -> player.getCurrentTime().toSeconds(),
                    player.currentTimeProperty()));

            lblCurrentTime.textProperty().bind(Bindings.createStringBinding(
                    () -> MyTunesUtility.timeFormatConverter(audioHandler.getMediaPlayer().getCurrentTime().toSeconds()), //Displays current time
                    player.currentTimeProperty()));

            lblTimeLength.setText(MyTunesUtility.timeFormatConverter((audioHandler.getMediaPlayer().getTotalDuration().toSeconds())));
        });
    }

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

    public void moveSongDownInPlaylist(ActionEvent actionEvent) {
    }

    public void moveSongUpInPlaylist(ActionEvent actionEvent) {
    }

    public void songStop(MouseEvent mouseEvent) { //On drag detected stops music
        audioHandler.stop();

        sliderSong.valueProperty().unbind();

        audioHandler.setTime(sliderSong.getValue());
    }

    public void continueSlider(MouseEvent mouseEvent) { //resume music after drag
        audioHandler.getMediaPlayer().setOnReady(() -> {
            var player = audioHandler.getMediaPlayer();
            sliderSong.maxProperty().bind(Bindings.createDoubleBinding( //sets  Song Leangthh
                    () -> player.getTotalDuration().toSeconds(),
                    player.totalDurationProperty()));

            sliderSong.valueProperty().bind(Bindings.createDoubleBinding( //Binds slider progress tto mediaPlayer
                    () -> player.getCurrentTime().toSeconds(),
                    player.currentTimeProperty()));
        });

        audioHandler.start();
    }
}
