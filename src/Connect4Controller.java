import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 * The {@code Connect4Controller} class serves as the controller in the MVC (Model-View-Controller)
 * design pattern for the Connect4 game. It is responsible for managing the interaction between the
 * game's model ({@code Connect4Model}) and view ({@code Connect4View}), including handling user
 * actions and updating the view based on changes in the model.
 *
 * <p>This class initializes action listeners for the game's GUI components, such as the grid buttons,
 * chat input, and menu items. It also controls the game's timer through a {@code ControllableTimer} instance.</p>
 *
 * <p><b>Student names and IDs:</b>
 * <ul>
 * <li>Hamza El Sousi, 040982818</li>
 * <li>Mansi Joshi, 041091664</li>
 * </ul></p>
 *
 * <p><b>Lab Professors:</b> Paulo Sousa & Daniel Cormier</p>
 * <p><b>Assignment:</b> A22</p>
 * <p><b>MVC Design:</b> CONTROLLER</p>
 */

public class Connect4Controller {
    private Connect4Model model;
    private Connect4View view;
    private ControllableTimer timer;

    /**
     * Constructs a {@code Connect4Controller} with specified model and view components.
     * Initializes the game's GUI by setting up action listeners for various interactive
     * components and starting the game timer.
     *
     * @param model the model component of the MVC design pattern, representing the game's state
     * @param view the view component of the MVC design pattern, representing the game's GUI
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
        this.view.setActionListenerForRestartItem(e -> restartGame());
        this.view.setActionListenerForExitItem(e -> System.exit(0));
        
        // Initialize and start the timer
        timer = new ControllableTimer(view);
        timer.start();
    }

    /**
     * Restarts the game by reinitializing the game board in both the model and view, and resetting
     * the timer. It also updates the game status to indicate which player's turn is next.
     */
    private void restartGame() {
        model.initializeBoard();
        view.resetBoard();
        view.updateStatus("Player R's turn");
        // Reset the timer
        timer.setStatus(ControllableTimer.RESET);
    }

    /**
     * The {@code ButtonListener} class implements {@code ActionListener} to handle actions
     * performed on the game board's buttons. Each button press results in a move being made
     * in the game, with subsequent checks for a win or draw condition.
     */
    private class ButtonListener implements ActionListener {
        @SuppressWarnings("unused")//used
		private final int row;
        private final int column;

        /**
         * Constructs a {@code ButtonListener} for a specific row and column on the game board.
         *
         * @param row the row index of the button
         * @param column the column index of the button
         */
        public ButtonListener(int row, int column) {
            this.row = row;
            this.column = column;
        }

        /**
         * Responds to button actions by making a move in the game model, updating the game view,
         * and checking for game end conditions.
         *
         * @param e the event object representing the action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            int nextRow = model.findNextRow(column);
            if (nextRow != -1) {
                model.makeMove(column);
                view.updateBoard(nextRow, column, model.getCurrentPlayer());
                if (model.checkWinner()) {
                    String winner = "Player " + model.getCurrentPlayer() + " wins!";
                    JOptionPane.showMessageDialog(view.getFrame(), winner, "Game Over", JOptionPane.INFORMATION_MESSAGE);
                    view.disableAllButtons();
                    // Stop the timer
                    timer.setStatus(ControllableTimer.STOP);
                } else if (model.isDraw()) {
                    JOptionPane.showMessageDialog(view.getFrame(), "The game is a draw!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                    view.disableAllButtons();
                    // Stop the timer
                    timer.setStatus(ControllableTimer.STOP);
                } else {
                    model.switchPlayer();
                    view.updateStatus("Player " + model.getCurrentPlayer() + "'s turn");
                }
            }
        }
    }

    /**
     * The {@code ChatListener} class implements {@code ActionListener} to handle actions performed
     * in the chat input field. It sends the chat message to the game's chat area.
     */
    private class ChatListener implements ActionListener {
        @Override
        /**
         * Responds to chat input actions by appending the input message to the game's chat area
         * and clearing the input field for new messages.
         *
         * @param e the event object representing the action event
         */
        public void actionPerformed(ActionEvent e) {
            String message = view.getChatInput();
            view.appendChat("Player: " + message);
            view.clearChatInput();
        }
    }
}
