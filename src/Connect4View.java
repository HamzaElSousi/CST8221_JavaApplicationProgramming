import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The {@code Connect4View} class represents the graphical user interface (GUI) for the Connect4 game.
 * It handles the visualization of the game board, status updates, and chat interactions. This class
 * incorporates various GUI components like buttons for the game board, text areas for chat, and labels
 * for displaying the current player and timer.
 * <p>
 * The class supports changing languages for the GUI elements through resource bundles and provides
 * methods for updating the game board, status, timer, and chat based on game progress. It is designed
 * to be used in conjunction with {@code Connect4Controller} and {@code Connect4Model} as part of an
 * MVC design pattern.
 * </p>
 *
 * <p><b>Student names and IDs:</b>
 * <ul>
 * <li>Hamza El Sousi, 040982818</li>
 * <li>Mansi Joshi, 041091664</li>
 * </ul>
 * </p>
 * <p><b>Lab Professor:</b> Paulo Sousa</p>
 * <p><b>Assignment:</b> A22</p>
 * <p><b>MVC Design:</b> VIEW</p>
 *
 * @see javax.swing.JFrame
 * @see java.awt.event.ActionListener
 */
public class Connect4View {
	private JFrame frame;
	private JButton[][] buttons;
	private JTextArea chatArea;
	private JTextField chatInput;
	private JPanel statusPanel;
	private JLabel currentPlayerLabel;
	private JLabel timerLabel;
	private JMenuBar menuBar;
	private JMenuItem restartItem, exitItem;
	private JMenu languageMenu, networkMenu;
	private JMenuItem englishItem, arabicItem, connectItem, disconnectItem;
	private ResourceBundle bundle;

	/**
	 * Initializes a new instance of the {@code Connect4View} class by loading the
	 * default resource bundle (English) and setting up the GUI components.
	 */
	public Connect4View() {
		// Load the default resource bundle (English)
		bundle = ResourceBundle.getBundle("resources.Connect4Bundle", Locale.ENGLISH);
		createAndShowGUI();
	}

	/**
	 * Returns the main frame of the game view.
	 * 
	 * @return The {@code JFrame} representing the main window of the game.
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Creates and displays the GUI components including the game board, status
	 * panel, and chat panel.
	 */
	private void createAndShowGUI() {
		frame = new JFrame(bundle.getString("title"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		initializeMenuBar();
		initializeBoardPanel();
		initializeRightPanel();

		frame.pack();
		frame.setLocationRelativeTo(null); // Center the window
		frame.setVisible(true);
		frame.setSize(new Dimension(800, 500));
		frame.setResizable(true);
		System.out.println("Current size: " + frame.getWidth() + "," + frame.getHeight());
	}

	private void initializeMenuBar() {
		menuBar = new JMenuBar();

		// Game menu
		JMenu gameMenu = new JMenu(bundle.getString("game"));
		restartItem = new JMenuItem(bundle.getString("restart"));
		exitItem = new JMenuItem(bundle.getString("exit"));
		gameMenu.add(restartItem);
		gameMenu.add(exitItem);
		menuBar.add(gameMenu);

		// Language menu
		languageMenu = new JMenu(bundle.getString("language_menu"));
		englishItem = new JMenuItem(bundle.getString("english"));
		arabicItem = new JMenuItem(bundle.getString("arabic"));
		languageMenu.add(englishItem);
		languageMenu.add(arabicItem);
		menuBar.add(languageMenu);

		// Network menu
		networkMenu = new JMenu(bundle.getString("network_menu"));
		connectItem = new JMenuItem(bundle.getString("connect"));
		disconnectItem = new JMenuItem(bundle.getString("disconnect"));
		networkMenu.add(connectItem);
		networkMenu.add(disconnectItem);
		menuBar.add(networkMenu);

		frame.setJMenuBar(menuBar);

		initializeLanguageMenu(); // Set action listeners for language menu items
	}

	private void initializeLanguageMenu() {
		englishItem.addActionListener(e -> updateLanguage(Locale.ENGLISH));
		arabicItem.addActionListener(e -> updateLanguage(new Locale("ar", "AR")));
	}

	private void updateLanguage(Locale locale) {
		bundle = ResourceBundle.getBundle("resources.Connect4Bundle", locale);

		// Update all components with new language
		frame.setTitle(bundle.getString("title"));
		restartItem.setText(bundle.getString("restart"));
		exitItem.setText(bundle.getString("exit"));
		languageMenu.setText(bundle.getString("language_menu"));
		networkMenu.setText(bundle.getString("network_menu"));
		connectItem.setText(bundle.getString("connect"));
		disconnectItem.setText(bundle.getString("disconnect"));
		currentPlayerLabel.setText(bundle.getString("current_player"));
		timerLabel.setText(bundle.getString("timer"));
		// Update other text components as needed
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

		statusPanel = new JPanel();
		statusPanel.setLayout(new GridLayout(2, 1));

		currentPlayerLabel = new JLabel(bundle.getString("current_player"), SwingConstants.CENTER);
		currentPlayerLabel.setBorder(BorderFactory.createTitledBorder(bundle.getString("current_player")));
		statusPanel.add(currentPlayerLabel);

		timerLabel = new JLabel(bundle.getString("timer"), SwingConstants.CENTER);
		timerLabel.setBorder(BorderFactory.createTitledBorder(bundle.getString("timer")));
		statusPanel.add(timerLabel);

		rightPanel.add(statusPanel, BorderLayout.NORTH);

		initializeChatPanel(rightPanel);
		frame.add(rightPanel, BorderLayout.EAST);
	}

	private void initializeChatPanel(JPanel rightPanel) {
		JPanel chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout());
		chatPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("chat")));

