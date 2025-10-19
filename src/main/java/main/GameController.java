package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML
    private Label points;
    @FXML
    private Label haveKey;
    @FXML
    public AnchorPane root;
    @FXML
    ImageView image1;
    @FXML
    Label name1;
    @FXML
    ImageView image2;
    @FXML
    Label name2;
    @FXML
    ImageView image3;
    @FXML
    Label name3;
    @FXML
    ImageView image4;
    @FXML
    Label name4;

    private int point = 0;

    // Store player names
    private List<String> playerNames;

    // Key flag
    private boolean key = false;

    // Call this before showing the game scene
    public void setPlayerNames(List<String> names) {
        this.playerNames = names;
        updateLabels();
    }

    private void updateLabels() {
        // For each label: if the label exists, set it either to the provided player name
        // or to the default "BOOT" when a name is not available.
        if (name1 != null) {
            if (playerNames != null && playerNames.size() > 0 && playerNames.get(0) != null) {
                name1.setText(playerNames.get(0));
            } else {
                name1.setText("BOOT");
            }
        }

        if (name2 != null) {
            if (playerNames != null && playerNames.size() > 1 && playerNames.get(1) != null) {
                name2.setText(playerNames.get(1));
            } else {
                name2.setText("BOOT");
            }
        }

        if (name3 != null) {
            if (playerNames != null && playerNames.size() > 2 && playerNames.get(2) != null) {
                name3.setText(playerNames.get(2));
            } else {
                name3.setText("BOOT");
            }
        }

        if (name4 != null) {
            if (playerNames != null && playerNames.size() > 3 && playerNames.get(3) != null) {
                name4.setText(playerNames.get(3));
            } else {
                name4.setText("BOOT");
            }
        }
    }

    /**
     * Set or clear the key flag and update the haveKey label.
     * This method can be called from Movement when the player picks up the key.
     */
    public void setKey(boolean value) {
        this.key = value;
        if (haveKey != null) {
            haveKey.setText("Have key: " + (value ? "true" : "false"));
        }
    }

    public boolean hasKey() {
        return key;
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
        Camera camera = new Camera(cameraWidth, cameraHeight, map[0].length, map.length, tileSize);

        int initialPlayerX = map[0].length / 2;
        int initialPlayerY = map.length / 2;

        updateLabels(); // Set labels if playerNames already set

        // Ensure haveKey label starts as false
        if (haveKey != null) haveKey.setText("Have key: false");

        Movement movement = new Movement();
        // Pass 'this' so Movement can notify the controller when the key is picked up
        movement.RunRun(image1, root, name1, mapRender, camera, initialPlayerX, initialPlayerY, this);
    }
}