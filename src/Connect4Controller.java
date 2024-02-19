/*
 * Student name && ID : Hamza El Sousi , 040982818
 * Student name && ID : Mansi Joshi , 041091664
 * Lab Prof: Paulo Sousa
 * Assignment: A12
 * Lab Prof: Paulo Sousa
 * MVC Desgin: CONTROLLER
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * The {@code Connect4Controller} class acts as the controller in the MVC design pattern for the Connect Four game.
 * It handles user interactions by managing action listeners for the game's UI components, such as buttons for the game board,
 * chat input, and menu items. It also controls the game's state, including starting and stopping timers for player turns,
 * and updates the model and view based on player actions.
 */
public class Connect4Controller {
    private Connect4Model model;
    private Connect4View view;
    private Timer playerTimer;
    private long currentPlayerStartTime;

    /**
     * Constructs a {@code Connect4Controller} with specified model and view components, initializing the game's UI
     * action listeners and starting the game's timer.
     * 
     * @param model the game model
     * @param view the game view
     */
    public Connect4Controller(Connect4Model model, Connect4View view) {
        this.model = model;
        this.view = view;

        // Initialize action listeners for the grid buttons
        for (int i = 0; i < Connect4Model.getRows(); i++) {
            for (int j = 0; j < Connect4Model.getColumns(); j++) {
                this.view.setActionListenerForButton(i, j, new ButtonListener(i, j));
            }
        }

        // Initialize action listeners for chat and menu items
        this.view.setActionListenerForChatInput(new ChatListener());
        this.view.setActionListenerForRestartItem(new RestartListener());
        this.view.setActionListenerForExitItem(new ExitListener());

        // Initialize a single timer for both players
        playerTimer = new Timer(1000, new TimerListener());
    }

    /**
     * Restarts the game, reinitializing the board in the model and resetting the view and timer.
     */
    private void restartGame() {
        model.initializeBoard();
        view.resetBoard();
        view.updateStatus("Player 1's turn", 0, 0, 0); // Reset chip counts and time
    }

    /**
     * Starts the timer for the current player's turn.
     */
    private void startTimer() {
        currentPlayerStartTime = System.currentTimeMillis();
        playerTimer.start();
    }

    /**
     * Stops the timer.
     */
    private void stopTimer() {
        playerTimer.stop();
    }

    /**
     * Updates the timer display in the view with the elapsed time since the current player's turn started.
     */
    private void updateTimer() {
        long elapsedTime = System.currentTimeMillis() - currentPlayerStartTime;
        int elapsedSeconds = (int) (elapsedTime / 1000); // Convert milliseconds to seconds
        view.updateStatus("Player " + model.getCurrentPlayer() + "'s turn", model.getRedChipsPlayed(), model.getYellowChipsPlayed(), elapsedSeconds);
    }

    /**
     * Inner class to handle button clicks on the game board. It processes the player's move and updates
     * the model and view accordingly.
     */
    private class ButtonListener implements ActionListener {
        @SuppressWarnings("unused") //  used
		private final int row;
        private final int column;

        public ButtonListener(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int nextRow = model.findNextRow(column);
            if (nextRow != -1) {
                model.makeMove(column);
                view.updateBoard(nextRow, column, model.getCurrentPlayer());
                if (model.checkWinner()) {
                    stopTimer(); // Stop the timer when the game ends
                    view.updateStatus("Player " + model.getCurrentPlayer() + " wins!", model.getRedChipsPlayed(), model.getYellowChipsPlayed(), 0);
                    view.disableAllButtons();
                } else if (model.isDraw()) {
                    stopTimer(); // Stop the timer when the game ends
                    view.updateStatus("The game is a draw!", model.getRedChipsPlayed(), model.getYellowChipsPlayed(), 0);
                    view.disableAllButtons();
                } else {
                    model.switchPlayer();
                    startTimer(); // Start the timer for the next player's turn
                }
            }
        }
    }

    private class ChatListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = view.getChatInput();
            view.appendChat("Player: " + message);
            view.clearChatInput();
        }
    }

    private class RestartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            restartGame();
        }
    }

    private class ExitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateTimer();
        }
    }
}
