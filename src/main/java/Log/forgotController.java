package Log;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class forgotController {
    @FXML
    private TextField mail; // fx:id="mail" in FXML
    @FXML
    private TextField newPassword; // fx:id="newPassword" in FXML

    @FXML
    void logIn(ActionEvent event) {
        utils.changeScene(event, "login.fxml", null, null);
    }

    @FXML
    void submit(ActionEvent event) {
        String email = mail.getText();
        String password = newPassword.getText();
        if (email.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please fill all the fields.");
            alert.show();
        } else {
            try {
                utils.forgot(event, email, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}