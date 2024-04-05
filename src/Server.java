import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
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
    
 // Inside Server class
    public synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Client disconnected and removed.");
    }


    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                } catch (IOException e) {
                    System.out.println("Exception in client handling: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            System.out.println("Server stopped");
        } catch (IOException e) {
            System.out.println("Error closing server: " + e.getMessage());
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
                System.out.println("New client connected");
            } catch (IOException e) {
                System.out.println("Error creating streams: " + e.getMessage());
             // Inside the finally block of the ClientHandler run method
            }  finally {
                    server.removeClient(this);
                    try {
                        in.close();
                        out.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }}
                }

        @Override
        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] parts = inputLine.split(NetworkProtocol.MESSAGE_SEPARATOR);
                    String type = parts[0];
                    switch (type) {
                        case NetworkProtocol.CHAT_MESSAGE:
                            // Broadcast chat message
                            server.broadcastMessage(inputLine);
                            break;
                        case NetworkProtocol.GAME_MOVE:
                            // Handle game move, validate, and broadcast new game state
                            break;
                        // Handle other cases
                    }
                }
            } catch (IOException e) {
                System.out.println("Error in client communication: " + e.getMessage());
            } finally {
                // Cleanup
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
