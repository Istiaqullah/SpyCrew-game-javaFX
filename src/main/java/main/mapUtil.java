package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class mapUtil {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/spycrew";
    private static final String USER = "root";
    private static final String PASSWORD = "Hasnat";
    
    // Fetches the map as int[][] from the database
    public static int[][] fetchMapFromDB() throws Exception {
        String mapText = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            stmt = conn.prepareStatement("SELECT map_text FROM game_map WHERE id = 1");
            rs = stmt.executeQuery();
            if (rs.next()) {
                mapText = rs.getString("map_text");
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }

        if (mapText == null) throw new Exception("Map not found in database!");

        // Parse map text into int[][]
        return parseMap(mapText);
    }

    // Converts the map text into int[][] array
    private static int[][] parseMap(String mapText) {
        String[] lines = mapText.split("\\r?\\n");
        List<int[]> rows = new ArrayList<>();
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] tokens = line.trim().split(" ");
            int[] row = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                row[i] = Integer.parseInt(tokens[i]);
            }
            rows.add(row);
        }

        int[][] map = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            map[i] = rows.get(i);
        }
        return map;
    }
}
