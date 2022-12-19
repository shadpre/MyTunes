package group01.mytunes.dialogs;

import group01.mytunes.entities.Album;
import group01.mytunes.entities.Artist;
import group01.mytunes.entities.Song;
import group01.mytunes.dao.AlbumDatabaseDAO;
import group01.mytunes.dao.ArtistDatabaseDAO;
import group01.mytunes.dao.interfaces.IAlbumDAO;
import group01.mytunes.dao.interfaces.IArtistDAO;
import group01.mytunes.utility.Triple;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AddSongDialog extends Dialog<Triple<Song, Artist, Album>> {

    private File selectedSongFile;

    @FXML
    private ComboBox<Artist> artistPicker;

    @FXML
    private ComboBox<Album> albumPicker;

    @FXML
    private Button filePickerButton;

    @FXML
    private ButtonType cancelButton;

    @FXML
    private TextField titleTextField;

    @FXML
    private Label lblFieldFilePath;

    private IArtistDAO artistDAO;
    private IAlbumDAO albumDAO;
    private ObservableList<Artist> artistObservableList;
    private ObservableList<Album> albumObservableList;

    private int durationInSeconds = 0;

    public AddSongDialog(Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../AddSongDialog.fxml"));
            loader.setController(this);

            DialogPane pane = loader.load();

            filePickerButton.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select song");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));
                File selectedFile = fileChooser.showOpenDialog(owner);

                if (selectedFile != null) {
                    selectedSongFile = selectedFile;
                    lblFieldFilePath.setText(String.valueOf(selectedFile));
                }

                if(titleTextField.getText().isEmpty()) {
                    String fileName = selectedFile.getName();
                    titleTextField.setText(fileName.substring(0, fileName.lastIndexOf('.')));
                }

                Media media = new Media(selectedSongFile.getAbsoluteFile().toURI().toString());
                MediaPlayer mp = new MediaPlayer(media);

                mp.setOnReady(() -> durationInSeconds = (int) (Math.round(mp.getTotalDuration().toSeconds())));
            });


            artistDAO = new ArtistDatabaseDAO();
            albumDAO = new AlbumDatabaseDAO();

            artistObservableList = FXCollections.observableArrayList(artistDAO.getArtists());
            albumObservableList = FXCollections.observableArrayList(albumDAO.getAlbums());

            artistPicker.setItems(artistObservableList);
            albumPicker.setItems(albumObservableList);

            initOwner(owner);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(false);
            setTitle("Add song");
            setDialogPane(pane);

            setResultConverter(buttonType -> {
                if(buttonType.equals(cancelButton)) return null;
                if(selectedSongFile == null) return null;
                if(titleTextField.getText().isEmpty()) return null;
                if(artistPicker.getItems().isEmpty()) return null;
                byte[] data;
                try {
                    data = Files.readAllBytes(selectedSongFile.getAbsoluteFile().toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if(data == null) return null;

                return new Triple<>(
                    new Song( -1, titleTextField.getText(), data, durationInSeconds),
                    artistPicker.getValue(),
                    albumPicker.getValue()
                );
            });

            setOnShowing(dialogEvent -> Platform.runLater(() -> titleTextField.requestFocus()));

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
