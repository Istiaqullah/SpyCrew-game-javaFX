package main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Game extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource("/main/Game.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("SpyCrew");
        stage.setScene(new Scene(root, 368, 368));
        root.requestFocus(); // Set focus to the root node (it can receive key events)
        stage.show();

    }
}