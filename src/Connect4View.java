import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * The {@code Connect4View} class represents the graphical user interface (GUI)
 * for the Connect4 game. It handles the visualization of the game board, status
 * updates, and chat interactions. This class incorporates various GUI
 * components like buttons for the game board, text areas for chat, and labels
 * for displaying the current player and timer.
 * <p>
 * The class supports changing languages for the GUI elements through resource
 * bundles and provides methods for updating the game board, status, timer, and
 * chat based on game progress. It is designed to be used in conjunction with
 * {@code Connect4Controller} and {@code Connect4Model} as part of an MVC design
 * pattern.
 * </p>
 *
 * <p>
 * <b>Student names and IDs:</b>
 * <ul>
 * <li>Hamza El Sousi, 040982818</li>
 * <li>Mansi Joshi, 041091664</li>
 * </ul>
 * </p>
 * <p>
 * <b>Lab Professor:</b> Paulo Sousa
 * </p>
 * <p>
 * <b>Assignment:</b> A22
 * </p>
 * <p>
 * <b>MVC Design:</b> VIEW
 * </p>
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
	private Client client;
	private JLabel connectionStatusLabel; // Declare it with other UI components



	/**
	 * Initializes a new instance of the {@code Connect4View} class by loading the
	 * default resource bundle (English) and setting up the GUI components.
	 */
	public Connect4View() {
		// Load the default resource bundle (English)
		bundle = ResourceBundle.getBundle("resources.Connect4Bundle", Locale.ENGLISH);
		createAndShowGUI();
	}

	public void setClient(Client client) {
        this.client = client;
        if (client != null) {
            client.setView(this);
        }
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
		connectItem.addActionListener(e -> {
		    // Prompt for server address
		    String host = JOptionPane.showInputDialog(frame, "Enter Server Address:", "Connect to Server", JOptionPane.QUESTION_MESSAGE);
		    if (host != null && !host.isEmpty()) {
		        // Prompt for port number
		        String portStr = JOptionPane.showInputDialog(frame, "Enter Server Port:", "Connect to Server", JOptionPane.QUESTION_MESSAGE);
		        int port = 0;
		        try {
		            port = Integer.parseInt(portStr);
		            if (port <= 0 || port > 65535) throw new NumberFormatException("Port out of range");
		            // Assuming you have a method to handle the connection logic
		            if (client != null) {
		                client.stopConnection(); // Ensure previous connections are closed
		            }
		            client = new Client(host, port, this); // Recreate the client with new host and port
		            client.startConnection(); // Attempt to connect
		            setClient(client); // Update the view's reference to the client
		            // The client itself should call updateConnectionStatus based on the connection result
		        } catch (NumberFormatException nfe) {
		            JOptionPane.showMessageDialog(frame, "Invalid port number. Please enter a number between 1 and 65535.", "Invalid Port", JOptionPane.ERROR_MESSAGE);
		        }
		    }
		});

		//		connectItem.setActionCommand("connect");
		networkMenu.add(disconnectItem);
		disconnectItem.addActionListener(e -> {
		    if (client != null) {
		        client.stopConnection(); // Disconnect from the server
		        updateConnectionStatus(false); // Update the view to show the disconnection status
		    }
		});

//		disconnectItem.setActionCommand("dissconnect");
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
			
				
				button.setActionCommand(String.valueOf(Connect4Model.getColumns()));
	            button.addActionListener(e -> {
	                int selectedColumn = Integer.parseInt(e.getActionCommand());
	                if (client != null) {
	                    client.sendMoveMessage(selectedColumn); // This assumes your Client class has a method sendMoveMessage
	                } else {
	                    System.out.println("Client instance is null.");
	                    //client.sendMoveMessage(selectedColumn);
	                }// Assuming you have a reference to a Client instance
	            });
	       //     gridPanel.add(button);
	            frame.add(boardPanel, BorderLayout.CENTER);
			}
		}
		frame.add(boardPanel, BorderLayout.CENTER);
	}

	private void initializeRightPanel() {
	    JPanel rightPanel = new JPanel();
	    rightPanel.setLayout(new BorderLayout());

	    // Initialize statusPanel first
	    statusPanel = new JPanel();
	    statusPanel.setLayout(new GridLayout(3, 1)); // Adjusted to accommodate 3 components

	    // Initialize and configure connectionStatusLabel
	    connectionStatusLabel = new JLabel("Not Connected");
	    connectionStatusLabel.setHorizontalAlignment(JLabel.CENTER);

	    // Add connectionStatusLabel to statusPanel
	    statusPanel.add(connectionStatusLabel);

	    currentPlayerLabel = new JLabel(bundle.getString("current_player"), SwingConstants.CENTER);
	    currentPlayerLabel.setBorder(BorderFactory.createTitledBorder(bundle.getString("current_player")));
	    statusPanel.add(currentPlayerLabel);

	    timerLabel = new JLabel(bundle.getString("timer"), SwingConstants.CENTER);
	    timerLabel.setBorder(BorderFactory.createTitledBorder(bundle.getString("timer")));
	    statusPanel.add(timerLabel);

	    // Now that statusPanel is properly initialized and populated, add it to rightPanel
	    rightPanel.add(statusPanel, BorderLayout.NORTH);

	    // Continue with the rest of the method...
	    initializeChatPanel(rightPanel);

	    // Finally, add the rightPanel to the frame
	    frame.add(rightPanel, BorderLayout.EAST);
	}



	private void initializeChatPanel(JPanel rightPanel) {
	    JPanel chatPanel = new JPanel(new BorderLayout());
	    chatArea = new JTextArea(10, 20);
	    chatArea.setEditable(false); // Prevent direct editing of the chat area
	    JScrollPane chatScrollPane = new JScrollPane(chatArea); // Allow scrolling
	    
	    chatInput = new JTextField();
	    chatInput.addActionListener(new ChatInputListener()); // Add listener for "Enter" key press
	    chatInput.addActionListener(e -> {
	        String chatMsg = chatInput.getText();
	        if (!chatMsg.isEmpty()) {
	            client.sendChatMessage(chatMsg); // Send chat message to the server
	            chatInput.setText(""); // Clear the input field
	        }
	    });
	    chatPanel.add(chatScrollPane, BorderLayout.CENTER);
	    chatPanel.add(chatInput, BorderLayout.SOUTH);
	    
	    rightPanel.add(chatPanel, BorderLayout.SOUTH); // Make sure this is laid out correctly in your UI
	}

	private class ChatInputListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        String message = chatInput.getText();
	        if (!message.isEmpty() && client != null) {
	            client.sendChatMessage(message); // Use the client instance to send the message
	            chatInput.setText(""); // Clear the input field after sending
	        }
	    }
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
	
	public void setActionListenerForMenuItems(ActionListener listener) {
		connectItem.addActionListener(listener);
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
		// button.setEnabled(false);
		button.revalidate();
		button.repaint();

	}
	
	public void showError(String message) {
	    JOptionPane.showMessageDialog(this.frame, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void enableChat() {
	    // Enable chat-related components here
	    chatInput.setEnabled(true);
	}

	public void enableGameControls() {
	    // Enable game-related components here
	    for (int i = 0; i < Connect4Model.getRows(); i++) {
	        for (int j = 0; j < Connect4Model.getColumns(); j++) {
	            buttons[i][j].setEnabled(true);
	        }
	    }
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
	
	public void updateConnectionStatus(boolean isConnected) {
	    if (isConnected) {
	        connectionStatusLabel.setText("Connected");
	        connectionStatusLabel.setForeground(new Color(34, 139, 34)); // Set to green or any color you prefer
	        // Enable game controls if necessary
	        enableGameControls(true);
	    } else {
	        connectionStatusLabel.setText("Disconnected");
	        connectionStatusLabel.setForeground(Color.RED);
	        // Disable game controls if necessary
	        enableGameControls(false);
	    }
	    frame.revalidate();
	    frame.repaint();
	}

	private void enableGameControls(boolean enable) {
	    for (JButton[] buttonRow : buttons) {
	        for (JButton button : buttonRow) {
	            button.setEnabled(enable);
	        }
	    }
	    chatInput.setEnabled(enable); // Enable/disable chat input based on connection status
	    // Add other components you wish to enable/disable
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
	 * Disables all buttons on the game board. This method is typically called when
	 * the game has ended to prevent further moves.
	 */
	public void disableAllButtons() {
		for (int i = 0; i < Connect4Model.getRows(); i++) {
			for (int j = 0; j < Connect4Model.getColumns(); j++) {
				buttons[i][j].setEnabled(false);
			}
		}
	}

	/**
	 * Appends a message to the chat area. This method is typically used to display
	 * chat messages from players.
	 * @param chatMessage 
	 * 
	 * @param message The message to be added to the chat area.
	 */
	public void appendChat(String chatMessage) {
		chatArea.append(chatMessage + "\n");
	}

	/**
	 * Clears the chat input field. This is typically called after a chat message
	 * has been sent.
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
	
	public void displayChatMessage(String chatMessage) {
	    chatArea.append(chatMessage + "\n");
	    chatArea.setCaretPosition(chatArea.getDocument().getLength()); // Auto-scroll to the latest message
	}

}
