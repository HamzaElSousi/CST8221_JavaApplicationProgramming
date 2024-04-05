import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class Connect4Controller {
    private Connect4Model model;
    private Connect4View view;
    private ControllableTimer timer;
    private Client client; // Added client reference
    private Server server; // Server reference for managing server actions, if needed within the controller

    public Connect4Controller(Connect4Model model, Connect4View view, Client client) {
        this.model = model;
        this.view = view;
        this.client = client;
        initializeClient(); // Initialize the client for communication
        initializeActionListeners();
        initializeTimer();
    }
    
    private void initializeTimer() {
        timer = new ControllableTimer(view);
        timer.start();
    }

    private void initializeClient() {
        // Placeholder for client initialization logic
        // Consider dynamically setting host and port, possibly from user input or configuration
        String host = "127.0.0.1";
        int port = 6666; // Example port, adjust as necessary
        this.client = new Client(host, port);
        this.view.setClient(client); // Ensure the view has a reference to the client for sending messages
        client.startConnection(host, port); // Start the client connection
    }

    private void initializeActionListeners() {
        // Initialize button listeners for the game grid
        for (int i = 0; i < Connect4Model.getRows(); i++) {
            for (int j = 0; j < Connect4Model.getColumns(); j++) {
                view.setActionListenerForButton(i, j, new ButtonListener(i, j));
            }
        }

        // Initialize chat input listener
        view.setActionListenerForChatInput(new ChatListener());

        // Menu item listeners
        view.setActionListenerForRestartItem(e -> restartGame());
        view.setActionListenerForExitItem(e -> System.exit(0));
        view.setActionListenerForMenuItems(new MenuListener());
    }

    public void connectToServer(String host, int port) {
        // Connection logic here
      //  client = new Client(host, port); // Assuming Client constructor accepts a reference to Connect4View
        client.startConnection(host, port);
        view.setClient(client); // This line makes sure the view has a reference to the client for sending chat messages
    }

    public void disconnectFromServer() {
        if (client != null) {
            client.stopConnection();
            client = null;
        }
    }

    private class ChatListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = view.getChatInput().trim();
            if (!message.isEmpty() && client != null) {
                client.sendChatMessage(message);
                view.clearChatInput();
            }
        }
    }

    private class MenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "connect":
                    String host = getHostFromUser();
                    if (host != null) {
                        int port = getUserDefinedPort();
                        if (port > 0) {
                            connectToServer(host, port);
                        }
                    }
                    break;
                case "disconnect":
                    disconnectFromServer();
                    break;
            }
        }
    }

    private String getHostFromUser() {
        String host = JOptionPane.showInputDialog(view.getFrame(), 
                                                  "Enter Server Host:", 
                                                  "Server Connection", 
                                                  JOptionPane.QUESTION_MESSAGE);
        if (host == null || host.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view.getFrame(), 
                                          "Invalid host address.", 
                                          "Error", 
                                          JOptionPane.ERROR_MESSAGE);
            return null; // Return null indicating invalid input
        }
        return host.trim();
    }

    private int getUserDefinedPort() {
        String portString = JOptionPane.showInputDialog(view.getFrame(), 
                                                         "Enter Server Port:", 
                                                         "Server Connection", 
                                                         JOptionPane.QUESTION_MESSAGE);
        try {
            return Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view.getFrame(), 
                                          "Invalid port number.", 
                                          "Error", 
                                          JOptionPane.ERROR_MESSAGE);
            return -1; // Return an invalid port number
        }
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
                    String winner = "Player " + model.getCurrentPlayer() + " wins!";
                    JOptionPane.showMessageDialog(view.getFrame(), winner, "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                    view.disableAllButtons();
                    // Stop the timer
                    timer.setStatus(ControllableTimer.STOP);
                } else if (model.isDraw()) {
                    JOptionPane.showMessageDialog(view.getFrame(), "The game is a draw!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
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
	 * Restarts the game by reinitializing the game board in both the model and
	 * view, and resetting the timer. It also updates the game status to indicate
	 * which player's turn is next.
	 */
	private void restartGame() {
		model.initializeBoard();
		view.resetBoard();
		view.updateStatus("Player R's turn");
		// Reset the timer
		timer.setStatus(ControllableTimer.RESET);
	}
	}

