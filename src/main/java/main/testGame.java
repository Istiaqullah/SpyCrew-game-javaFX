package main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class testGame extends Application {
   // private Sound backgroundSound;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(testGame.class.getResource("/main/testGame.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("SpyCrew");
        stage.setScene(new Scene(root, 368, 368)); // Adjust for your tile scaling if needed
        root.requestFocus(); // Set focus to the root node (it can receive key events)
        stage.show();

    }
}