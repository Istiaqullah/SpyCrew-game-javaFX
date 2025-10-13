package Log;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;


public class signupController {
    @FXML
    TextField username;
    @FXML
    PasswordField password;
    @FXML
    TextField email;
    @FXML
    Label massage;
@FXML
    void logIn(ActionEvent event)
    {
        utils.changeScene(event,"login.fxml",null,null);

    }

    @FXML
    void signUp(ActionEvent event)
    {
       if(username.getText().isEmpty() || email.getText().isEmpty() || password.getText().isEmpty())
       {
           System.out.println("Please fill all the fields");
           massage.setText("Please fill all the fields");
       }
       else
       {
           try {
               utils.signup(event,username.getText(),email.getText(),password.getText());
           } catch (Exception e) {
               throw new RuntimeException(e);
           }
       }
    }
}
