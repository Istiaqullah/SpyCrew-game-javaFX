package main;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MapRender {
    public static final int TILE_SIZE = 32; // 2x scale
    private Image[] tileImages;

    public MapRender() {
        tileImages = new Image[23];
        for (int i = 0; i < 23; i++) {
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

    public Group renderMap(int[][] map, Camera camera, int playerX, int playerY, ImageView player, Label nameLabel) {
        Group mapGroup = new Group();
        int camX = camera.getCameraX();
        int camY = camera.getCameraY();
        int camW = camera.getCameraWidth();
        int camH = camera.getCameraHeight();

        int tileSize = camera.getTileSize();

        int startTileX = camX / tileSize;
        int startTileY = camY / tileSize;
        int endTileX = Math.min(map[0].length, startTileX + camW);
        int endTileY = Math.min(map.length, startTileY + camH);

        int offsetX = -(camX % tileSize);
        int offsetY = -(camY % tileSize);

        for (int y = startTileY; y < endTileY; y++) {
            for (int x = startTileX; x < endTileX; x++) {
                int tileType = map[y][x];
                ImageView tileView = new ImageView(tileImages[tileType]);
                tileView.setFitWidth(tileSize);
                tileView.setFitHeight(tileSize);
                tileView.setX((x - startTileX) * tileSize + offsetX);
                tileView.setY((y - startTileY) * tileSize + offsetY);
                mapGroup.getChildren().add(tileView);
            }
        }

        int playerPixelX = playerX * tileSize;
        int playerPixelY = playerY * tileSize;
        int playerScreenX = playerPixelX - camX;
        int playerScreenY = playerPixelY - camY;

        player.setFitWidth(tileSize);
        player.setFitHeight(tileSize);
        player.setX(playerScreenX);
        player.setY(playerScreenY);

        nameLabel.setLayoutX(playerScreenX - (nameLabel.getPrefWidth() - tileSize) / 2);
        nameLabel.setLayoutY(playerScreenY - nameLabel.getPrefHeight());

        mapGroup.getChildren().add(player);
        mapGroup.getChildren().add(nameLabel);

        return mapGroup;
    }
}