package main;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

public class Movement {

    private BooleanProperty wPressed = new SimpleBooleanProperty();
    private BooleanProperty aPressed = new SimpleBooleanProperty();
    private BooleanProperty sPressed = new SimpleBooleanProperty();
    private BooleanProperty dPressed = new SimpleBooleanProperty();

    private BooleanBinding keyPressed = wPressed.or(aPressed).or(sPressed).or(dPressed);

    private int movementVariable = 1;

    private RunningAnimation runningAnimation;

    @FXML
    private ImageView player;

    @FXML
    private AnchorPane scene;
    @FXML
    private Label name;



    private static int[][] map; // The map array
    private int tileSize = 16; // Size of each tile in pixels
    private int tile0Value = 0; // Value representing tile_2
private int tile9Value = 9;
private int tile10Value = 10;
    Sound walk=new Sound("walk.mp3");
    public static void setMap(int[][] map) {
        Movement.map = map;
    }

    public void RunRun(ImageView player, AnchorPane scene, Label name){
        this.player = player;
        this.scene = scene;
        this.name = name;
        movementSetup();
        runningAnimation = new RunningAnimation(player);
        keyPressed.addListener(((observableValue, aBoolean, t1) -> {
            if(!aBoolean){
                timer.start();
                walk.play();
                runningAnimation.startAnimation();
            } else {
                timer.stop();
                runningAnimation.stopAnimation();
            }
        }));
    }
    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long timestamp) {

            if (wPressed.get()) {
                int nextY = (int)((player.getLayoutY() - movementVariable) / tileSize);
                int x = (int)(player.getLayoutX() / tileSize);
                if (map[nextY][x] != tile0Value&& map[nextY][x] != tile9Value && map[nextY][x] != tile10Value) {
                    player.setLayoutY(player.getLayoutY() - movementVariable);
                    name.setLayoutY(name.getLayoutY() - movementVariable);
                }
            }
            if (sPressed.get()) {
                int nextY = (int)((player.getLayoutY() + movementVariable + player.getFitHeight() - 1) / tileSize);
                int x = (int)(player.getLayoutX() / tileSize);
                if (map[nextY][x] != tile0Value&& map[nextY][x] != tile9Value && map[nextY][x] != tile10Value) {
                    player.setLayoutY(player.getLayoutY() + movementVariable);
                    name.setLayoutY(name.getLayoutY() + movementVariable);
                }
            }
            if (aPressed.get()) {
                int y = (int)(player.getLayoutY() / tileSize);
                int nextX = (int)((player.getLayoutX() - movementVariable) / tileSize);
                if (map[y][nextX] != tile0Value&& map[y][nextX] != tile9Value && map[y][nextX] != tile10Value) {
                    player.setLayoutX(player.getLayoutX() - movementVariable);
                    name.setLayoutX(name.getLayoutX() - movementVariable);

                }
            }
            if (dPressed.get()) {
                int y = (int)(player.getLayoutY() / tileSize);
                int nextX = (int)((player.getLayoutX() + movementVariable + player.getFitWidth() - 1) / tileSize);
                if (map[y][nextX] != tile0Value&& map[y][nextX] != tile9Value && map[y][nextX] != tile10Value) {
                    player.setLayoutX(player.getLayoutX() + movementVariable);
                    name.setLayoutX(name.getLayoutX() + movementVariable);
                }
            }
        }
    };
    private void movementSetup(){
        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.W) {
                wPressed.set(true);

                runningAnimation.setDirection(KeyCode.W);
            }
            if(e.getCode() == KeyCode.A) {
                aPressed.set(true);

                runningAnimation.setDirection(KeyCode.A);
            }
            if(e.getCode() == KeyCode.S) {
                sPressed.set(true);

                runningAnimation.setDirection(KeyCode.S);
            }
            if(e.getCode() == KeyCode.D) {
                dPressed.set(true);

                runningAnimation.setDirection(KeyCode.D);
            }
        });

        scene.setOnKeyReleased(e ->{
            if(e.getCode() == KeyCode.W) {
                wPressed.set(false);
            }

            if(e.getCode() == KeyCode.A) {
                aPressed.set(false);
            }

            if(e.getCode() == KeyCode.S) {
                sPressed.set(false);
            }

            if(e.getCode() == KeyCode.D) {
                dPressed.set(false);
            }
        });
    }
}