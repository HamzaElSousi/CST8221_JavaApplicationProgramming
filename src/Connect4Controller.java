/*
 * Student name && ID : Hamza El Sousi , 040982818
 * Student name && ID : Mansi Joshi , 041091664
 * Lab Prof: Paulo Sousa
 * Assignment: A12
 * Lab Prof: Paulo Sousa
 * MVC Desgin: MODEL 
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Connect4Controller {
    private Connect4Model model;
    private Connect4View view;

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
    }

    private void restartGame() {
        model.initializeBoard();
        view.resetBoard();
        view.updateStatus("Player R's turn");
    }

    private class ButtonListener implements ActionListener {
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
                    view.updateStatus("Player " + model.getCurrentPlayer() + " wins!");
                    view.disableAllButtons();
                } else if (model.isDraw()) {
                    view.updateStatus("The game is a draw!");
                    view.disableAllButtons();
                } else {
                    model.switchPlayer();
                    view.updateStatus("Player " + model.getCurrentPlayer() + "'s turn");
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
}
