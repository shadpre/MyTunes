package group01.mytunes.dialogs;

import group01.mytunes.Models.Artist;
import group01.mytunes.Models.Song;
import group01.mytunes.dao.ArtistDatabaseDAO;
import group01.mytunes.dao.interfaces.IArtistDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class NewSongDialog extends Dialog<Song> {

        private File selectedSongFile;

        @FXML
        private ComboBox<Artist> artistPicker;

        @FXML
        private Button filePickerButton;

        @FXML
        private TextField titleTextField;

        private IArtistDAO artistDAO = new ArtistDatabaseDAO();

        public NewSongDialog(Window owner) {
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
                        System.out.println(selectedFile.getAbsoluteFile());
                    }
                });

                List<Artist> artists = artistDAO.getArtists();
                artistPicker.setItems(FXCollections.observableArrayList(artists));

                initOwner(owner);
                initModality(Modality.APPLICATION_MODAL);

                setResizable(false);
                setTitle("Add song");
                setDialogPane(pane);

                setResultConverter(buttonType -> {
                    if(selectedSongFile == null) return null;
                    try {
                        byte[] fileData = Files.readAllBytes(selectedSongFile.toPath());
                        return new Song( 1, titleTextField.getText(), fileData, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                setOnShowing(dialogEvent -> Platform.runLater(() -> titleTextField.requestFocus()));

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
}
