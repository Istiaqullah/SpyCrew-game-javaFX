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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Set;

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

    private final int tile0Value = 0;
    private final int tile9Value = 9;
    private final int tile10Value = 10;
    private final int tile21Value = 21;
    private final int tile24Value = 24;
    private final int tile6Value = 6; // tile_6 for 2-point bonus
    private final int tileBrickBreaker = 26; // tile_26 for Brick Breaker

    private final int tileTeleport = 3;
    private final int tileMiniGame = 14;
    private final int tileKey = 23; // tile_23 is the key tile
    private final int tileMemoryGame = 25; // tile_25 is memory game
    private final int tileCarDodge = 27; // tile_27 for Car Dodge mini-game

    private Camera camera;
    private MapRender mapRenderer;

    private int playerX, playerY;
    private String currentUsername = null; // Must be set for DB update!

    private Teleportation teleportation;

    private final long moveDelayNanos = 100_000_000;
    private long lastMoveTime = 0;

    private GameController gameController = null;

    private int playerIndex = 0;

    private int points = 0;
    private boolean hasKey = false;

    // Set to store claimed tile_6 positions for this player
    private final Set<String> claimedTile6Set = new HashSet<>();

    public static void setMap(int[][] newMap) {
        map = newMap;
    }

    public void setUsername(String username) {
        this.currentUsername = username;
    }

    public void RunRun(ImageView player, AnchorPane scene, Label name, MapRender mapRenderer, Camera camera, int initialPlayerX, int initialPlayerY, GameController gameController, int playerIndex) {
        this.player = player;
        this.scene = scene;
        this.name = name;
        this.mapRenderer = mapRenderer;
        this.camera = camera;
        this.playerX = initialPlayerX;
        this.playerY = initialPlayerY;
        this.gameController = gameController;
        this.playerIndex = playerIndex;

        this.teleportation = new Teleportation(map);

        movementSetup();
        this.runningAnimation = new RunningAnimation(player);

        camera.update(playerX, playerY);
        redrawMap();

        Platform.runLater(() -> {
            if (scene != null) {
                scene.setFocusTraversable(true);
                scene.requestFocus();
            }
        });

        updatePointLabel();
        updateKeyLabel();

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

        if (val == tile0Value || val == tile9Value || val == tile10Value) {
            return false;
        }
        if ((val == tile21Value || val == tile24Value)) {
            return hasKey;
        }
        return true;
    }

    private void movementSetup() {
        if (scene == null) return;

        scene.setOnMouseClicked(e -> scene.requestFocus());

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
            launchMiniGameAndUpdatePoints();
        } else if (currentTile == tileKey && !hasKey) {
            hasKey = true;
            updateKeyLabel();
        } else if (currentTile == tileMemoryGame) {
            launchMemoryGameAndUpdatePoints();
        } else if (currentTile == tile6Value) {
            String tileKey = playerX + "," + playerY;
            if (!claimedTile6Set.contains(tileKey)) {
                claimedTile6Set.add(tileKey);
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Points Awarded");
                    alert.setHeaderText(null);
                    alert.setContentText("You got 2 points!");
                    alert.showAndWait();
                });
                points += 2;
                updatePointLabel();
                if (currentUsername != null && !currentUsername.isBlank()) {
                    updatePointsInDatabase(currentUsername, 2);
                }
            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Already Claimed");
                    alert.setHeaderText(null);
                    alert.setContentText("You already claimed this chest!");
                    alert.showAndWait();
                });
            }
        } else if (currentTile == tileBrickBreaker) {
            // Launch Brick Breaker mini-game in a new thread
            new Thread(() -> {
                try {
                    Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MiniGame3/brickbreaker-view.fxml"));
                            Parent root = loader.load();
                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Brick Breaker");
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.showAndWait();
                            // TODO: If you want to award points based on game result,
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else if (currentTile == tileCarDodge) {
            // Launch Car Dodge mini-game in a new thread
            new Thread(() -> {
                try {
                    Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MiniGame4/car-dodge-view.fxml"));
                            Parent root = loader.load();
                            Stage stage = new Stage();
                            stage.setScene(new Scene(root, 300, 600));
                            stage.setTitle("Car Dodge Game 🚗");
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.showAndWait();
                            // If you want to get score from CarDodgeController, add code here
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void launchMemoryGameAndUpdatePoints() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/MiniGame2/memory-view.fxml"));
                Parent root = loader.load();
                MiniGame2.MemoryGameController controller = loader.getController();
                if (controller != null) {
                    controller.setResultCallback(finalScore -> {
                        points += finalScore;
                        updatePointLabel();

                        if (currentUsername == null || currentUsername.isBlank()) {
                            System.out.println("Username not set — skipping points update. Points earned: " + finalScore);
                        } else {
                            updatePointsInDatabase(currentUsername, finalScore);
                        }
                    });
                }
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 600, 650));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Memory Match Game 🧠");
                stage.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
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

    private void launchMiniGameAndUpdatePoints() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/MiniGame1/TicTacToe.fxml"));
                Parent root = loader.load();
                MiniGame1.TicTacToeController controller = loader.getController();
                if (controller != null) {
                    controller.setResultCallback(result -> {
                        int earned = switch (result) {
                            case "win" -> 4;
                            case "draw" -> 2;
                            case "loss" -> 0;
                            default -> 0;
                        };
                        points += earned;
                        updatePointLabel();

                        if (currentUsername == null || currentUsername.isBlank()) {
                            System.out.println("Username not set — skipping points update. Points earned: " + earned);
                        } else {
                            updatePointsInDatabase(currentUsername, earned);
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

    private void updatePointLabel() {
        if (gameController != null) {
            gameController.setPlayerPoint(playerIndex, points);
        }
    }

    private void updateKeyLabel() {
        if (gameController != null) {
            gameController.setPlayerKey(playerIndex, hasKey);
        }
    }

    private void updatePointsInDatabase(String username, int pointsToAdd) {
        if (username == null || username.isBlank()) {
            System.out.println("updatePointsInDatabase called with null/empty username; skipping DB update.");
            return;
        }
        System.out.println("Updating points for user: " + username + ", add: " + pointsToAdd);
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