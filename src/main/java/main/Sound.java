package main;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class Sound {
    private MediaPlayer player;

    public Sound(String fileName) {
        URL resource = getClass().getResource("/main/sounds/" + fileName);
        if (resource != null) {
            Media media = new Media(resource.toString());
           player  = new MediaPlayer(media);
        }
    }

    public void play() {
        if (player != null) {
            player.stop();
            player.play();
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    public void loop() {
        if (player != null) {
            player.setCycleCount(player.INDEFINITE);
            player.play();
        }
    }
}
