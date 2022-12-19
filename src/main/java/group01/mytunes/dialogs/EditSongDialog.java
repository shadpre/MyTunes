package group01.mytunes.dialogs;

import group01.mytunes.entities.Album;
import group01.mytunes.entities.Artist;
import group01.mytunes.entities.Song;
import group01.mytunes.utility.Triple;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

public class EditSongDialog extends Dialog<Triple<String, Artist, Album>> {

    @FXML
    private ButtonType cancelButton;

    @FXML
    private TextField titleTextField;

    @FXML
    private ComboBox<Artist> artistPicker;

    @FXML
    private ComboBox<Album> albumPicker;

    /**
     * Constructor of the edit song dialog window.
     *
     * Returns new the new values for the song.
     *
     * @param owner Parent window.
     * @param selectedData Song, Artists, Albums.
     * @param artists List of available artists.
     * @param albums List of available albums.
     */
    public EditSongDialog(Window owner, Triple<Song, List<Integer>, List<Integer>> selectedData, List<Artist> artists, List<Album> albums) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../EditSongDialog.fxml"));
            loader.setController(this);

            DialogPane pane = loader.load();

            initOwner(owner);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(false);
            setTitle("Edit song");
            setDialogPane(pane);

            /*
                Set data fields
             */

            // Title text field
            titleTextField.setText(selectedData.getFirst().getTitle());

            // Artist picker
            artistPicker.setItems(FXCollections.observableArrayList(artists));
            if(selectedData.getSecond().size() > 0) {
                var selectedArtist =  artists.stream().filter(artist -> artist.getId() == selectedData.getSecond().get(0)).findFirst();
                selectedArtist.ifPresent(artist -> artistPicker.setValue(artist));
            }

            // Album picker
            albumPicker.setItems(FXCollections.observableArrayList(albums));
            if(selectedData.getThird().size() > 0) {
                var selectedAlbum =  albums.stream().filter(album -> album.getId() == selectedData.getThird().get(0)).findFirst();
                selectedAlbum.ifPresent(album -> albumPicker.setValue(album));
            }

            // When ok / cancel button is pressed
            setResultConverter(buttonType -> {
                if (buttonType.equals(cancelButton)) return null;

                return new Triple<>(
                    titleTextField.getText(), // new title
                    artistPicker.getValue(), // new artist
                    albumPicker.getValue()  // new album
                );
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
