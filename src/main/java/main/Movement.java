package main;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Handles player movement, camera updates, map redrawing, and interactions (teleportation / minigame / key pickup).
 */
public class Movement {

    private final BooleanProperty wPressed = new SimpleBooleanProperty();
    private final BooleanProperty aPressed = new SimpleBooleanProperty();
    private final BooleanProperty sPressed = new SimpleBooleanProperty();
    private final BooleanProperty dPressed = new SimpleBooleanProperty();

    private RunningAnimation runningAnimation;

    @FXML
    private ImageView player;

    @FXML
    private AnchorPane scene;
    @FXML
    private Label name;

    private static int[][] map;
    private final int defaultTileSize = 32;

    // Values representing impassable tiles (from your original code)
    private final int tile0Value = 0;
    private final int tile9Value = 9;
    private final int tile10Value = 10;
    private final int tile21Value = 21;

    private final int tileTeleport = 3;
    private final int tileMiniGame = 14;
    private final int tileKey = 23; // tile_23 is the key tile

    private Camera camera;
    private MapRender mapRenderer;

    private int playerX, playerY;
    private String currentUsername = null; // Can be set from outside via setUsername(...)

    private Teleportation teleportation;

    private final long moveDelayNanos = 100_000_000;
    private long lastMoveTime = 0;

    // Reference to GameController so we can notify it of key pickup
    private GameController gameController = null;

    public static void setMap(int[][] newMap) {
        map = newMap;
    }

    public void setUsername(String username) {
        this.currentUsername = username;
    }

    /**
     * Run the movement logic and wire input handlers.
     * Note: added last parameter GameController to allow notifying the controller about pickups.
     */
    public void RunRun(ImageView player, AnchorPane scene, Label name, MapRender mapRenderer, Camera camera, int initialPlayerX, int initialPlayerY, GameController gameController) {
        this.player = player;
        this.scene = scene;
        this.name = name;
        this.mapRenderer = mapRenderer;
        this.camera = camera;
        this.playerX = initialPlayerX;
        this.playerY = initialPlayerY;
        this.gameController = gameController;

        this.teleportation = new Teleportation(map);

        movementSetup();
        this.runningAnimation = new RunningAnimation(player);

        camera.update(playerX, playerY);
        redrawMap();

        // Ensure the anchor pane can receive keyboard focus so it receives key events
        Platform.runLater(() -> {
            if (scene != null) {
                scene.setFocusTraversable(true);
                scene.requestFocus();
            }
        });

        movementTimer.start();
    }

    private void redrawMap() {
        if (mapRenderer == null || scene == null || player == null) return;
        Group group = mapRenderer.renderMap(map, camera, playerX, playerY, player, name);
        scene.getChildren().clear();
        scene.getChildren().add(group);
    }

    private boolean canMove(int x, int y) {
        if (map == null) return false;
        if (x < 0 || y < 0 || y >= map.length || x >= map[0].length) return false;
        int val = map[y][x];
        return val != tile0Value && val != tile9Value && val != tile10Value && val != tile21Value;
    }

    private void movementSetup() {
        if (scene == null) return;

        // Ensure mouse click will focus the pane so key events deliver
        scene.setOnMouseClicked(e -> {
            scene.requestFocus();
        });

        scene.setOnKeyPressed(e -> {
            boolean wasMoving = wPressed.get() || aPressed.get() || sPressed.get() || dPressed.get();

            KeyCode code = e.getCode();
            if (code == KeyCode.W) wPressed.set(true);
            if (code == KeyCode.A) aPressed.set(true);
            if (code == KeyCode.S) sPressed.set(true);
            if (code == KeyCode.D) dPressed.set(true);

            if (!wasMoving && (wPressed.get() || aPressed.get() || sPressed.get() || dPressed.get())) {
                if (runningAnimation != null) runningAnimation.startAnimation();
            }

            // Interaction key (E) handling: teleportation, minigame, or key pickup depending on tile
            if (code == KeyCode.E) {
                handleInteraction();
            }
        });

        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.W) wPressed.set(false);
            if (code == KeyCode.A) aPressed.set(false);
            if (code == KeyCode.S) sPressed.set(false);
            if (code == KeyCode.D) dPressed.set(false);

