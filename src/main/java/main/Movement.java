package main;
import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Group;

public class Movement {

    private BooleanProperty wPressed = new SimpleBooleanProperty();
    private BooleanProperty aPressed = new SimpleBooleanProperty();
    private BooleanProperty sPressed = new SimpleBooleanProperty();
    private BooleanProperty dPressed = new SimpleBooleanProperty();

    private RunningAnimation runningAnimation;

    @FXML
    private ImageView player;

    @FXML
    private AnchorPane scene;
    @FXML
    private Label name;
    private static int[][] map;
    private final int tileSize = 32; // Or whatever your scale is

    private int tile0Value = 0;
    private int tile9Value = 9;
    private int tile10Value = 10;
    private int tile21Value = 21;

    private Camera camera;
    private MapRender mapRenderer;

    private int playerX, playerY; // in tile coordinates

    // Teleportation support
    private Teleportation teleportation;

    // For hold-to-move speed control
    private final long moveDelayNanos = 100_000_000; // 200ms = 5 tiles/sec. Adjust for speed.
    private long lastMoveTime = 0;

    public static void setMap(int[][] map) {
        Movement.map = map;
    }

    public void RunRun(ImageView player, AnchorPane scene, Label name, MapRender mapRenderer, Camera camera, int initialPlayerX, int initialPlayerY) {
        this.player = player;
        this.scene = scene;
        this.name = name;
        this.mapRenderer = mapRenderer;
        this.camera = camera;
        this.playerX = initialPlayerX;
        this.playerY = initialPlayerY;

        // Teleportation setup
        this.teleportation = new Teleportation(map);

        movementSetup();
        runningAnimation = new RunningAnimation(player);

        camera.update(playerX, playerY);
        redrawMap();

        movementTimer.start();
    }

    private void redrawMap() {
        Group group = mapRenderer.renderMap(map, camera, playerX, playerY, player, name);
        scene.getChildren().clear();
        scene.getChildren().add(group);
    }

    private boolean canMove(int x, int y) {
        if(x < 0 || y < 0 || y >= map.length || x >= map[0].length) return false;
        int val = map[y][x];
        return val != tile0Value && val != tile9Value && val != tile10Value && val != tile21Value;
    }

    private void movementSetup(){
        scene.setOnKeyPressed(e -> {
            boolean wasMoving = wPressed.get() || aPressed.get() || sPressed.get() || dPressed.get();

            if(e.getCode() == KeyCode.W) wPressed.set(true);
            if(e.getCode() == KeyCode.A) aPressed.set(true);
            if(e.getCode() == KeyCode.S) sPressed.set(true);
            if(e.getCode() == KeyCode.D) dPressed.set(true);

            if (!wasMoving && (wPressed.get() || aPressed.get() || sPressed.get() || dPressed.get())) {
                runningAnimation.startAnimation();
            }

            // TELEPORTATION LOGIC
            if (e.getCode() == KeyCode.E) {
                if (teleportation.isOnTile3(playerX, playerY)) {
                    int[] newPos = teleportation.getRandomTeleportPosition(playerX, playerY);
                    if (newPos != null) {
                        playerX = newPos[0];
                        playerY = newPos[1];
                        camera.update(playerX, playerY);
                        redrawMap();
                    }
                }
            }
        });

        scene.setOnKeyReleased(e ->{
            if(e.getCode() == KeyCode.W) wPressed.set(false);
            if(e.getCode() == KeyCode.A) aPressed.set(false);
            if(e.getCode() == KeyCode.S) sPressed.set(false);
            if(e.getCode() == KeyCode.D) dPressed.set(false);

            if (!wPressed.get() && !aPressed.get() && !sPressed.get() && !dPressed.get()) {
                runningAnimation.stopAnimation();
            }
        });
    }

    // AnimationTimer for continuous movement
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
                runningAnimation.setDirection(KeyCode.W);
            } else if (sPressed.get()) {
                if (canMove(playerX, playerY + 1)) {
                    playerY++;
                    moved = true;
                }
                runningAnimation.setDirection(KeyCode.S);
            } else if (aPressed.get()) {
                if (canMove(playerX - 1, playerY)) {
                    playerX--;
                    moved = true;
                }
                runningAnimation.setDirection(KeyCode.A);
            } else if (dPressed.get()) {
                if (canMove(playerX + 1, playerY)) {
                    playerX++;
                    moved = true;
                }
                runningAnimation.setDirection(KeyCode.D);
            }

            if (moved) {
                camera.update(playerX, playerY);
                redrawMap();
            }
        }
    };
}