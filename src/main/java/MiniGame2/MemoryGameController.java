package MiniGame2;

import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class MemoryGameController {

    @FXML
    private GridPane grid;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label movesLabel;
    @FXML
    private Button restartButton;

    private final int gridSize = 4; // 4x4 grid = 8 pairs
    private final int maxMoves = 15; // max moves
    private int remainingMoves;

    private Button firstCard = null;
    private Button secondCard = null;
    private boolean canClick = true;
    private int score = 0;
    private int matchedPairs = 0;

    private List<String> cardValues;

    // Result callback for main game
    private Consumer<Integer> resultCallback = null;

    @FXML
    public void initialize() {
        setupCards();
    }

    private void setupCards() {
        grid.getChildren().clear();

        String[] emojis = {"🍎", "🍌", "🍇", "🍉", "🍒", "🍍", "🥝", "🍓"};
        cardValues = new ArrayList<>();

        for (String emoji : emojis) {
            cardValues.add(emoji);
            cardValues.add(emoji);
        }

        Collections.shuffle(cardValues);

        score = 0;
        matchedPairs = 0;
        remainingMoves = maxMoves;
        updateLabels();

        int index = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Button card = new Button();
                card.setPrefSize(130, 130);
                card.setStyle("-fx-font-size: 36px; -fx-background-color: #1e293b; -fx-text-fill: transparent;");
                String value = cardValues.get(index++);

                card.setOnAction(e -> flipCard(card, value));
                grid.add(card, j, i);
            }
        }
    }

    private void flipCard(Button card, String value) {
        if (!canClick || card == firstCard || card.isDisabled()) return;

        RotateTransition rotateOut = new RotateTransition(Duration.millis(150), card);
        rotateOut.setFromAngle(0);
        rotateOut.setToAngle(90);

        rotateOut.setOnFinished(e -> {
            card.setText(value);
            card.setStyle("-fx-font-size: 36px; -fx-background-color: #475569; -fx-text-fill: white;");

            RotateTransition rotateIn = new RotateTransition(Duration.millis(150), card);
            rotateIn.setFromAngle(270);
            rotateIn.setToAngle(360);
            rotateIn.play();

            if (firstCard == null) {
                firstCard = card;
            } else {
                secondCard = card;
                canClick = false;

                remainingMoves--;
                updateLabels();

                PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
                pause.setOnFinished(ev -> checkMatch());
                pause.play();
            }
        });

        rotateOut.play();
    }

    private void checkMatch() {
        String firstValue = firstCard.getText();
        String secondValue = secondCard.getText();

        if (firstValue.equals(secondValue)) {
            score += 5;
            matchedPairs++;
            firstCard.setDisable(true);
            secondCard.setDisable(true);
        } else {
            // Flip back unmatched cards
            flipBack(firstCard);
            flipBack(secondCard);
        }

        firstCard = null;
        secondCard = null;
        canClick = true;

        // Check for win or max moves reached
        if (matchedPairs == (gridSize * gridSize) / 2) {
            endGame("🎉 You Won!");
        } else if (remainingMoves == 0) {
            endGame("💀 Game Over");
        }
    }

    private void flipBack(Button card) {
        RotateTransition rotateOut = new RotateTransition(Duration.millis(150), card);
        rotateOut.setFromAngle(360);
        rotateOut.setToAngle(270);

        rotateOut.setOnFinished(e -> {
            card.setText("");
            card.setStyle("-fx-background-color: #1e293b;");
            RotateTransition rotateIn = new RotateTransition(Duration.millis(150), card);
            rotateIn.setFromAngle(90);
            rotateIn.setToAngle(0);
            rotateIn.play();
        });

        rotateOut.play();
    }

    private void updateLabels() {
        scoreLabel.setText("Score: " + score);
        movesLabel.setText("Moves Left: " + remainingMoves);
    }

    // Fixed: alert.showAndWait in Platform.runLater to avoid IllegalStateException
    private void endGame(String title) {
        if (resultCallback != null) {
            resultCallback.accept(score);
        }
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText("Final Score: " + score);
            alert.showAndWait();

            Window window = scoreLabel.getScene().getWindow();
            if (window != null) window.hide();
        });
    }

    @FXML
    private void restartGame() {
        setupCards();
    }

    // Setter for the result callback
    public void setResultCallback(Consumer<Integer> callback) {
        this.resultCallback = callback;
    }
}