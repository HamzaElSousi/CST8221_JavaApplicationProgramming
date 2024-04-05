import java.awt.EventQueue;
import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class Client {
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private String host;
	private int port;
	private volatile boolean running = true; // Added volatile for thread visibility
	private Connect4View view; // Reference to the game UI

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
	public void startConnection(String ip, int port) {
		try {
			System.out.println("Attempting to connect to the server at " + ip + ":" + port);
			clientSocket = new Socket(ip, port);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println("Connection established with the server.");
			listenForServerMessages(); // Start listening for messages immediately upon connection
		} catch (IOException e) {
			System.out.println("Client error: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Could not connect to the server. Please make sure the server is running and try again.",
					"Connection Error", JOptionPane.ERROR_MESSAGE);
//	         Consider setting a flag here to indicate that the client is not connected, and disable features that require a connection
			running = true;
		}
	}

	// Sends a message to the server and waits for the response
	public String sendMessage(String msg) {
		if (out != null) {
			try {
				System.out.println("Sending message to server: " + msg);
				out.println(msg);
				String response = in.readLine();
				return response;
			} catch (SocketException e) {
				System.out.println("Connection lost: " + e.getMessage());
				// Implement your logic for reconnecting or alerting the user here
				return null;
			} catch (IOException e) {
				System.out.println("Error in sendMessage: " + e.getMessage());
				e.printStackTrace();
				return null;
			}
		} else {
			System.out.println("Cannot send message, connection not established.");
		}
		return null;
	}

	// Closes the connection and the IO streams
	public void stopConnection() {
		try {
			running = false; // Ensure the listening thread stops
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (clientSocket != null)
				clientSocket.close();
			System.out.println("Connection to server has been closed.");
		} catch (IOException e) {
			System.out.println("Error closing client: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendChatMessage(String msg) {
		sendMessage(NetworkProtocol.CHAT_MESSAGE + msg);
	}

	public void sendGameMove(int column) {
		sendMessage(NetworkProtocol.GAME_MOVE + NetworkProtocol.MESSAGE_SEPARATOR + column);
	}

	public void cleanup() {
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
		} finally {
			// this.server.removeClient(this);
		}
	}

	// Modify the listenForServerMessages method or similar to handle MOVE messages
	private void listenForServerMessages() {
		new Thread(() -> {
			try {
				while (running && !clientSocket.isClosed()) {
					if (in.ready()) {
						String msg = in.readLine();
						if (msg.startsWith("CHAT:")) {
							// Assuming displayChatMessage is a method in your Connect4View
							String chatMsg = msg.substring(5);
							EventQueue.invokeLater(() -> view.displayChatMessage(chatMsg));
						} else if (msg.startsWith("MOVE:")) {
							int column = Integer.parseInt(msg.split(":")[1]);
							// Here, you need to update your game model and view based on the received move
							// This requires you to have a method in your view/controller to handle this
						}
					}
				}
			} catch (IOException e) {
				System.out.println("Error reading from server: " + e.getMessage());
			}
		}).start();
	}

	public void setView(Connect4View view) {
		this.view = view;
	}

	// Displays chat messages in the console (for now)
	private void displayChatMessage(String message) {
		System.out.println(message);
	}

	// Example usage
	public static void main(String[] args) {
		// Connect4View view = new Connect4View();

		// Now, when the application runs, and the user enters a message in the
		// chatInput field,
		// pressing Enter will trigger the ChatInputListener, which will send the
		// message through the Client.
	}
}