import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import javax.naming.NameParser;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server {
	private ServerSocket serverSocket;
	private final List<ClientHandler> clients = new ArrayList<>();
	public ServerGui serverGui;
	private int port;
	private Connect4Model model = new Connect4Model(null);

	public Server(int port) {
        this.port = port;
        initializeGui();
    }

    public Server() {
        initializeGui();
    }

    private void initializeGui() {
        EventQueue.invokeLater(() -> {
            serverGui = new ServerGui();
            serverGui.launch();
        });
    }

	// Inside Server class
	public synchronized void removeClient(ClientHandler clientHandler) {
		clients.remove(clientHandler);
		System.out.println("Client " + clientHandler.getClientSocket().getRemoteSocketAddress() + " removed. ID:0017");
		// You can also add more cleanup code here if needed
	}

	public boolean isPortAvailable(int port) {
		try (ServerSocket serverSocket = new ServerSocket(this.port)) {
			return true;
		} catch (IOException e) {
			return false; // Either the port is in use or it's an invalid port number
		}
	}


	public void start(int port) {
		new Thread(() -> {
		try {
			serverSocket = new ServerSocket(this.port);
			System.out.println("Server started on port " + this.port + " ID:0001");

			while (!serverSocket.isClosed()) {
				try {
					Socket clientSocket = serverSocket.accept();
					System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
					ClientHandler clientHandler = new ClientHandler(clientSocket, this, model); // Pass model her;
					clients.add(clientHandler);
					new Thread(clientHandler).start();
				} catch (IOException e) {
					System.out.println("Server exception: " + e.getMessage() + " ID:0002");
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.println("Server exception: " + e.getMessage() + " ID:0003");
			if (serverSocket.isClosed()) {
				System.out.println("Server Socket is closed, exiting." + "ID:0014");
			} else {
				System.out.println("Exception in client handling: " + e.getMessage());
				// Log or handle the exception
			}
		}
		}).start();

	}

	public void stop() {
        try {
            for (ClientHandler client : clients) {
                client.closeClientSocket(); // Ensure all client sockets are closed
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server: " + e.getMessage() + " ID:0005");
        }
    }

	public synchronized void broadcastMessage(String message) {
		for (ClientHandler client : clients) {
			client.sendMessage(message);
		}
	}

	private class ClientHandler implements Runnable {
		private Socket clientSocket;
		private PrintWriter out;
		private BufferedReader in;
		private Server server;
		private Connect4Model model;

//        public ClientHandler(Socket socket, Server server) {
//            this.clientSocket = socket;
//            this.server = server;
//            try {
//                out = new PrintWriter(clientSocket.getOutputStream(), true);
//                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                System.out.println("New client connected"+ " ID:0006");
//            } catch (IOException e) {
//                System.out.println("Error creating streams: " + e.getMessage()+ " ID:0007");
//             // Inside the finally block of the ClientHandler run method
//            }  finally {
//                    server.removeClient(this);
//                    try {
//                        in.close();
//                        out.close();
//                        clientSocket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }}
//                }

		public ClientHandler(Socket socket, Server server, Connect4Model model) {
			this.clientSocket = socket;
			this.server = server;
			this.model = model;
			try {
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				System.out.println("New client connected" + " ID:0006");
			} catch (IOException e) {
				System.out.println("Error creating streams: " + e.getMessage() + " ID:0007");
				try {
					if (clientSocket != null && !clientSocket.isClosed())
						clientSocket.close();
				} catch (IOException log) {
					log.printStackTrace();
				}
				server.removeClient(this);
				return; // Exit the constructor if an exception occurs
			}

			// Move the resource cleanup logic outside the try-catch block
			// This ensures that resources are only closed if they have been successfully
			// initialized
			// and avoids NullPointerExceptions
		}
		public synchronized void processMessage(String message, ClientHandler sender) {
		    // Synchronized to prevent concurrent modifications of any shared resources

		    if (message.startsWith(NetworkProtocol.CHAT_MESSAGE)) {
		        // Extract the actual chat message
		        String chatMsg = message.substring(NetworkProtocol.CHAT_MESSAGE.length() + 1);
		        broadcastMessage(NetworkProtocol.CHAT_MESSAGE + " " + ": " + chatMsg);
		    } else if (message.startsWith(NetworkProtocol.GAME_MOVE)) {
		        // Extract move details
		        String moveDetails = message.substring(NetworkProtocol.GAME_MOVE.length() + 1);
		        // Assuming moveDetails contains the column where the move was made
		        // Update game state here based on the move
		        
		        // Optionally, broadcast the updated game state to all clients
		        broadcastGameState();
		    }
		    // Handle other message types as needed
		}
		
		public synchronized void broadcastMessage(String message) {
		    // Iterate over a thread-safe collection of client handlers
		    for (ClientHandler client : clients) {
		        client.sendMessage(message);
		    }
		}
		
	
		public synchronized void broadcastGameState() {
		    String gameState = model.getSerializedGameState();
		    server.broadcastMessage("GAME_STATE:" + gameState); // Use server's method to broadcast
		}





		@Override
		public void run() {
			try {
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					System.out
							.println("Message from client " + clientSocket.getRemoteSocketAddress() + ": " + inputLine);
					String[] parts = inputLine.split(NetworkProtocol.MESSAGE_SEPARATOR);
					if (parts.length < 2) {
						System.out.println("Invalid message format received: " + inputLine);
						continue;
					}
					String type = parts[0];
					String content = parts[1];
					switch (type) {
					case NetworkProtocol.PLAYER_CONNECT:
						// Established connection
						handlePlayerConnect(content);
						break;

					case NetworkProtocol.CHAT_MESSAGE:
						// Broadcast chat message
						handleChatMessage(content);
//						Client.displayChatMessage(content);
//                            String chatMsg = parts[1]; // Assuming the message follows "CHAT_MESSAGE#ActualMessage" format
						break;

					case NetworkProtocol.GAME_MOVE:
						// Handle game move, validate, and broadcast new game state
						handleGameMove(content);
						break;

					case NetworkProtocol.PLAYER_DISCONNECT:
						// Handle client disconnect
						handlePlayerDisconnect();
						return; // Exit the while loop and proceed to cleanup

					// Handle other cases
					}
				}
			} catch (IOException e) {
				System.out.println("ClientHandler IOException for client " + clientSocket.getRemoteSocketAddress()
						+ ": " + e.getMessage() + " ID:0012");
			} finally {
				// Cleanup logic here

				cleanup();
				System.out.println("Error closing client socket: " + " ID:0014");
				System.out.println("Client " + clientSocket.getRemoteSocketAddress() + " cleanup complete ID:0015");
			}
		}

		private void cleanup() {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (clientSocket != null && !clientSocket.isClosed())
					clientSocket.close();
			} catch (IOException e) {
				System.out.println("Error cleaning up resources: " + e.getMessage());
			} finally {
				server.removeClient(this);
				System.out.println(
						"Cleanup completed for client " + getClientSocket().getRemoteSocketAddress() + " ID:0016");
			}
		}

		private String clientName; // Declare a clientName variable in ClientHandler

		private void handlePlayerConnect(String content) {
		    this.clientName = content; // Assuming content is the username
		    System.out.println(clientName + " connected. ID:0008");
		}
		private String getClientName() {
		    return this.clientName;
		}

		private void handleChatMessage(String content) {
			server.broadcastMessage("CHAT:" + content);
			System.out.println("Chat from server ID:0009");
			// Additional logic for handling chat messages can be added here
		}

		private void handleGameMove(String content) {
		    // Parse the move from the content
		    int column = Integer.parseInt(content); // Assuming content is the column index for simplicity
		    model.makeMove(column); // Example method in Connect4Model to make a move
		    broadcastGameState(); // Then broadcast the new game state
		}


		private void handlePlayerDisconnect() {
			System.out.println("Disconnected from server ID:0011");
			server.removeClient(this);
			// Additional logic for handling player disconnect can be added here
		}

		public Socket getClientSocket() {
			return clientSocket;
		}

		private void closeClientSocket() {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				if (clientSocket != null && !clientSocket.isClosed()) {
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println("Error closing client socket: " + e.getMessage() + " ID:0018");
			}
		}

		public void sendMessage(String message) {
			out.println(message);
		}
	}

	public class ServerGui extends JFrame {
		private JPanel mainPane;
		private JTextField nameField;
		private JTextField portNumInput;
		private JTextField statusField;
		private JButton startButton;
		private JButton stopButton;

		public ServerGui() {
			super("Server");
			mainPane = new JPanel();
			setGui();
			addListener();
//			launch();
		}

		private void updateServerStatus(boolean isRunning) {
            SwingUtilities.invokeLater(() -> {
                startButton.setEnabled(!isRunning);
                stopButton.setEnabled(isRunning);
                // You might also want to update a status label here to inform the user about the server status
            });
        }

		private void addListener() {
			startButton.addActionListener(e -> {
				try {
					int port = Integer.parseInt(getPort());
					if (isPortAvailable(port)) {
						new Thread(() -> start(port)).start();
					} else {
						JOptionPane.showMessageDialog(this, "Port is unavailable. Please choose a different port.",
								"Port Unavailable", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this, "Invalid port number. Please enter a valid port.",
							"Invalid Input", JOptionPane.ERROR_MESSAGE);
				}
			});
			stopButton.addActionListener(e -> stop());
		}

		public void addListener(ActionListener al) {
			startButton.addActionListener(al);
			stopButton.addActionListener(al);
		}

		public String getName() {
			return nameField.getText().trim();
		}

		public String getPort() {
			return portNumInput.getText().trim();
		}

		private void setGui() {
			mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

			JPanel firstRow = new JPanel();
			nameField = new JTextField(12);
			JLabel nameLabel = new JLabel("Name: ");
			firstRow.add(nameLabel);
			firstRow.add(nameField);
			mainPane.add(firstRow);

			JPanel secondRow = new JPanel();
			portNumInput = new JTextField(12);
			JLabel portLabel = new JLabel("Port: ");
			secondRow.add(portLabel);
			secondRow.add(portNumInput);
			mainPane.add(secondRow);

			JPanel thirdRow = new JPanel();
			statusField = new JTextField(12);
			statusField.setEditable(false);
			mainPane.add(thirdRow);

			JPanel buttonsRow = new JPanel();
			startButton = new JButton("Start");
			startButton.setActionCommand("start");
			stopButton = new JButton("Stop: ");
			stopButton.setActionCommand("stop");
			buttonsRow.add(startButton);
			buttonsRow.add(stopButton);
			mainPane.add(buttonsRow);

			getContentPane().add(mainPane);
		}

		public void launch() {
			super.setLocationRelativeTo(null);
			super.pack();
			super.setResizable(false);
			super.setVisible(true);
		}

	}
}
