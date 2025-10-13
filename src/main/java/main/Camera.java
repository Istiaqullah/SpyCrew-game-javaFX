package main;

public class Camera {
    private final int cameraWidth;
    private final int cameraHeight;
    private final int mapWidth;
    private final int mapHeight;
    private final int tileSize;

    private int cameraX;
    private int cameraY;

    public Camera(int cameraWidth, int cameraHeight, int mapWidth, int mapHeight, int tileSize) {
        this.cameraWidth = cameraWidth;
        this.cameraHeight = cameraHeight;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.tileSize = tileSize;
        this.cameraX = 0;
        this.cameraY = 0;
    }

    public void update(int playerX, int playerY) {
        int playerPixelX = playerX * tileSize + tileSize / 2;
        int playerPixelY = playerY * tileSize + tileSize / 2;

        int viewWidth = cameraWidth * tileSize;
        int viewHeight = cameraHeight * tileSize;

        cameraX = playerPixelX - viewWidth / 2;
        cameraY = playerPixelY - viewHeight / 2;

        cameraX = Math.max(0, Math.min(cameraX, mapWidth * tileSize - viewWidth));
        cameraY = Math.max(0, Math.min(cameraY, mapHeight * tileSize - viewHeight));
    }

    public int getCameraX() { return cameraX; }
    public int getCameraY() { return cameraY; }
    public int getViewWidth() { return cameraWidth * tileSize; }
    public int getViewHeight() { return cameraHeight * tileSize; }
    public int getTileSize() { return tileSize; }
    public int getCameraWidth() { return cameraWidth; }
    public int getCameraHeight() { return cameraHeight; }
}