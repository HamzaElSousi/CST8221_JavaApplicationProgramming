/*
 * Student name && ID : Hamza El Sousi , 040982818
 * Student name && ID : Mansi Joshi , 041091664
 * Assignment:
 * Lab Prof: Paulo Sousa
 * Assignment: A12
 * Lab Prof: Paulo Sousa
 * MVC Desgin: MODEL 
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Connect4View {
    private JFrame frame;
    private JButton[][] buttons;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JLabel statusLabel;
    private JMenuBar menuBar;
    private JMenuItem restartItem, exitItem;

    public Connect4View() {
        createAndShowGUI();
    }

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
    }

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

    private void initializeRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        statusLabel = new JLabel("Player R's turn", SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createTitledBorder("Current Player"));
        rightPanel.add(statusLabel, BorderLayout.NORTH);

        initializeChatPanel(rightPanel);
        frame.add(rightPanel, BorderLayout.EAST);
    }

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

    public void updateStatus(String text) {
        statusLabel.setText(text);
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
}
