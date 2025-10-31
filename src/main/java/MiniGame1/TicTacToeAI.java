package MiniGame1;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeAI extends Application {

    private Button[][] buttons = new Button[3][3];
    private char[][] board = new char[3][3];
    private boolean playerTurn = true; // Human = X, AI = O
    private int playerPoints = 0; // Player score

    @Override
    public void start(Stage stage) {
        GridPane grid = new GridPane();

        // Initialize board
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = ' ';
                Button btn = new Button(" ");
                btn.setFont(new Font(24));
                btn.setPrefSize(100, 100);

                final int r = row, c = col;
                btn.setOnAction(e -> handleMove(r, c));

                buttons[row][col] = btn;
                grid.add(btn, col, row);
            }
        }

        Scene scene = new Scene(grid, 320, 320);
        stage.setScene(scene);
        stage.setTitle("Tic Tac Toe");
        stage.show();
    }

    // Handle human move
    private void handleMove(int row, int col) {
        if (board[row][col] == ' ' && playerTurn) {
            board[row][col] = 'X';
            buttons[row][col].setText("X");

            if (checkWin('X')) {
                playerPoints += 4;
                showWinner("You Win! 🎉\nYour Points: " + playerPoints);
                disableBoard();
                return;
            }

            if (isDraw()) {
                showWinner("Draw!");
                return;
            }

            playerTurn = false;
            aiMove();
        }
    }

    // Easy AI: random move
    private void aiMove() {
        // Collect all empty cells
        List<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c] == ' ') {
                    emptyCells.add(new int[]{r, c});
                }
            }
        }

        // Pick a random empty cell
        if (!emptyCells.isEmpty()) {
            int[] move = emptyCells.get((int) (Math.random() * emptyCells.size()));
            int r = move[0];
            int c = move[1];

            board[r][c] = 'O';
            buttons[r][c].setText("O");

            if (checkWin('O')) {
                showWinner("AI Wins! 😢");
                disableBoard();
                return;
            }
            if (isDraw()) {
                showWinner("Draw!");
                return;
            }
        }

        playerTurn = true; // back to player
    }

    // Check if a player has won
    private boolean checkWin(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true;
        }
        return (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
                (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    private boolean isDraw() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (board[r][c] == ' ') return false;
        return true;
    }

    // Disable all buttons after game ends
    private void disableBoard() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                buttons[r][c].setDisable(true);
    }

    // Show winner in a popup
    private void showWinner(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
