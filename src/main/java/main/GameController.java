package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML
    public AnchorPane root;

    @FXML Label point1, key1, name1;
    @FXML ImageView image1;

    @FXML Label point2, key2, name2;
    @FXML ImageView image2;

    @FXML Label point3, key3, name3;
    @FXML ImageView image3;

    @FXML Label point4, key4, name4;
    @FXML ImageView image4;

    // Player state
    private List<String> playerNames;
    private int[] points = new int[4];
    private boolean[] keys = new boolean[4];

    // Movement instances for each player
    private Movement[] movements = new Movement[4];

    // Call this before showing the game scene
    public void setPlayerNames(List<String> names) {
        this.playerNames = names;
        updateLabels();
    }

    // Call this whenever points or key status change
    public void setPlayerPoint(int playerIndex, int point) {
        if (playerIndex >= 0 && playerIndex < points.length) {
            points[playerIndex] = point;
            updateLabels();
        }
    }

    public void setPlayerKey(int playerIndex, boolean hasKey) {
        if (playerIndex >= 0 && playerIndex < keys.length) {
            keys[playerIndex] = hasKey;
            updateLabels();
        }
    }

    private void updateLabels() {
        if (name1 != null) name1.setText((playerNames != null && playerNames.size() > 0 && playerNames.get(0) != null) ? playerNames.get(0) : "Hasnat");
        if (name2 != null) name2.setText((playerNames != null && playerNames.size() > 1 && playerNames.get(1) != null) ? playerNames.get(1) : "Hasnat");
        if (name3 != null) name3.setText((playerNames != null && playerNames.size() > 2 && playerNames.get(2) != null) ? playerNames.get(2) : "Hasnat");
        if (name4 != null) name4.setText((playerNames != null && playerNames.size() > 3 && playerNames.get(3) != null) ? playerNames.get(3) : "Hasnat");

        if (point1 != null) point1.setText("Point: " + points[0]);
        if (key1 != null) key1.setText("Key: " + (keys[0] ? "Yes" : "No"));
        if (point2 != null) point2.setText("Point: " + points[1]);
        if (key2 != null) key2.setText("Key: " + (keys[1] ? "Yes" : "No"));
        if (point3 != null) point3.setText("Point: " + points[2]);
        if (key3 != null) key3.setText("Key: " + (keys[2] ? "Yes" : "No"));
        if (point4 != null) point4.setText("Point: " + points[3]);
        if (key4 != null) key4.setText("Key: " + (keys[3] ? "Yes" : "No"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MapRender mapRender = new MapRender();
        int[][] map;
        try {
            map = mapUtil.fetchMapFromDB();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load map from DB", e);
        }
        Movement.setMap(map);

        int cameraWidth = 12;
        int cameraHeight = 12;
        int tileSize = MapRender.TILE_SIZE;

        int initialPlayerX = map[0].length / 2;
        int initialPlayerY = map.length / 2;

        updateLabels(); // Set labels if playerNames already set

        // Initialize movement for 4 players (can be adapted for fewer/more players)
        movements[0] = new Movement();
        Camera camera1 = new Camera(cameraWidth, cameraHeight, map[0].length, map.length, tileSize);
        movements[0].RunRun(image1, root, name1, mapRender, camera1, initialPlayerX, initialPlayerY, this, 0);
        // SET THE USERNAME FOR PLAYER 1!
        movements[0].setUsername("player1_username");

        movements[1] = new Movement();
        Camera camera2 = new Camera(cameraWidth, cameraHeight, map[0].length, map.length, tileSize);
        movements[1].RunRun(image2, root, name2, mapRender, camera2, initialPlayerX, initialPlayerY, this, 1);
        // SET THE USERNAME FOR PLAYER 2!
        movements[1].setUsername("player2_username");

        movements[2] = new Movement();
        Camera camera3 = new Camera(cameraWidth, cameraHeight, map[0].length, map.length, tileSize);
        movements[2].RunRun(image3, root, name3, mapRender, camera3, initialPlayerX, initialPlayerY, this, 2);
        // SET THE USERNAME FOR PLAYER 3!
        movements[2].setUsername("player3_username");

        movements[3] = new Movement();
        Camera camera4 = new Camera(cameraWidth, cameraHeight, map[0].length, map.length, tileSize);
        movements[3].RunRun(image4, root, name4, mapRender, camera4, initialPlayerX, initialPlayerY, this, 3);
        // SET THE USERNAME FOR PLAYER 4!
        movements[3].setUsername("player4_username");
    }
}

