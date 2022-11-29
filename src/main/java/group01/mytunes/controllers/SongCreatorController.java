package group01.mytunes.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SongCreatorController  implements Initializable {
    @FXML private TextField txtFieldPath;
    @FXML private TextField txtFieldTittle;
    @FXML private TextField txtFieldTime;
    @FXML private ComboBox ComboBoxCategory;
    @FXML private ComboBox comboBoxArtist;
    @FXML private Button btnChoseFilepath;
    @FXML private Button btnAddArtist;
    @FXML private Button btnAddCategory;
    @FXML private Button btnCancel;
    @FXML private Button btnSaveSong;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void displayError(Throwable t)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("!!ERROR!!");
        alert.setHeaderText("Something went wrong, \n ERROR:      " + t.getMessage());
        alert.showAndWait();
    }


    public void openCreateArtistWindow(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../CreateArtist.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    public void OpenCreateCategoryWindow(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../CreateCategory.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    public void CloseWindowAndCancelChanges(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void chooseFile(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select song");
        fileChooser.getExtensionFilters().add(
                //new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));
        Stage stage = (Stage) btnChoseFilepath.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            txtFieldPath.setText(String.valueOf(selectedFile));
            String time = calculateTimer((int) selectedFile.length());
            txtFieldTime.setText(time);
        }
    }

    private String calculateTimer(int time){
        int h = 0;
        int m = 0;
        int s = 0;

        while (time > 0) {
            if (time >= 60) {
                time -= 60;
                m++;
            } else if (m >= 60) {
                m -= 60;
                h++;
            } else {
                s += time;
                time -= time;
            }
        }

        return h + ":" + m + ":" + s;

    }

    public void saveSong(ActionEvent actionEvent) {
        String tittle = txtFieldTittle.getText();
        String artist = comboBoxArtist.getPromptText();
        String category = ComboBoxCategory.getPromptText();
        String time = txtFieldTime.getText();
        String path = txtFieldPath.getText();

        System.out.println(tittle + artist + category + time + path);
    }

}
