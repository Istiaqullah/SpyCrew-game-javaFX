package main;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class Sound {
    private MediaPlayer mediaPlayer;

    public Sound(String soundFileName) {
        try {
            // The resource path should be relative to the resources folder
            URL soundUrl = getClass().getResource("/main/sound/" + soundFileName);
            if (soundUrl == null) {
                throw new RuntimeException("Sound file not found: " + soundFileName);
            }
            Media media = new Media(soundUrl.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop forever
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }
}