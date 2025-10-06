package main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

public class RunningAnimation {

    private ImageView runner;
    private Timeline timeline;
    private int frame = 0;// 0 or 1 to toggle between two frames
    private KeyCode currentDirection = null;

    private final Image imgup1 = new Image("file:src/main/resources/img/Player/up_1.png");
    private final Image imgup2 = new Image("file:src/main/resources/img/Player/up_2.png");
    private final Image imgdown1 = new Image("file:src/main/resources/img/Player/down_1.png");
    private final Image imgdown2 = new Image("file:src/main/resources/img/Player/down_2.png");
    private final Image imgright1 = new Image("file:src/main/resources/img/Player/right_1.png");
    private final Image imgright2 = new Image("file:src/main/resources/img/Player/right_2.png");
    private final Image imgleft1 = new Image("file:src/main/resources/img/Player/left_1.png");
    private final Image imgleft2 = new Image("file:src/main/resources/img/Player/left_2.png");

    public RunningAnimation(ImageView runner) {
        this.runner = runner;
        timeline = new Timeline(new KeyFrame(Duration.millis(150), e -> updateImage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void setDirection(KeyCode direction) {
        currentDirection = direction;
        updateImage();
    }

    public void startAnimation() {
        timeline.play();
    }

    public void stopAnimation() {
        timeline.stop();
    }

    private void updateImage() {
        if (currentDirection == null) return;
        switch (currentDirection) {
            case W:
                runner.setImage(frame % 2 == 0 ? imgup1 : imgup2);
                break;
            case S:
                runner.setImage(frame % 2 == 0 ? imgdown1 : imgdown2);
                break;
            case A:
                runner.setImage(frame % 2 == 0 ? imgleft1 : imgleft2);
                break;
            case D:
                runner.setImage(frame % 2 == 0 ? imgright1 : imgright2);
                break;
            default:
                break;
        }
        frame++;
    }
}