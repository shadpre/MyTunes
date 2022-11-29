package group01.mytunes.controllers;

import group01.mytunes.dao.PlaylistDatabaseDAO;
import group01.mytunes.dao.interfaces.IPlaylistDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NewPlaylistController implements Initializable {

    private IPlaylistDAO playlistDAO;

    @FXML
    public Button btnCancel;

    @FXML
    public Button btnSave;

    @FXML
    public TextField txtFieldPlaylistName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.playlistDAO = new PlaylistDatabaseDAO();
    }

    public void handleSaveClick() {
        String playlistName = txtFieldPlaylistName.getText();

        if(playlistName.isEmpty()) return;

        playlistDAO.createPlaylist(playlistName.trim());

        closeWindow();
    }

    public void handleCancelClick() {
        System.out.println("Cancel");
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtFieldPlaylistName.getScene().getWindow();
        stage.close();
    }
}
