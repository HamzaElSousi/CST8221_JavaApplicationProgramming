import java.awt.EventQueue;
import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Client {
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private String host = "127.0.0.1"; // initially hard coding then will make dynamic
	private int port = 6666; // initially hard coding then will make dynamic
	private volatile boolean running = true; // Added volatile for thread visibility
	private Connect4View view; // Reference to the game UI

	// Constants for reconnection attempts
	private static final int MAX_RECONNECT_ATTEMPTS = 2;
	private static final long RECONNECT_DELAY_MS = 5000; // 5 seconds

	public Client(String host, int port, Connect4View view) {
		this.host = host;
		this.port = port;
		this.view = view;
	}

	// Modify the constructor or add a setter method to set this reference
	public Client(String serverAddress, int serverPort) {
		try {
			Socket socket = new Socket(serverAddress, serverPort);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Initializes the connection with the server using class-level host and port
	public void startConnection() {
	    if (clientSocket != null && !clientSocket.isClosed()) {
	        System.out.println("Already connected to the server.");
	     // In your Client class, after attempting to connect or disconnect
	        view.updateConnectionStatus(true); // or false, depending on the connection outcome

	        return;
	    }
	    if (host == null || host.isEmpty() || port <= 0 || port > 65535) {
	        System.out.println("Invalid IP address or port.");
	        return;
	    }
	    try {
	        System.out.println("Attempting to connect to the server at " + host + ":" + port);
	        clientSocket = new Socket(host, port);
	        out = new PrintWriter(clientSocket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        System.out.println("Connection established with the server.");
	     // In your Client class, after attempting to connect or disconnect
	        view.updateConnectionStatus(true); // or false, depending on the connection outcome
	        running = true; // Set running flag to true
	        listenForServerMessages(); // Start listening for messages
	    } catch (IOException e) {
	        System.out.println("Client error: " + e.getMessage());
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null,
	                "Could not connect to the server. Please make sure the server is running and try again.",
	                "Connection Error", JOptionPane.ERROR_MESSAGE);
	        running = false;
	    }
	}



	private boolean attemptReconnect() {
		System.out.println("Attempting to reconnect to the server: " + host + ":" + port);
		int attempts = 0;
		while (attempts < MAX_RECONNECT_ATTEMPTS && running) {
			try {
				Thread.sleep(5000); // Wait for 5 seconds before attempting reconnection
				closeResources(); // Close existing resources if open
				clientSocket = new Socket(host, port);
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				System.out.println("Reconnection successful.");
				listenForServerMessages(); // Re-establish message listening
				return true;
			} catch (IOException | InterruptedException e) {
				System.out.println("Reconnect attempt " + (attempts + 1) + " failed: " + e.getMessage());
				attempts++;
			}
		}
		System.out.println("All reconnection attempts failed.");
		return false;
	}

	// New method to close resources safely
	private void closeResources() {
		try {
			if (out != null) {
				out.close();
				out = null;
			}
			if (in != null) {
				in.close();
				in = null;
			}
//	        if (clientSocket != null && !clientSocket.isClosed()) {
//	            clientSocket.close();
//	            clientSocket = null;
//	        }
		} catch (IOException e) {
			System.out.println("Error closing resources: " + e.getMessage());
		}
	}

	// Sends a message to the server and waits for the response
	
	// Method to send a move message to the server
	public void sendMoveMessage(int column) {
	    if (out != null) {
	        sendMessage(NetworkProtocol.constructMessage(NetworkProtocol.GAME_MOVE, Integer.toString(column)));
	    } else {
	        System.out.println("Connection to server is not established.ID: 0001");
	        // You can handle this situation accordingly, such as showing an error message to the user.
	    }
	}

	// Method to send a win message to the server
	public void sendWinMessage() {
	    if (out != null) {
	        sendMessage(NetworkProtocol.GAME_WIN);
	    } else {
	        System.out.println("Connection to server is not established.ID: 0002");
	        // You can handle this situation accordingly, such as showing an error message to the user.
	    }
	}

    // Method to send a draw message to the server
	public void sendDrawMessage() {
	    if (out != null) {
	        sendMessage(NetworkProtocol.GAME_DRAW);
	    } else {
	        System.out.println("Connection to server is not established.ID: 0003");
	        // You can handle this situation accordingly, such as showing an error message to the user.
	    }
	}

    // Method to send a player switch message to the server
    public void sendPlayerSwitchMessage(char currentPlayer) {
        if (out != null) {
            sendMessage(NetworkProtocol.constructMessage(NetworkProtocol.PLAYER_SWITCH, String.valueOf(currentPlayer)));
        } else {
            System.out.println("Connection to server is not established.ID: 0004");
            // You can handle this situation accordingly, such as showing an error message to the user.
        }}

//    // Method to send a generic message to the server
    private void sendMessage(String message) {
        if (out != null) {
            out.write(message+ "\n");
            out.flush();
        } else {
            System.out.println("Error: PrintWriter 'out' is not initialized.");
        }
    }


//	public String sendMessage(String msg) {
//        if (clientSocket == null || clientSocket.isClosed() || out == null) {
//            System.out.println("Not connected to server.");
//            return null;
//        }
//        try {
//            out.println(msg);
//            String response = in.readLine();
//            if (response == null) {
//                throw new SocketException("Connection closed by server.");
//            }
//            return response;
//        } catch (SocketException e) {
//            System.out.println("Connection lost due to a SocketException: " + e.getMessage());
//            if (attemptReconnect()) {
//                return attemptReconnect() ? sendMessage(msg) : null; // Try to reconnect and resend the message
//            }
//        } catch (IOException e) {
//            System.out.println("IO Error: " + e.getMessage());
//            // Depending on your needs, you might want to attempt to reconnect here as well
//        }
//        return null;
//    }

	// Closes the connection and the IO streams
	public void stopConnection() {
		running = false; // This should be the first action
		cleanup(); // Ensure resources are closed
	}

	public void sendChatMessage(String msg) {
		sendMessage(NetworkProtocol.CHAT_MESSAGE + NetworkProtocol.MESSAGE_SEPARATOR + msg);
	}

	public void sendGameMove(int column, int row) {
		sendMessage(NetworkProtocol.GAME_MOVE + NetworkProtocol.MESSAGE_SEPARATOR + Connect4Model.getColumns()
				+ Connect4Model.getRows());
	}

	private void cleanup() {
		try {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
			if (clientSocket != null && !clientSocket.isClosed()) {
				clientSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Modify the listenForServerMessages method or similar to handle MOVE messages
	private void listenForServerMessages() {
	    new Thread(() -> {
	        try {
	            String message;
	            while ((message = in.readLine()) != null && running) {
	                System.out.println("Server says: " + message);
	                // Process the message based on the protocol
	                // For example, update UI with chat messages or game moves
	                if (message.startsWith(NetworkProtocol.CHAT_MESSAGE)) {
	                    String[] parts = message.split(NetworkProtocol.MESSAGE_SEPARATOR);
	                    if (parts.length >= 2) {
	                        String chatMsg = parts[1]; // Extract chat message
	                        view.displayChatMessage(chatMsg); // Update UI with chat message
	                    }
	                } else {
	                    // Handle other types of messages
	                }
	            }
	        } catch (SocketException e) {
	            System.out.println("SocketException, possible server disconnect: " + e.getMessage());
	        } catch (IOException e) {
	            System.out.println("IOException in client listening thread: " + e.getMessage());
	        } finally {
	            if (running) {
	                // Attempt reconnect or notify user of disconnect
	                System.out.println("Attempting to reconnect...");
	                attemptReconnect(); 
	                // or notifyUserOfDisconnect();
	            }
	        }
	    }).start();
	}


	public void setView(Connect4View view) {
		this.view = view;
	}

	// Displays chat messages in the console (for now)
	public void displayChatMessage(String message) {
	    SwingUtilities.invokeLater(() -> {
	        view.appendChat(message);
	    });
	}


//	public void updateBoardFromServer(String serializedGameState) {
//	    // Parse the serialized game state and update the local game model
//	    Connect4Model.updateGameState(serializedGameState);
//	    
//	    Connect4Model connect4Model = new Connect4Model(null);
//		// Refresh the game view
//	    connect4Model.refreshGameBoard(Connect4Model.getBoard());
//	}

	private void startKeepAlive() {
		new Thread(() -> {
			while (running) {
				try {
					Thread.sleep(30000); // Keep-alive interval: 30 seconds
					sendMessage("KEEP_ALIVE");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.out.println("Keep-alive thread interrupted.");
					return;
				} catch (Exception e) {
					System.out.println("Error sending keep-alive message: " + e.getMessage());
					// Optionally, attempt reconnection or handle the error further here
				}
			}
		}).start();
	}

	// Example usage

//		public static void main(String[] args) {
//		    EventQueue.invokeLater(() -> {
////		         Your GUI initialization code, set view here if needed
//		 Connect4View view = new Connect4View();
//		 Connect4Controller controller =new Connect4Controller(null, view, null);
//		    });
////	 Now, when the application runs, and the user enters a message in the
////	 chatInput field,
////	 pressing Enter will trigger the ChatInputListener, which will send the
////	 message through the Client.
//	}

		public static void main(String[] args) {
		    EventQueue.invokeLater(() -> {
		        String host = "localhost"; // Change this to your server's host address
		        int port = 6666; // Change this to your server's port
		        Connect4View view = new Connect4View(); // Assuming you have such a constructor

		        Client client = new Client(host, port, view);
		        client.startConnection(); // Start the connection

		        // Assuming Connect4View has methods to update UI based on connection status
		        if (client.clientSocket != null && client.clientSocket.isConnected()) {
		            // Update the UI to reflect that the connection has been established
		            view.updateConnectionStatus(true);

		            // Enable chat or game controls in the UI
		            view.enableChat();
		            view.enableGameControls();

		            // Optionally, send a message to the server indicating that the client has successfully connected
		            // This could be a simple "HELLO" message or something more complex if your protocol requires
		            client.sendChatMessage("HELLO");
		        } else {
		            // Update the UI to show that the connection could not be established
		            view.updateConnectionStatus(false);
		        }

		        // Setup a shutdown hook to ensure resources are cleaned up properly on exit
		        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		            if (client.clientSocket != null && client.clientSocket.isConnected()) {
		                client.sendChatMessage("GOODBYE");
		                client.stopConnection(); // Ensure the connection is closed gracefully
		            }
		        }));
		    });
		}

}