            if (!wPressed.get() && !aPressed.get() && !sPressed.get() && !dPressed.get()) {
                if (runningAnimation != null) runningAnimation.stopAnimation();
            }
        });
    }

    private void handleInteraction() {
        if (map == null) return;
        if (playerY < 0 || playerY >= map.length || playerX < 0 || playerX >= map[0].length) return;

        int currentTile = map[playerY][playerX];

        if (currentTile == tileTeleport) {
            // Teleportation: E pressed while standing on tile 3
            if (teleportation != null && teleportation.isOnTile3(playerX, playerY)) {
                int[] newPos = teleportation.getRandomTeleportPosition(playerX, playerY);
                if (newPos != null) {
                    playerX = newPos[0];
                    playerY = newPos[1];
                    if (camera != null) camera.update(playerX, playerY);
                    redrawMap();
                }
            }
        } else if (currentTile == tileMiniGame) {
            // Launch mini-game and update points on result
            launchMiniGameAndUpdatePoints();
        } else if (currentTile == tileKey) {
            // Player picks up a key on tile_23
            if (gameController != null) {
                gameController.setKey(true);
            }
        }
    }

    private final AnimationTimer movementTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (!(wPressed.get() || aPressed.get() || sPressed.get() || dPressed.get())) {
                lastMoveTime = now;
                return;
            }
            if (now - lastMoveTime < moveDelayNanos) {
                return;
            }
            lastMoveTime = now;

            boolean moved = false;
            if (wPressed.get()) {
                if (canMove(playerX, playerY - 1)) {
                    playerY--;
                    moved = true;
                }
                if (runningAnimation != null) runningAnimation.setDirection(KeyCode.W);
            } else if (sPressed.get()) {
                if (canMove(playerX, playerY + 1)) {
                    playerY++;
                    moved = true;
                }
                if (runningAnimation != null) runningAnimation.setDirection(KeyCode.S);
            } else if (aPressed.get()) {
                if (canMove(playerX - 1, playerY)) {
                    playerX--;
                    moved = true;
                }
                if (runningAnimation != null) runningAnimation.setDirection(KeyCode.A);
            } else if (dPressed.get()) {
                if (canMove(playerX + 1, playerY)) {
                    playerX++;
                    moved = true;
                }
                if (runningAnimation != null) runningAnimation.setDirection(KeyCode.D);
            }

            if (moved) {
                if (camera != null) camera.update(playerX, playerY);
                redrawMap();
            }
        }
    };

    // ---- MiniGame Integration ----
    private void launchMiniGameAndUpdatePoints() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/MiniGame1/TicTacToe.fxml"));
                Parent root = loader.load();
                MiniGame1.TicTacToeController controller = loader.getController();
                if (controller != null) {
                    controller.setResultCallback(result -> {
                        int points = switch (result) {
                            case "win" -> 4;
                            case "draw" -> 2;
                            case "loss" -> 0;
                            default -> 0;
                        };
                        // Only update DB if username is set
                        if (currentUsername == null || currentUsername.isBlank()) {
                            System.out.println("Username not set — skipping points update. Points earned: " + points);
                        } else {
                            updatePointsInDatabase(currentUsername, points);
                        }
                    });
                }

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Tic Tac Toe");
                stage.showAndWait();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void updatePointsInDatabase(String username, int pointsToAdd) {
        if (username == null || username.isBlank()) {
            System.out.println("updatePointsInDatabase called with null/empty username; skipping DB update.");
            return;
        }
        System.out.println("Updating points for user: " + username + ", add: " + pointsToAdd);
        // NOTE: Credentials are hardcoded here (as in original). Consider moving to config.
        String url = "jdbc:mysql://localhost:3306/spycrew";
        String user = "root";
        String pass = "Hasnat";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = conn.prepareStatement("UPDATE users SET score = score + ? WHERE username = ?")) {
            ps.setInt(1, pointsToAdd);
            ps.setString(2, username);
            int affected = ps.executeUpdate();
            System.out.println("Database update affected rows: " + affected);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}