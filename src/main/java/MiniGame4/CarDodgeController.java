package MiniGame4;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class CarDodgeController {
    @FXML
    private Canvas gameCanvas;

    private double carX;
    private final double carWidth = 40;
    private final double carHeight = 60;
    private final double carY = 520;

    private final double laneWidth = 100;
    private final int laneCount = 3;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private final ArrayList<double[]> obstacles = new ArrayList<>(); // [x, y, width, height]
    private final Random random = new Random();

    private int score = 0;
    private boolean gameOver = false;

    public void initialize() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        carX = (gameCanvas.getWidth() - carWidth) / 2;

        double canvasWidth = gameCanvas.getWidth();
        double canvasHeight = gameCanvas.getHeight();

        // Key controls: A for left, D for right
        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A) leftPressed = true;   // A key moves left
            if (e.getCode() == KeyCode.D) rightPressed = true;  // D key moves right
        });
        gameCanvas.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.A) leftPressed = false;
            if (e.getCode() == KeyCode.D) rightPressed = false;
        });

        AnimationTimer timer = new AnimationTimer() {
            long lastSpawn = 0;

            @Override
            public void handle(long now) {
                if (!gameOver) {
                    // Move car with proper boundaries
                    if (leftPressed) {
                        carX -= 5;
                        if (carX < 0) carX = 0;
                    }
                    if (rightPressed) {
                        carX += 5;
                        if (carX > canvasWidth - carWidth)
                            carX = canvasWidth - carWidth;
                    }

                    // Spawn obstacles every second
                    if (now - lastSpawn > 1_000_000_000) {
                        lastSpawn = now;
                        int lane = random.nextInt(laneCount);
                        double obsWidth = 60;   // wider obstacles
                        double obsHeight = 80;  // taller obstacles
                        obstacles.add(new double[]{lane * laneWidth + 20, -obsHeight, obsWidth, obsHeight});
                    }

                    // Move obstacles and check collisions
                    Iterator<double[]> iterator = obstacles.iterator();
                    while (iterator.hasNext()) {
                        double[] obs = iterator.next();

                        // Gradually increase speed based on score
                        double baseSpeed = 5;
                        double speedIncrement = 0.1;
                        double obstacleSpeed = baseSpeed + score * speedIncrement;

                        obs[1] += obstacleSpeed;

                        // Remove obstacle if passed bottom
                        if (obs[1] > canvasHeight) {
                            iterator.remove();
                            score++;
                        }

                        // Collision detection
                        if (obs[1] + obs[3] >= carY && obs[1] <= carY + carHeight &&
                                obs[0] + obs[2] >= carX && obs[0] <= carX + carWidth) {
                            gameOver = true;
                        }
                    }
                }

                // Draw everything
                gc.setFill(Color.LIGHTGRAY);
                gc.fillRect(0, 0, canvasWidth, canvasHeight);

                gc.setFill(Color.DARKGRAY);
                for (int i = 1; i < laneCount; i++)
                    gc.fillRect(i * laneWidth - 2, 0, 4, canvasHeight);

                // Draw car
                gc.setFill(Color.BLUE);
                gc.fillRect(carX, carY, carWidth, carHeight);

                // Draw obstacles
                gc.setFill(Color.RED);
                for (double[] obs : obstacles)
                    gc.fillRect(obs[0], obs[1], obs[2], obs[3]);

                // Draw score
                gc.setFill(Color.BLACK);
                gc.setFont(new Font(20));
                gc.fillText("Score: " + score, 10, 20);

                // If game over, show final score on canvas
                if (gameOver) {
                    gc.setFill(Color.BLACK);
                    gc.setFont(new Font(40));
                    gc.fillText("GAME OVER!", canvasWidth / 2 - 120, canvasHeight / 2 - 20);
                    gc.fillText("Score: " + score, canvasWidth / 2 - 80, canvasHeight / 2 + 40);
                }
            }
        };
        timer.start();
}
}