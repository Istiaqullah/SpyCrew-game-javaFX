package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Teleportation {
    private int[][] map;
    private List<int[]> tile3Positions; // List of {x, y} positions of tile_3
    private Random random;

    public Teleportation(int[][] map) {
        this.map = map;
        this.tile3Positions = new ArrayList<>();
        this.random = new Random();
        findTile3Positions();
    }

    private void findTile3Positions() {
        tile3Positions.clear();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 3) {
                    tile3Positions.add(new int[]{x, y});
                }
            }
        }
    }

    public boolean isOnTile3(int x, int y) {
        for (int[] pos : tile3Positions) {
            if (pos[0] == x && pos[1] == y) return true;
        }
        return false;
    }

    // Returns {newX, newY} if teleportation is possible, or null if not
    public int[] getRandomTeleportPosition(int currentX, int currentY) {
        List<int[]> otherPositions = new ArrayList<>();
        for (int[] pos : tile3Positions) {
            if (!(pos[0] == currentX && pos[1] == currentY)) {
                otherPositions.add(pos);
            }
        }
        if (otherPositions.isEmpty()) return null;
        return otherPositions.get(random.nextInt(otherPositions.size()));
    }
}