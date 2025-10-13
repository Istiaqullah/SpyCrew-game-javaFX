package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    public AnchorPane root;
    @FXML
    ImageView image;
    @FXML
    Label name;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MapRender MapRender = new MapRender();
        int[][] map;
        try {
           map = mapUtil.fetchMapFromDB();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Movement.setMap(map);

        int cameraWidth = 12;
        int cameraHeight = 12;
        int tileSize = MapRender.TILE_SIZE;
        Camera camera = new Camera(cameraWidth, cameraHeight, map[0].length, map.length, tileSize);

        int initialPlayerX = map[0].length / 2;
        int initialPlayerY = map.length / 2;

        name.setText("Hasnat");

        Movement Movement = new Movement();
        Movement.RunRun(image, root, name, MapRender, camera, initialPlayerX, initialPlayerY);
    }
}