import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Connect4GUI {
    private JFrame frame;
    private JButton[][] buttons;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JLabel statusLabel;
    private Connect4Game game;

    public Connect4GUI() {
        game = new Connect4Game();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Connect Four");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        initializeMenuBar(); // Initialize the menu bar
        initializeBoardPanel();
        initializeRightPanel();
        
        frame.pack();
        frame.setVisible(true);
    }
    
    private void initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");
        JMenuItem restartItem = new JMenuItem("Restart");
        restartItem.addActionListener(e -> restartGame());
        //i18n means internationalization
        JMenuItem i18n = new JMenuItem("Language");
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(restartItem);
        gameMenu.add(exitItem);
        menuBar.add(gameMenu);

        frame.setJMenuBar(menuBar);
    }


    private void restartGame() {
        game = new Connect4Game(); // Reset the game logic
        for (int i = 0; i < Connect4Game.getRows(); i++) {
            for (int j = 0; j < Connect4Game.getColumns(); j++) {
                buttons[i][j].setText(""); // Clear the text on the buttons
                buttons[i][j].setEnabled(true); // Enable the buttons
            }
        }
        statusLabel.setText("Player R's turn"); // Reset the status label
    }


	private void initializeRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        statusLabel = new JLabel("Player R's turn", SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createTitledBorder("Current Player"));
        rightPanel.add(statusLabel, BorderLayout.NORTH);

        initializeChatPanel(rightPanel);
        
        frame.add(rightPanel, BorderLayout.EAST);
    }


    private void initializeBoardPanel() {
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(Connect4Game.getRows(), Connect4Game.getColumns()));
        buttons = new JButton[Connect4Game.getRows()][Connect4Game.getColumns()];
        for (int i = 0; i < Connect4Game.getRows(); i++) {
            for (int j = 0; j < Connect4Game.getColumns(); j++) {
                JButton button = new JButton();
                buttons[i][j] = button;
                button.addActionListener(new ButtonListener(i, j));
                boardPanel.add(button);
            }
        }
        frame.add(boardPanel, BorderLayout.CENTER);
    }

    private void initializeChatPanel(JPanel rightPanel) {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder("Chat"));
        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        chatInput = new JTextField();
        chatInput.addActionListener(new ChatListener());

        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);

        rightPanel.add(chatPanel, BorderLayout.CENTER);
    }


    private void initializeStatusLabel() {
        statusLabel = new JLabel("Player R's turn");
        frame.add(statusLabel, BorderLayout.SOUTH);
    }

    private class ButtonListener implements ActionListener {
        private final int column;

        public ButtonListener(int row, int column) {
            this.column = column;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Find the next available row in this column
            int row = findNextRow(column);
            if (row != -1) {
                game.makeMove(column);
                buttons[row][column].setText(String.valueOf(game.getCurrentPlayer()));
                buttons[row][column].setEnabled(false); // Optional: disable button after a move

                if (game.checkWinner()) {
                    statusLabel.setText("Player " + game.getCurrentPlayer() + " wins!");
                    disableAllButtons(); // Optional: Disable all buttons after a win
                } else if (game.isDraw()) {
                    statusLabel.setText("The game is a draw!");
                    disableAllButtons(); // Optional: Disable all buttons after a draw
                } else {
                    game.switchPlayer();
                    statusLabel.setText("Player " + game.getCurrentPlayer() + "'s turn");
                }
            }
        }

        private int findNextRow(int column) {
            for (int i = Connect4Game.getRows() - 1; i >= 0; i--) {
                if (game.getBoardValue(i, column) == '.') {
                    return i;
                }
            }
            return -1; // Column is full
        }

        private void disableAllButtons() {
            for (int i = 0; i < Connect4Game.getRows(); i++) {
                for (int j = 0; j < Connect4Game.getColumns(); j++) {
                    buttons[i][j].setEnabled(false);
                }
            }
        }
    }


    private class ChatListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = chatInput.getText();
            chatArea.append("Player: " + message + "\n");
            chatInput.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Connect4GUI());
    }
}
