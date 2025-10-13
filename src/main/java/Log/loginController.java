package Log;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;


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
void setBtn_verify(ActionEvent event) {
    utils.changeScene(event,"Verification.fxml",null,null);
}
@FXML
void setBtn_login(ActionEvent event) throws SQLException {
    if(username.getText().trim().isEmpty() || password.getText().trim().isEmpty()) {
        System.out.println("Please fill all the fields");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Please fill all the fields.");
        alert.show();
    }
    else
     utils.login(event,username.getText(),password.getText());

    }
}