		chatArea = new JTextArea(20, 30);
		chatArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(chatArea);
		chatInput = new JTextField();

		chatPanel.add(scrollPane, BorderLayout.CENTER);
		chatPanel.add(chatInput, BorderLayout.SOUTH);

		rightPanel.add(chatPanel, BorderLayout.CENTER);
	}

	// Setters for action listeners

	/**
	 * Sets an action listener for a specific button on the game board. This allows
	 * for custom behavior when the button is clicked, typically used to handle game
	 * moves.
	 * 
	 * @param row      The row index of the button.
	 * @param column   The column index of the button.
	 * @param listener The action listener to be attached to the button.
	 */
	public void setActionListenerForButton(int row, int column, ActionListener listener) {
		buttons[row][column].addActionListener(listener);
	}

	/**
	 * Sets an action listener for the chat input text field. This is typically used
	 * to handle sending chat messages.
	 * 
	 * @param listener The action listener to be attached to the chat input field.
	 */
	public void setActionListenerForChatInput(ActionListener listener) {
		chatInput.addActionListener(listener);
	}

	/**
	 * Sets an action listener for the restart menu item. This allows for custom
	 * behavior when the restart option is selected, typically used to restart the
	 * game.
	 * 
	 * @param listener The action listener to be attached to the restart item.
	 */
	public void setActionListenerForRestartItem(ActionListener listener) {
		restartItem.addActionListener(listener);
	}

	/**
	 * Sets an action listener for the exit menu item. This allows for custom
	 * behavior when the exit option is selected, typically used to exit the game.
	 * 
	 * @param listener The action listener to be attached to the exit item.
	 */
	public void setActionListenerForExitItem(ActionListener listener) {
		exitItem.addActionListener(listener);
	}

	// Additional private helper methods and public setter methods would also be
	// documented in a similar fashion.

	/**
	 * Updates the visual representation of a specific button on the game board to
	 * reflect a player's move. This includes setting the text of the button to show
	 * the player's symbol and disabling the button to prevent further moves on it.
	 * 
	 * @param row    The row index of the button to update.
	 * @param column The column index of the button to update.
	 * @param player The symbol of the player making the move.
	 */
	// Methods for updating the view
    public void updateBoard(int row, int column, char player) {
        JButton button = buttons[row][column];
        ImageIcon icon;
        if (player == 'R') {
            // Load the red disc image
            icon = new ImageIcon(getClass().getResource("Red.gif"));
        } else {
            // Load the yellow disc image
            icon = new ImageIcon(getClass().getResource("Yellow.gif"));
        }
        // Set the image icon to the button
        button.setIcon(icon);
        //button.setEnabled(false);
        button.revalidate();
        button.repaint();

    }

	/**
	 * Resets the game board to its initial state. This includes clearing all button
	 * texts and enabling them for new moves. It is typically called at the start of
	 * a new game or when the game is restarted.
	 */
    public void resetBoard() {
        for (int i = 0; i < Connect4Model.getRows(); i++) {
            for (int j = 0; j < Connect4Model.getColumns(); j++) {
                buttons[i][j].setIcon(null); // Remove the icon from the button
                buttons[i][j].setEnabled(true);
                buttons[i][j].setBackground(Color.WHITE);
            }
        }
    }


	/**
	 * Updates the status label to display a given message. This method is typically
	 * used to show which player's turn it is or to display game status messages.
	 * 
	 * @param text The message to be displayed in the status label.
	 */
	public void updateStatus(String text) {
		currentPlayerLabel.setText(text);
	}

	/**
	 * Updates the timer label to display the elapsed time in seconds since the
	 * start of the game.
	 * 
	 * @param timeInSeconds The elapsed time in seconds.
	 */
	public void updateTimerLabel(int timeInSeconds) {
		// Update the timer label with the elapsed time
		timerLabel.setText(bundle.getString("timer") + ": " + timeInSeconds + " " + bundle.getString("seconds"));
	}

	/**
	 * Disables all buttons on the game board. This method is typically called when the game has ended to prevent further moves.
	 */
	public void disableAllButtons() {
		for (int i = 0; i < Connect4Model.getRows(); i++) {
			for (int j = 0; j < Connect4Model.getColumns(); j++) {
				buttons[i][j].setEnabled(false);
			}
		}
	}

	/**
	 * Appends a message to the chat area. This method is typically used to display chat messages from players.
	 * 
	 * @param message The message to be added to the chat area.
	 */
	public void appendChat(String message) {
		chatArea.append(message + "\n");
	}

	/**
	 * Clears the chat input field. This is typically called after a chat message has been sent.
	 */
	public void clearChatInput() {
		chatInput.setText("");
	}

	/**
	 * Gets the text currently entered in the chat input field.
	 * 
	 * @return The text from the chat input field.
	 */
	public String getChatInput() {
		return chatInput.getText();
	}
}
