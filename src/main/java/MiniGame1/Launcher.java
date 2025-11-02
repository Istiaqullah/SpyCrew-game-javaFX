package MiniGame1;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    public static String result = null;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TicTacToe.fxml"));
        Scene scene = new Scene(loader.load());
        TicTacToeController controller = loader.getController();
        controller.setResultCallback(res -> {
            result = res;
            stage.close();
        });
        stage.setScene(scene);
        stage.setTitle("Tic Tac Toe");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}