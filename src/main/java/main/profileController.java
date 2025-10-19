package main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

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
    @FXML
    private Button createRoomButton;
    @FXML
    private Button joinRoomButton;
    @FXML
    private Button leaveRoomButton;
    @FXML
    private TextField roomKeyField;
    @FXML
    private Label roomKeyDisplay;

    // These fields will be set after login, not hardcoded anymore
    private Integer playerId = null;
    private String username = null;

    private Integer roomId = null;
    private String currentRoomKey = "";
    private Integer roomCreatorId = null;
    private Timeline pollingTimeline;

    // This method will be called by utils.changeScene after login
    public void setUsername(String username) {
        this.username = username;
        // Fetch playerId and points from DB based on username
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT id, score FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.playerId = rs.getInt("id");
                int accountPoints = rs.getInt("score");
                if (name != null) {
                    name.setText("Name: " + username);
                }
                if (point != null) {
                    point.setText("Point: " + accountPoints);
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Only fetch profile if username/playerId is set
        try {
            if (playerId != null) {
                UserProfile profile = getUserProfile(playerId);
                if (profile != null) {
                    name.setText("Name: " + profile.username);
                    point.setText("Point: " + profile.score);
                }

                historyVBox.getChildren().clear();
                for (GameHistory gh : getGameHistory(playerId)) {
                    Label historyLabel = new Label(
                            "Game #" + gh.id + " | Result: " + gh.gameResult +
                                    " | Points: " + gh.pointsGained +
                                    " | Date: " + gh.playedAt
                    );
                    historyVBox.getChildren().add(historyLabel);
                }
            }

            roomVBox.getChildren().clear();
            if (roomId != null) {
                for (RoomPlayer rp : getRoomPlayers(roomId)) {
                    Label roomLabel = new Label(rp.username);
                    roomVBox.getChildren().add(roomLabel);
                }
                roomKeyDisplay.setText("Room Key: " + currentRoomKey);
            } else {
                roomKeyDisplay.setText("Room Key: ");
            }

            createRoomButton.setOnAction(e -> createRoom());
            joinRoomButton.setOnAction(e -> joinRoom());
            leaveRoomButton.setOnAction(e -> leaveRoom());
            startButton.setOnAction(e -> startGame());

            // Start polling for game start if in a room
            startPollingForGameStart();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------ Room Management Methods ---------------------

    private void createRoom() {
        String roomKey = UUID.randomUUID().toString().substring(0, 8); // short random key
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO rooms (room_key, creator_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, roomKey);
            ps.setInt(2, playerId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                roomId = rs.getInt(1);
                currentRoomKey = roomKey;
                roomCreatorId = playerId;
                roomKeyDisplay.setText("Room Key: " + currentRoomKey);
            }
            rs.close();
            ps.close();

            PreparedStatement ps2 = conn.prepareStatement("INSERT INTO room_players (room_id, user_id) VALUES (?, ?)");
            ps2.setInt(1, roomId);
            ps2.setInt(2, playerId);
            ps2.executeUpdate();
            ps2.close();

            refreshRoomPlayers();
            startPollingForGameStart();
        } catch (SQLException e) {
            showAlert("Error", "Could not create room: " + e.getMessage());
        }
    }

    private void joinRoom() {
        String roomKey = roomKeyField.getText().trim();
        if (roomKey.isEmpty()) {
            showAlert("Input Error", "Please enter a room key.");
            return;
        }
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT id, creator_id FROM rooms WHERE room_key = ?");
            ps.setString(1, roomKey);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                roomId = rs.getInt("id");
                roomCreatorId = rs.getInt("creator_id");
                currentRoomKey = roomKey;
                roomKeyDisplay.setText("Room Key: " + currentRoomKey);

                PreparedStatement ps2 = conn.prepareStatement("INSERT IGNORE INTO room_players (room_id, user_id) VALUES (?, ?)");
                ps2.setInt(1, roomId);
                ps2.setInt(2, playerId);
                ps2.executeUpdate();
                ps2.close();

                refreshRoomPlayers();
                startPollingForGameStart();
            } else {
                showAlert("Room Not Found", "No room with this key.");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            showAlert("Error", "Could not join room: " + e.getMessage());
        }
    }

    private void leaveRoom() {
        if (roomId == null) return;
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM room_players WHERE room_id = ? AND user_id = ?");
            ps.setInt(1, roomId);
            ps.setInt(2, playerId);
            ps.executeUpdate();
            ps.close();

            PreparedStatement ps2 = conn.prepareStatement("SELECT creator_id FROM rooms WHERE id = ?");
            ps2.setInt(1, roomId);
            ResultSet rs = ps2.executeQuery();
            if (rs.next() && rs.getInt("creator_id") == playerId) {
                PreparedStatement ps3 = conn.prepareStatement("DELETE FROM rooms WHERE id = ?");
                ps3.setInt(1, roomId);
                ps3.executeUpdate();
                ps3.close();
            }
            rs.close();
            ps2.close();

            roomId = null;
            currentRoomKey = "";
            roomCreatorId = null;
            roomKeyDisplay.setText("Room Key: ");
            roomVBox.getChildren().clear();
            stopPollingForGameStart();
        } catch (SQLException e) {
            showAlert("Error", "Could not leave room: " + e.getMessage());
        }
    }

    private void startGame() {
        if (roomId == null) {
            showAlert("Error", "You are not in a room!");
            return;
        }
        if (playerId != roomCreatorId) {
            showAlert("Not Allowed", "Only the room creator can start the game!");
            return;
        }
        try (Connection conn = getConnection()) {
            PreparedStatement ps2 = conn.prepareStatement("UPDATE rooms SET started = TRUE WHERE id = ?");
            ps2.setInt(1, roomId);
            ps2.executeUpdate();
            ps2.close();

            showAlert("Game Started", "Game started for Room: " + currentRoomKey);
            switchToGameScene();
        } catch (SQLException e) {
            showAlert("Error", "Could not start game: " + e.getMessage());
        }
    }

    // ------------- Polling for Game Start -------------
    private void startPollingForGameStart() {
        if (pollingTimeline != null) {
            pollingTimeline.stop();
        }
        if (roomId == null) return;
        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
            if (!isGameStarted()) return;
            pollingTimeline.stop();
            switchToGameScene();
        }));
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
        pollingTimeline.play();
    }

    private void stopPollingForGameStart() {
        if (pollingTimeline != null) {
            pollingTimeline.stop();
            pollingTimeline = null;
        }
    }

    private boolean isGameStarted() {
        if (roomId == null) return false;
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT started FROM rooms WHERE id = ?");
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean started = rs.getBoolean("started");
                rs.close();
                ps.close();
                return started;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            // Ignore, just keep polling
        }
        return false;
    }

    private void switchToGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/game.fxml"));
            Parent gameRoot = loader.load();
            GameController gameController = loader.getController();

            // Get room player names
            List<String> names = new ArrayList<>();
            for (RoomPlayer rp : getRoomPlayers(roomId)) {
                names.add(rp.username);
            }
            gameController.setPlayerNames(names);

            Stage stage = (Stage) name.getScene().getWindow();
            Scene gameScene = new Scene(gameRoot, 368, 368);
            gameRoot.requestFocus();
            stage.setScene(gameScene);
            stage.setTitle("SpyCrew");
            stage.show();
        } catch (Exception e) {
            //showAlert("Error", "Failed to switch to game scene: " + e.getMessage());
        }
    }

    private void refreshRoomPlayers() throws SQLException {
        roomVBox.getChildren().clear();
        if (roomId != null) {
            for (RoomPlayer rp : getRoomPlayers(roomId)) {
                Label roomLabel = new Label(rp.username);
                roomVBox.getChildren().add(roomLabel);
            }
        }
    }

    // --------------- Helper Methods -------------------

    private UserProfile getUserProfile(int userId) throws SQLException {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT username, score FROM users WHERE id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            UserProfile profile = null;
            if (rs.next()) {
                profile = new UserProfile(rs.getString("username"), rs.getInt("score"));
            }
            rs.close();
            ps.close();
            return profile;
        }
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
        try (Connection conn = getConnection()) {
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
            return list;
        }
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

    private java.util.List<RoomPlayer> getRoomPlayers(int roomId) throws SQLException {
        java.util.List<RoomPlayer> list = new java.util.ArrayList<>();
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT u.username FROM room_players rp JOIN users u ON rp.user_id = u.id WHERE rp.room_id = ?"
            );
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RoomPlayer(rs.getString("username")));
            }
            rs.close();
            ps.close();
            return list;
        }
    }

    private static class RoomPlayer {
        String username;
        public RoomPlayer(String username) {
            this.username = username;
        }
    }

    // Helper to get connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/spycrew", "root", "Hasnat");
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
