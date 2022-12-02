package group01.mytunes.controllers;

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
import group01.mytunes.utility.MyTunesUtility;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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
    @FXML private ListView<Song> listViewSongs;
    @FXML private Label labelSongPlaying;
    @FXML private Label lblCurrentSelectedPlaylist;
    @FXML private TextField txtFieldSearchbar;
    @FXML private MenuItem menuQuit;
    @FXML private MenuItem menuAddSong;

    @FXML private MenuItem menuAddArtist, menuEditArtist, menuDeleteArtist;
    @FXML private MenuItem menuAddPlaylist, menuEditPlaylist;

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

        lblCurrentSelectedPlaylist.textProperty().bind(
            Bindings.when(indexDataModel.getSelectedPlaylistObservable().isNull())
                .then("No playlist selected")
                .otherwise(indexDataModel.getSelectedPlaylistObservable().asString())
        );

        System.out.println("Controller initialized");
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
        listViewSongs.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                playSong(listViewSongs.getSelectionModel().getSelectedItem());
            }
        });
        listViewSongs.setCellFactory(lv -> {
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
        });
    }

    private void initListViewPlaylistSong()  {
        listViewPlaylistSongs.setItems(indexDataModel.getSongPlaylistInfoObservableList());
        listViewPlaylistSongs.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                playSong(listViewPlaylistSongs.getSelectionModel().getSelectedItem().getSong());
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
        menuEditArtist.setOnAction(event -> indexDataModel.editArtist());
        menuDeleteArtist.setOnAction(event -> indexDataModel.deleteArtist());

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

    private void playSong(Song song) {
        audioHandler.playSong(song);
        bindSongSlider();
        updatePlayPauseButtons();
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

    public void deleteSelectedPlaylistHandler() {
        Playlist selectedPlaylist = listViewPlayLists.getSelectionModel().getSelectedItem();
        if(selectedPlaylist != null) indexDataModel.deletePlaylist(selectedPlaylist);
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
            System.out.println(selectedSong + " " + newName);
            indexDataModel.editSong(selectedSong, newName);
        });
    }

    public void makeNewSongWindowOpen() {
        AddSongDialog dialog = new AddSongDialog(listViewSongs.getScene().getWindow());
        dialog.showAndWait().ifPresent(song -> indexDataModel.addSong(song));
    }

    public void insertSongToPlaylist() {
        var selectedSong = listViewSongs.getSelectionModel().getSelectedItem();

        indexDataModel.addSongToPlaylist(selectedSong, getSelectedPlaylist(), getSelectedPlaylist());
    }

    private Playlist getSelectedPlaylist() {
        return indexDataModel.getSelectedPlaylistObservable().getValue();
    }

    public void searchForSong() {
        listViewSongs.setItems(indexDataModel.getSongInfoObservableList());
        try {
            indexDataModel.searchForSong(txtFieldSearchbar.getText().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void playOrPauseSong() {
        audioHandler.playPause();
        updatePlayPauseButtons();
    }

    private void updatePlayPauseButtons() {
        if (audioHandler.isPlaying()) {
            labelSongPlaying.setText("Playing");
            btnPlayPause.setText("II");
        } else {
            labelSongPlaying.setText("Not playing");
            btnPlayPause.setText("⪢");
        }
    }

    /**
     * Binds the data to the song slider.
     */
    private void bindSongSlider() {
        audioHandler.getMediaPlayer().setOnReady(() -> {
            var player = audioHandler.getMediaPlayer();
            sliderSong.maxProperty().bind(Bindings.createDoubleBinding(
                    () -> player.getTotalDuration().toSeconds(),
                    player.totalDurationProperty()));

            sliderSong.valueProperty().bind(Bindings.createDoubleBinding(
                    () -> player.getCurrentTime().toSeconds(),
                    player.currentTimeProperty()));

            lblCurrentTime.textProperty().bind(Bindings.createStringBinding(
                    () -> MyTunesUtility.timeFormatConverter(audioHandler.getMediaPlayer().getCurrentTime().toSeconds()),
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
            }
        });


    }
}
