package main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerTableUtil {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/spycrew";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD =  "Hasnat";

    // Create the player table for this game session
    public static void createPlayerTable(int gameId) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS players_" + gameId + " (" +
                "player_id INT PRIMARY KEY," +
                "player_name VARCHAR(50)," +
                "pos_x INT NOT NULL," +
                "pos_y INT NOT NULL," +
                "role ENUM('normal', 'impostor') NOT NULL," +
                "is_alive BOOLEAN NOT NULL DEFAULT TRUE" +
                ")";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    // Drop the player table for this game session
    public static void dropPlayerTable(int gameId) throws SQLException {
        String sql = "DROP TABLE IF EXISTS players_" + gameId;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    // Add all players to the game, and randomly assign one as impostor
    public static void addPlayersWithRandomImpostor(int gameId, List<Integer> playerIds, List<String> playerNames, int startX, int startY) throws SQLException {
        Random rand = new Random();
        int impostorIndex = rand.nextInt(playerIds.size());
        for (int i = 0; i < playerIds.size(); i++) {
            String role = (i == impostorIndex) ? "impostor" : "normal";
            addPlayer(gameId, playerIds.get(i), playerNames.get(i), startX, startY, role);
        }
    }

    // Add a player at the start of the game
    public static void addPlayer(int gameId, int playerId, String playerName, int posX, int posY, String role) throws SQLException {
        String sql = "INSERT INTO players_" + gameId +
                " (player_id, player_name, pos_x, pos_y, role, is_alive) VALUES (?, ?, ?, ?, ?, TRUE)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);
            stmt.setString(2, playerName);
            stmt.setInt(3, posX);
            stmt.setInt(4, posY);
            stmt.setString(5, role);
            stmt.executeUpdate();
        }
    }

    // Update a player's position
    public static void updatePlayerPosition(int gameId, int playerId, int posX, int posY) throws SQLException {
        String sql = "UPDATE players_" + gameId + " SET pos_x = ?, pos_y = ? WHERE player_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, posX);
            stmt.setInt(2, posY);
            stmt.setInt(3, playerId);
            stmt.executeUpdate();
        }
    }

    // Update a player's alive status (for killing a player etc.)
    public static void updatePlayerAliveStatus(int gameId, int playerId, boolean isAlive) throws SQLException {
        String sql = "UPDATE players_" + gameId + " SET is_alive = ? WHERE player_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isAlive);
            stmt.setInt(2, playerId);
            stmt.executeUpdate();
        }
    }

    // Get all players and their states (for rendering opponent positions)
    public static List<PlayerState> getAllPlayers(int gameId) throws SQLException {
        String sql = "SELECT player_id, player_name, pos_x, pos_y, role, is_alive FROM players_" + gameId;
        List<PlayerState> players = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PlayerState ps = new PlayerState(
                        rs.getInt("player_id"),
                        rs.getString("player_name"),
                        rs.getInt("pos_x"),
                        rs.getInt("pos_y"),
                        rs.getString("role"),
                        rs.getBoolean("is_alive")
                );
                players.add(ps);
            }
        }
        return players;
    }

    // Helper class for player state
    public static class PlayerState {
        public int id;
        public String name;
        public int x;
        public int y;
        public String role;
        public boolean alive;

        public PlayerState(int id, String name, int x, int y, String role, boolean alive) {
            this.id = id;
            this.name = name;
            this.x = x;
            this.y = y;
            this.role = role;
            this.alive = alive;
        }
    }
}