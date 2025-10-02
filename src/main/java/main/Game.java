package main;

import Log.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Game extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource("/main/Game.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("SpyCrew");
        stage.setResizable(false);
        stage.setScene(new Scene(root, 864, 480));
        root.requestFocus(); // Set focus to the root node
        stage.show();
    }
}
