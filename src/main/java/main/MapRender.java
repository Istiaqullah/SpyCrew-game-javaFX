package main;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MapRender {
    public static final int TILE_SIZE = 16;
    private Image[] tileImages;

    public MapRender() {
        tileImages = new Image[20];
        for (int i = 0; i < 20; i++) {
            tileImages[i] = new Image(getClass().getResourceAsStream("/img/Map/tile_" + i + ".png"));
        }
    }

    public int[][] loadMap(String filename) throws Exception {
        List<int[]> rows = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.trim().split(" ");
            int[] row = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                row[i] = Integer.parseInt(tokens[i]);
            }
            rows.add(row);
        }
        reader.close();

        int[][] map = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            map[i] = rows.get(i);
        }
        return map;
    }

    public Group renderMap(int[][] map) {
        Group mapGroup = new Group();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                int tileType = map[y][x];
                ImageView tileView = new ImageView(tileImages[tileType]);
                tileView.setX(x * TILE_SIZE);
                tileView.setY(y * TILE_SIZE);
                mapGroup.getChildren().add(tileView);
            }
        }
        return mapGroup;
    }
}
