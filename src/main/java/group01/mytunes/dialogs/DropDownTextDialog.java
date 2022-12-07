package group01.mytunes.dialogs;

import group01.mytunes.utility.Tuple;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

public class DropDownTextDialog<T> extends Dialog<Tuple<T,String>>{
    @FXML
    private ComboBox<T> tComboBox;

    @FXML
    private TextField inputTextField;

    /**
     * Makes a new window wtih a comboox and textField.
     * comboBox can be any list any value.
     * returns the selected item from combo box and the value of the textField
     * @param owner they owner window
     * @param title the given tittle of the window
     * @param text the given display tekst
     * @param obList the given list of values for the comboBOx of any type
     */
    public DropDownTextDialog(Window owner, String title , String text, List<T> obList) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../DropDownTextDialog.fxml"));
            loader.setController(this);

            DialogPane pane = loader.load();

           inputTextField.setText(text);

            initOwner(owner);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(false);
            setTitle(title);
            setDialogPane(pane);

            tComboBox.setItems(FXCollections.observableArrayList(obList));

            setResultConverter(buttonType -> {
                if(inputTextField.getText().isEmpty()) return null;
                if(tComboBox.getItems().isEmpty()) return null;

                return new Tuple<>(tComboBox.getValue(), inputTextField.getText());
            });

            setOnShowing(dialogEvent -> Platform.runLater(() -> inputTextField.requestFocus()));

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
