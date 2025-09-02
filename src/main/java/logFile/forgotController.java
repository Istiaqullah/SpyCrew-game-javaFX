package logFile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class forgotController {

    @FXML
    void logIn(ActionEvent event)
    {
        utils.changeScene(event,"login.fxml",null,null);

    }




}
