package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
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
    private Integer roomId = null; // Set after room is created or joined

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Load player profile info
            PlayerProfileUtil.PlayerProfile profile = PlayerProfileUtil.getPlayerProfile(playerId);
            if (profile != null) {
                name.setText("Name: " + profile.name);
                point.setText("Point: " + profile.achievementPoints);
            }

            // Load game history
            List<PlayerProfileUtil.GameHistory> history = PlayerProfileUtil.getGameHistory(playerId);
            historyVBox.getChildren().clear();
            for (PlayerProfileUtil.GameHistory gh : history) {
                Label historyLabel = new Label(
                        "Game #" + gh.gameId + " | Result: " + gh.result +
                                " | Points: " + gh.pointsGained +
                                " | Date: " + gh.playedAt.toString()
                );
                historyVBox.getChildren().add(historyLabel);
            }

            // Load room players if in a room
            roomVBox.getChildren().clear();
            if (roomId != null) {
                List<String> roomPlayers = RoomUtil.getRoomPlayers(roomId);
                for (String playerName : roomPlayers) {
                    Label roomLabel = new Label(playerName);
                    roomVBox.getChildren().add(roomLabel);
                }
            }

            // Start button action (for starting the game)
            startButton.setOnAction(e -> startGame());

        } catch (SQLException e) {
            e.printStackTrace();
            // You can display an error message to the user here
        }
    }

    // Logic to start the game when creator clicks start
    private void startGame() {
        try {
            if (roomId == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You are not in a room!");
                alert.showAndWait();
                return;
            }
            // Only allow if current user is room creator
            if (!RoomUtil.isRoomCreator(roomId, playerId)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Only the room creator can start the game!");
                alert.showAndWait();
                return;
            }
            // Start the room (can be <5 players)
            if (RoomUtil.startRoom(roomId, playerId)) {
                // Get all player IDs and names in the room
                List<Integer> playerIds = RoomUtil.getRoomPlayerIds(roomId);
                List<String> playerNames = RoomUtil.getRoomPlayers(roomId);
                int gameId = roomId; // Or use a different id if needed

                // Create game player table
                PlayerTableUtil.createPlayerTable(gameId);

                // Add all players, randomly assign impostor
                PlayerTableUtil.addPlayersWithRandomImpostor(gameId, playerIds, playerNames, 0, 0);

                // Proceed to start game logic/UI...
                System.out.println("Game started!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error starting game: " + e.getMessage());
            alert.showAndWait();
        }
    }
}