package logFile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static logFile.utils.changeScene;


public class loginController {
    @FXML
    TextField username;
    @FXML
    PasswordField password;
    @FXML
    Label massage;



@FXML
void setBtn_create(ActionEvent event) {
    utils.changeScene(event,"signup.fxml",null,null);
}
@FXML
void setBtn_forgot(ActionEvent event) {
    utils.changeScene(event,"forgot.fxml",null,null);
}

@FXML
void setBtn_login(ActionEvent event) {

    }
}
