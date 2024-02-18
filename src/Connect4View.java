/*
 * Student name && ID : Hamza El Sousi , 040982818
 * Student name && ID : Mansi Joshi , 041091664
 * Assignment:
 * Lab Prof: Paulo Sousa
 * Assignment: A12
 * Lab Prof: Paulo Sousa
 * MVC Desgin: VIEW
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * The {@code Connect4View} class is responsible for the graphical user interface of the Connect Four game.
 * It includes the game board, status labels, chat area, and menu items. This class follows the MVC design pattern
 * by acting as the View component, which interacts with the Model to reflect the game's current state and
 * receives user input to be processed by the Controller.
 * 
 */
public class Connect4View {
    private JFrame frame;
    private JButton[][] buttons;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JLabel redChipsLabel;
    private JLabel yellowChipsLabel;
    private JLabel timeLabel;
    private JMenuBar menuBar;
    private JMenuItem restartItem, exitItem;
    private long startTime;
    
    /**
     * Constructs a new Connect4View instance and initializes the GUI.
     */
    public Connect4View() {
        createAndShowGUI();
    }

    /**
     * Initializes and displays the GUI components of the Connect Four game. This method sets up the main
     * frame, game board, status panel, chat panel, and menu items. It ensures that the GUI is responsive
     * and user-friendly.
     */
    private void createAndShowGUI() {
        frame = new JFrame("Connect Four");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        initializeMenuBar();
        initializeBoardPanel();
        initializeRightPanel();

        frame.pack();
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        
        frame.pack();
        frame.setSize(new Dimension(800, 500));
        frame.setVisible(true);
        frame.setResizable(true);
        System.out.println("Current size: "+ frame.getWidth() + "," + frame.getHeight());
    }

    /**
     * Initializes the menu bar and its items, including game controls like restart and exit.
     */
    private void initializeMenuBar() {
        menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        restartItem = new JMenuItem("Restart");
        exitItem = new JMenuItem("Exit");

        gameMenu.add(restartItem);
        gameMenu.add(exitItem);
        menuBar.add(gameMenu);

        frame.setJMenuBar(menuBar);
    }

    /**
     * Initializes the game board with buttons for each cell, allowing players to interact with the game.
     * The layout and appearance of the board are set up here.
     */
    private void initializeBoardPanel() {
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(Connect4Model.getRows(), Connect4Model.getColumns()));
        buttons = new JButton[Connect4Model.getRows()][Connect4Model.getColumns()];

        for (int i = 0; i < Connect4Model.getRows(); i++) {
            for (int j = 0; j < Connect4Model.getColumns(); j++) {
                JButton button = new JButton();
                button.setOpaque(true);
                button.setBackground(Color.WHITE);
                buttons[i][j] = button;
                boardPanel.add(button);
            }
        }
        frame.add(boardPanel, BorderLayout.CENTER);
    }

    /**
     * Initializes the right panel of the GUI, which includes the status panel and the chat panel.
     * This method sets up the layout and components used to display game status and chat messages.
     */
    private void initializeRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        statusPanel = new JPanel(new GridLayout(0, 1));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Current Status"));

        statusLabel = new JLabel("Player R's turn");
        statusPanel.add(statusLabel);

        redChipsLabel = new JLabel("Red Chips Played: 0");
        statusPanel.add(redChipsLabel);

        yellowChipsLabel = new JLabel("Yellow Chips Played: 0");
        statusPanel.add(yellowChipsLabel);

        timeLabel = new JLabel("Time Taken: 0 ms");
        statusPanel.add(timeLabel);

        rightPanel.add(statusPanel, BorderLayout.NORTH);

        initializeChatPanel(rightPanel);
        frame.add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Initializes the chat panel for displaying messages and allowing players to send messages.
     */
    private void initializeChatPanel(JPanel rightPanel) {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder("Chat"));

        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        chatInput = new JTextField();

        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);

        rightPanel.add(chatPanel, BorderLayout.CENTER);
    }

    // Setters for action listeners
    public void setActionListenerForButton(int row, int column, ActionListener listener) {
        buttons[row][column].addActionListener(listener);
    }

    public void setActionListenerForChatInput(ActionListener listener) {
        chatInput.addActionListener(listener);
    }

    public void setActionListenerForRestartItem(ActionListener listener) {
        restartItem.addActionListener(listener);
    }

    public void setActionListenerForExitItem(ActionListener listener) {
        exitItem.addActionListener(listener);
    }

    // Methods for updating the view
    /**
     * Updates the appearance and state of the board cell at the specified row and column to reflect
     * the player's move.
     * 
     * @param row the row index of the cell to update
     * @param column the column index of the cell to update
     * @param player the character representing the player's move ('R' for red, 'Y' for yellow)
     */
    public void updateBoard(int row, int column, char player) {
        JButton button = buttons[row][column];
        button.setText(String.valueOf(player));
        button.setEnabled(false);
    }

    
    public void resetBoard() {
        for (int i = 0; i < Connect4Model.getRows(); i++) {
            for (int j = 0; j < Connect4Model.getColumns(); j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                buttons[i][j].setBackground(Color.WHITE);
            }
        }
    }

    public void updateStatus(String text, int redChipsPlayed, int yellowChipsPlayed, long timeTaken) {
        statusLabel.setText(text);
        redChipsLabel.setText("Red Chips Played: " + redChipsPlayed);
        yellowChipsLabel.setText("Yellow Chips Played: " + yellowChipsPlayed);
        timeLabel.setText("Time Taken: " + timeTaken + " ms");
    }

    public void disableAllButtons() {
        for (int i = 0; i < Connect4Model.getRows(); i++) {
            for (int j = 0; j < Connect4Model.getColumns(); j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    public void appendChat(String message) {
        chatArea.append(message + "\n");
    }

    public void clearChatInput() {
        chatInput.setText("");
    }

    public String getChatInput() {
        return chatInput.getText();
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Calculates and returns the number of red chips played on the board.
     * 
     * @param model the Connect4Model instance used to access the game state
     * @return the number of red chips played
     */
    public int getRedChipsPlayed(Connect4Model model) {
        int redChips = 0;
        for (int i = 0; i < Connect4Model.getRows(); i++) {
            for (int j = 0; j < Connect4Model.getColumns(); j++) {
                if (model.getBoardValue(i, j) == 'R') {
                    redChips++;
                }
            }
        }
        return redChips;
    }

    /**
     * Calculates and returns the number of yellow chips played on the board.
     * 
     * @param model the Connect4Model instance used to access the game state
     * @return the number of yellow chips played
     */
    public int getYellowChipsPlayed(Connect4Model model) {
        int yellowChips = 0;
        for (int i = 0; i < Connect4Model.getRows(); i++) {
            for (int j = 0; j < Connect4Model.getColumns(); j++) {
                if (model.getBoardValue(i, j) == 'Y') {
                    yellowChips++;
                }
            }
        }
        return yellowChips;
    }

    /**
     * Returns the current status text displayed in the status label.
     * 
     * @return the current status text
     */
    public String getStatusText() {
        return statusLabel.getText();
    }

}
