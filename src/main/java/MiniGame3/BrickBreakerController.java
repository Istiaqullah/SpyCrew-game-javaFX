package MiniGame3;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class BrickBreakerController {

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private double paddleX = 250;
    private double ballX = 300, ballY = 300;
    private double ballDX = 3, ballDY = -3;
    private boolean[][] bricks = new boolean[5][8];
    private int brickWidth = 75, brickHeight = 20;
    private int score = 0;
    private int lives = 3;
    private Timeline timeline;
    private boolean gameEnded = false;

    // For smooth movement
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private final double paddleSpeed = 8; // pixels per frame for smoothness

    @FXML
    public void initialize() {
        gc = gameCanvas.getGraphicsContext2D();
        resetBricks();
        drawGame();
        startGameLoop();

        // Attach key handler to scene
        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKeyPressed);
                newScene.setOnKeyReleased(this::handleKeyReleased);
                newScene.getRoot().requestFocus();
            }
        });
    }

    private void startGameLoop() {
        timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> updateGame()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void resetBricks() {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 8; j++)
                bricks[i][j] = true;
    }

    private void updateGame() {
        if (gameEnded) return;

        ballX += ballDX;
        ballY += ballDY;

        // Smooth paddle movement
        if (leftPressed && paddleX > 0) {
            paddleX -= paddleSpeed;
            if (paddleX < 0) paddleX = 0;
        }
        if (rightPressed && paddleX < gameCanvas.getWidth() - 100) {
            paddleX += paddleSpeed;
            if (paddleX > gameCanvas.getWidth() - 100) paddleX = gameCanvas.getWidth() - 100;
        }

        // Wall collisions
        if (ballX <= 0 || ballX >= gameCanvas.getWidth() - 10) ballDX *= -1;
        if (ballY <= 0) ballDY *= -1;

        // Paddle collision
        if (ballY >= 480 && ballX >= paddleX && ballX <= paddleX + 100) {
            ballDY *= -1;
        }

        // Brick collision
        int remainingBricks = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 8; j++) {
                if (bricks[i][j]) {
                    remainingBricks++;
                    double bx = j * (brickWidth + 5) + 35;
                    double by = i * (brickHeight + 5) + 30;
                    if (ballX + 10 > bx && ballX < bx + brickWidth && ballY + 10 > by && ballY < by + brickHeight) {
                        bricks[i][j] = false;
                        ballDY *= -1;
                        score += 1; // Add 1 point per brick
                    }
                }
            }
        }

        // Win condition
        if (remainingBricks == 0) {
            gameEnded = true;
            drawGame();
            drawGameOver("🎉 You Win! 🎉\nScore: " + score);
            return;
        }

        // Lose life
        if (ballY > gameCanvas.getHeight()) {
            lives--;
            if (lives > 0) {
                resetBall();
            } else {
                gameEnded = true;
                drawGame();
                drawGameOver("💀 Game Over! 💀\nScore: " + score);
                return;
            }
        }

        drawGame();
    }

    private void resetBall() {
        ballX = 300;
        ballY = 300;
        ballDX = 3;
        ballDY = -3;
    }

    private void drawGame() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Draw paddle
        gc.setFill(Color.BLUE);
        gc.fillRect(paddleX, 480, 100, 10);

        // Draw ball
        gc.setFill(Color.WHITE);
        gc.fillOval(ballX, ballY, 10, 10);

        // Draw bricks
        gc.setFill(Color.ORANGE);
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 8; j++)
                if (bricks[i][j])
                    gc.fillRect(j * (brickWidth + 5) + 35, i * (brickHeight + 5) + 30, brickWidth, brickHeight);

        // Draw score and lives
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 20, 20);
        gc.fillText("Lives: " + lives, 550, 20);
    }

    private void drawGameOver(String message) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        gc.setFill(Color.RED);
        gc.setFont(gc.getFont()); // Use default font
        String[] lines = message.split("\n");
        double x = gameCanvas.getWidth() / 2 - 80;
        double y = gameCanvas.getHeight() / 2 - 20;
        for (int i = 0; i < lines.length; i++) {
            gc.fillText(lines[i], x, y + i * 30);
        }
    }

    // Handle key press (set flag for smooth movement)
    public void handleKeyPressed(KeyEvent event) {
        if (gameEnded) return;
        if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
            leftPressed = true;
        }
        if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
            rightPressed = true;
        }
    }

    // Handle key release (unset flag)
    public void handleKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
            leftPressed = false;
        }
        if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
            rightPressed = false;
        }
    }
}