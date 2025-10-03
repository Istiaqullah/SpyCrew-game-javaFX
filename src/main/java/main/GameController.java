package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    public AnchorPane root;
    @FXML
    ImageView image;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MapRender mapRender = new MapRender();
        int[][] map= null;
        try {
            map = mapRender.loadMap("src/main/resources/map.txt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Movement.setMap(map);
        Group mapGroup= mapRender.renderMap(map);
        root.getChildren().add(0,mapGroup);
       // root.getChildren().add(image);
        Movement movement = new Movement();
        movement.makeRunnerMovable(image, root);
    }
}
