//package main;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class RoomUtil {
//    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/spycrew";
//    private static final String DB_USER = "root";     // Replace!
//    private static final String DB_PASSWORD = "Hasnat";
//
//    // Create a new room and return its room_id, storing the creator
//    public static int createRoom(int creatorPlayerId) throws SQLException {
//        String sql = "INSERT INTO game_room (started, created_by) VALUES (FALSE, ?)";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            stmt.setInt(1, creatorPlayerId);
//            stmt.executeUpdate();
//            try (ResultSet rs = stmt.getGeneratedKeys()) {
//                if (rs.next()) return rs.getInt(1);
//            }
//        }
//        throw new SQLException("Unable to create room.");
//    }
//
//    // Join a room (add player to room_player)
//    public static boolean joinRoom(int roomId, int playerId) throws SQLException {
//        // Check if room is not started
//        if (isRoomStarted(roomId)) return false;
//        // Prevent duplicate join
//        if (isPlayerInRoom(roomId, playerId)) return false;
//        String sql = "INSERT INTO room_player (room_id, player_id) VALUES (?, ?)";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, roomId);
//            stmt.setInt(2, playerId);
//            stmt.executeUpdate();
//        }
//        return true;
//    }
//
//    // Get all players in a room (return their names)
//    public static List<String> getRoomPlayers(int roomId) throws SQLException {
//        String sql = "SELECT pp.player_name FROM room_player rp JOIN player_profile pp ON rp.player_id = pp.player_id WHERE rp.room_id = ?";
//        List<String> players = new ArrayList<>();
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, roomId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    players.add(rs.getString("player_name"));
//                }
//            }
//        }
//        return players;
//    }
//
//    // Get all player IDs in a room
//    public static List<Integer> getRoomPlayerIds(int roomId) throws SQLException {
//        String sql = "SELECT player_id FROM room_player WHERE room_id = ?";
//        List<Integer> ids = new ArrayList<>();
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, roomId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) ids.add(rs.getInt("player_id"));
//            }
//        }
//        return ids;
//    }
//
//    // Start the room (creator only)
//    public static boolean startRoom(int roomId, int currentPlayerId) throws SQLException {
//        if (isRoomStarted(roomId)) return false;
//        if (!isRoomCreator(roomId, currentPlayerId)) return false; // Only creator can start
//        String sql = "UPDATE game_room SET started = TRUE WHERE room_id = ?";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, roomId);
//            stmt.executeUpdate();
//        }
//        return true;
//    }
//
//    // Check if room started
//    public static boolean isRoomStarted(int roomId) throws SQLException {
//        String sql = "SELECT started FROM game_room WHERE room_id = ?";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, roomId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getBoolean("started");
//                }
//            }
//        }
//        return false;
//    }
//
//    // Check if current player is the creator
//    public static boolean isRoomCreator(int roomId, int playerId) throws SQLException {
//        String sql = "SELECT created_by FROM game_room WHERE room_id = ?";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, roomId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) return rs.getInt("created_by") == playerId;
//            }
//        }
//        return false;
//    }
//
//    // Check if a player is already in the room
//    public static boolean isPlayerInRoom(int roomId, int playerId) throws SQLException {
//        String sql = "SELECT 1 FROM room_player WHERE room_id = ? AND player_id = ?";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, roomId);
//            stmt.setInt(2, playerId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                return rs.next();
//            }
//        }
//    }
//
//    // Optional: Remove player from room (for leaving)
//    public static void leaveRoom(int roomId, int playerId) throws SQLException {
//        String sql = "DELETE FROM room_player WHERE room_id = ? AND player_id = ?";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, roomId);
//            stmt.setInt(2, playerId);
//            stmt.executeUpdate();
//        }
//    }
//
//    // Optional: Get room ID for player (if you want to find the room the player is in)
//    public static Integer getPlayerRoom(int playerId) throws SQLException {
//        String sql = "SELECT room_id FROM room_player WHERE player_id = ?";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, playerId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt("room_id");
//                }
//            }
//        }
//        return null;
//    }
//}