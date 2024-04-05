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
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Server {
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();
    public ServerGui serverGui;

    public Server(int port) {
        EventQueue.invokeLater(() -> serverGui = new ServerGui());
    }
    
 public Server() {
	 EventQueue.invokeLater(() -> serverGui = new ServerGui());
 }

	// Inside Server class
 public synchronized void removeClient(ClientHandler clientHandler) {
     clients.remove(clientHandler);
     System.out.println("Client " + clientHandler.getClientSocket().getRemoteSocketAddress() + " removed. ID:0016");
     // You can also add more cleanup code here if needed
 }

 public boolean isPortAvailable(int port) {
	    try (ServerSocket serverSocket = new ServerSocket(port)) {
	        return true;
	    } catch (IOException e) {
	        return false; // Either the port is in use or it's an invalid port number
	    }
	}
 
 public void startServer(int port) {
	    if (!isPortAvailable(port)) {
	        System.out.println("Port " + port + " is unavailable. Please use a different port.");
	        return;
	    }
	    // Server starting logic...
	}

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port + " ID:0001");

            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
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
        
    }

    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            System.out.println("Server stopped"+ " ID:0004");
        } catch (IOException e) {
            System.out.println("Error closing server: " + e.getMessage()+ " ID:0005");
            e.printStackTrace();
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

        public ClientHandler(Socket socket, Server server) {
            this.clientSocket = socket;
            this.server = server;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println("New client connected"+ " ID:0006");
            } catch (IOException e) {
                System.out.println("Error creating streams: " + e.getMessage()+ " ID:0007");
             // Inside the finally block of the ClientHandler run method
            }  finally {
//                    server.removeClient(this);
                    try {
                        in.close();
                        out.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }}
                }

        public Socket getClientSocket() {
            return clientSocket;
        }

		@Override
        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Message from client " + clientSocket.getRemoteSocketAddress() + ": " + inputLine);
                    String[] parts = inputLine.split(NetworkProtocol.MESSAGE_SEPARATOR);
                    String type = parts[0];

                    switch (type) {
                        case NetworkProtocol.PLAYER_CONNECT:
                            // Established connection
                            System.out.println("Connected from Server ID:0008");
                            break;

                        case NetworkProtocol.CHAT_MESSAGE:
                            // Broadcast chat message
                            String chatMsg = parts[1]; // Assuming the message follows "CHAT_MESSAGE#ActualMessage" format
                            server.broadcastMessage("CHAT:" + chatMsg);
                            System.out.println("Chat from case in Server ID:0009");
                            break;

                        case NetworkProtocol.GAME_MOVE:
                            // Handle game move, validate, and broadcast new game state
                            System.out.println("Game move from client ID:0010");
                            break;

                        case NetworkProtocol.PLAYER_DISCONNECT:
                            // Handle client disconnect
                            System.out.println("Disconnected from Server ID:0011");
                            server.removeClient(this);
                            return; // Exit the while loop and proceed to cleanup
                            
                        // Handle other cases
                    }
                }
            } catch (IOException e) {
                System.out.println("ClientHandler IOException for client " + clientSocket.getRemoteSocketAddress() + ": " + e.getMessage() + " ID:0012");
            } finally {
                // Cleanup logic here
                server.removeClient(this);
                closeClientSocket();
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing client socket: " + e.getMessage() + " ID:0014");
                }
                System.out.println("Client " + clientSocket.getRemoteSocketAddress() + " cleanup complete ID:0015");
            }
        }
		private void closeClientSocket() {
		    try {
		        if (in != null) in.close();
		        if (out != null) out.close();
		        if (clientSocket != null && !clientSocket.isClosed()) {
		            clientSocket.close();
		        }
		    } catch (IOException e) {
		        System.out.println("Error closing client socket: " + e.getMessage() + " ID:0015");
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
            launch();
		}
		private void addListener() {
            startButton.addActionListener(e -> {
                int port = Integer.parseInt(getPort());
                new Thread(() -> start(port)).start();
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
