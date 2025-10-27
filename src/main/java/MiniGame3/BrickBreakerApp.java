package MiniGame3;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BrickBreakerApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MiniGame3/brickbreaker-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Brick Breaker");
        stage.setScene(scene);
        stage.show();
        // No key handler here! All is handled in controller.
    }

    public static void main(String[] args) {
        launch();
    }
}