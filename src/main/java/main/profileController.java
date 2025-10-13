package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class profileController implements Initializable {

    @FXML
    private Label name;
    @FXML
    private Label point;
    @FXML
    private VBox historyVBox;
    @FXML
    private VBox roomVBox;
    @FXML
    private Button startButton;

    // Set these from your app/session logic
    private int playerId = 1; // Example: set user id dynamically

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Load player profile info
            UserProfile profile = getUserProfile(playerId);
            if (profile != null) {
                name.setText("Name: " + profile.username);
                point.setText("Point: " + profile.score);
            }

            // Load game history
            historyVBox.getChildren().clear();
            for (GameHistory gh : getGameHistory(playerId)) {
                Label historyLabel = new Label(
                        "Game #" + gh.id + " | Result: " + gh.gameResult +
                                " | Points: " + gh.pointsGained +
                                " | Date: " + gh.playedAt
                );
                historyVBox.getChildren().add(historyLabel);
            }

            // Room logic (unchanged for now)
            roomVBox.getChildren().clear();
            // (Add your room logic here)

            // Start button action
            startButton.setOnAction(e -> startGame());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private UserProfile getUserProfile(int userId) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/spycrew", "root", "Hasnat");
        PreparedStatement ps = conn.prepareStatement("SELECT username, score FROM users WHERE id = ?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        UserProfile profile = null;
        if (rs.next()) {
            profile = new UserProfile(rs.getString("username"), rs.getInt("score"));
        }
        rs.close();
        ps.close();
        conn.close();
        return profile;
    }

    private static class UserProfile {
        String username;
        int score;
        public UserProfile(String username, int score) {
            this.username = username;
            this.score = score;
        }
    }

    private java.util.List<GameHistory> getGameHistory(int userId) throws SQLException {
        java.util.List<GameHistory> list = new java.util.ArrayList<>();
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/spycrew", "root", "Hasnat");
        PreparedStatement ps = conn.prepareStatement(
                "SELECT id, game_result, points_gained, played_at FROM game_history WHERE user_id = ? ORDER BY played_at DESC"
        );
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new GameHistory(
                    rs.getInt("id"),
                    rs.getString("game_result"),
                    rs.getInt("points_gained"),
                    rs.getTimestamp("played_at")
            ));
        }
        rs.close();
        ps.close();
        conn.close();
        return list;
    }

    private static class GameHistory {
        int id;
        String gameResult;
        int pointsGained;
        java.sql.Timestamp playedAt;
        public GameHistory(int id, String gameResult, int pointsGained, java.sql.Timestamp playedAt) {
            this.id = id;
            this.gameResult = gameResult;
            this.pointsGained = pointsGained;
            this.playedAt = playedAt;
        }
    }

    // Your startGame logic (unchanged)
    private void startGame() {
        // Implement your game start logic here
    }
}