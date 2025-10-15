package Log;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.sql.*;
import java.io.IOException;

//password Hasnat
public class utils {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/spycrew";
    private static final String USER = "root";
    private static final String PASSWORD = "Hasnat";
    // Sign Up
    public static void signup(ActionEvent event,String username, String email, String password) throws SQLException {
       Connection conn = null;
       PreparedStatement insert = null;
       PreparedStatement exists = null;
         ResultSet resultSet = null;
         try {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                String checkQuery = "SELECT * FROM users WHERE username = ? OR email = ?";
                exists = conn.prepareStatement(checkQuery);
                exists.setString(1, username);
                exists.setString(2, email);
                resultSet = exists.executeQuery();
                if (resultSet.next()) {
                    System.out.println("Username or Email already exists.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Username or Email already exists.");
                    alert.show();

                } else {
                    String insertQuery = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
                    insert = conn.prepareStatement(insertQuery);
                    insert.setString(1, username);
                    insert.setString(2, email);
                    insert.setString(3, password);
                    insert.executeUpdate();
                    System.out.println("User registered.");
                    changeScene(event ,"/main/profile.fxml",username,null);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (resultSet != null) resultSet.close();
                if (exists != null) exists.close();
                if (insert != null) insert.close();
                if (conn != null) conn.close();
         }
    }

public static void login(ActionEvent event,String username, String password) throws SQLException {

    Connection conn = null;
    PreparedStatement exists = null;
    ResultSet resultSet = null;
    try {
        conn = DriverManager.getConnection(URL, USER, PASSWORD);
        String checkQuery = "SELECT * FROM users WHERE username = ? AND password = ? ";
        exists = conn.prepareStatement(checkQuery);
        exists.setString(1, username);
        exists.setString(2, password);
        resultSet = exists.executeQuery();
        if (resultSet.next()) {
            System.out.println("Login successful.");
            changeScene(event ,"/main/profile.fxml",username,null );
        } else {
            System.out.println("Invalid username or password.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid username or password.");
            alert.show();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        if (resultSet != null) resultSet.close();
        if (exists != null) exists.close();
        if (conn != null) conn.close();
    }
}

    // Change Scene


    static void changeScene(ActionEvent event, String fxmlFile, String username, String fame) {
        try {
            FXMLLoader loader = new FXMLLoader(utils.class.getResource(fxmlFile));
            Parent root = loader.load();

            // Pass username to profileController if applicable
            if (username != null && fxmlFile.equals("/main/profile.fxml")) {
                Object controller = loader.getController();
                if (controller instanceof main.profileController) {
                    ((main.profileController) controller).setUsername(username);
                }
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 850, 470));
            stage.setTitle("SpyCrew");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}