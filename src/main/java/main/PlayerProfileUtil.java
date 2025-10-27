//package main;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PlayerProfileUtil {
//    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/spycrew";
//    private static final String DB_USER ="root";
//    private static final String DB_PASSWORD =  "Hasnat";
//
//    // Register a new player
//    public static int createPlayerProfile(String playerName) throws SQLException {
//        String sql = "INSERT INTO player_profile (player_name) VALUES (?)";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            stmt.setString(1, playerName);
//            stmt.executeUpdate();
//            try (ResultSet rs = stmt.getGeneratedKeys()) {
//                if (rs.next()) return rs.getInt(1);
//            }
//        }
//        throw new SQLException("Unable to create player profile.");
//    }
//
//    // Get player profile info
//    public static PlayerProfile getPlayerProfile(int playerId) throws SQLException {
//        String sql = "SELECT * FROM player_profile WHERE player_id = ?";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, playerId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return new PlayerProfile(
//                            rs.getInt("player_id"),
//                            rs.getString("player_name"),
//                            rs.getInt("achievement_points")
//                    );
//                }
//            }
//        }
//        return null;
//    }
//
//    // Add a game history record
//    public static void addGameHistory(int playerId, String result, int pointsGained) throws SQLException {
//        String sql = "INSERT INTO game_history (player_id, result, achievement_points_gained) VALUES (?, ?, ?)";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, playerId);
//            stmt.setString(2, result);
//            stmt.setInt(3, pointsGained);
//            stmt.executeUpdate();
//        }
//        // You may also update achievement points in player_profile here if needed
//        updateAchievementPoints(playerId, pointsGained);
//    }
//
//    // Get player's game history
//    public static List<GameHistory> getGameHistory(int playerId) throws SQLException {
//        String sql = "SELECT * FROM game_history WHERE player_id = ? ORDER BY played_at DESC";
//        List<GameHistory> historyList = new ArrayList<>();
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, playerId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    historyList.add(new GameHistory(
//                            rs.getInt("game_id"),
//                            rs.getInt("player_id"),
//                            rs.getString("result"),
//                            rs.getInt("achievement_points_gained"),
//                            rs.getTimestamp("played_at")
//                    ));
//                }
//            }
//        }
//        return historyList;
//    }
//
//    // Update achievement points
//    public static void updateAchievementPoints(int playerId, int points) throws SQLException {
//        String sql = "UPDATE player_profile SET achievement_points = achievement_points + ? WHERE player_id = ?";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, points);
//            stmt.setInt(2, playerId);
//            stmt.executeUpdate();
//        }
//    }
//
//    // Data classes
//    public static class PlayerProfile {
//        public int id;
//        public String name;
//        public int achievementPoints;
//
//        public PlayerProfile(int id, String name, int achievementPoints) {
//            this.id = id;
//            this.name = name;
//            this.achievementPoints = achievementPoints;
//        }
//    }
//    public static class GameHistory {
//        public int gameId;
//        public int playerId;
//        public String result;
//        public int pointsGained;
//        public Timestamp playedAt;
//
//        public GameHistory(int gameId, int playerId, String result, int pointsGained, Timestamp playedAt) {
//            this.gameId = gameId;
//            this.playerId = playerId;
//            this.result = result;
//            this.pointsGained = pointsGained;
//            this.playedAt = playedAt;
//        }
//    }
//}