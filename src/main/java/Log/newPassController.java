package Log;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class newPassController {
    @FXML
    void logIn(ActionEvent event)
    {
        utils.changeScene(event,"login.fxml",null,null);

    }
}
