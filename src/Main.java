/*
 * Student name && ID : Hamza El Sousi , 040982818
 * Student name && ID : Mansi Joshi , 041091664
 * Lab Prof: Paulo Sousa && Daniel Cormier
 * Assignment: A22
 * MVC Desgin: MAIN
 */
import java.awt.EventQueue;

import javax.swing.SwingUtilities;

/**
 * The {@code Main} class serves as the entry point for the Connect4 game application.
 * It initializes the MVC (Model-View-Controller) components of the application and
 * sets up the necessary connections between them. This class uses SwingUtilities
 * to ensure that the GUI is created and updated on the Event Dispatch Thread (EDT),
 * following the best practices for Swing applications.
 */
public class Main {
	/**
     * The main method serves as the program's entry point. It uses {@link SwingUtilities#invokeLater}
     * to ensure that the application's GUI is initialized and manipulated on the EDT.
     * This method initializes the MVC components: {@link Connect4Model}, {@link Connect4View},
     * and {@link Connect4Controller}, and establishes the necessary connections between them.
     * 
     * @param args not used in this application
     */
	public static void main(String[] args) {
	    EventQueue.invokeLater(() -> {
	        Server server = new Server(6666); // Choose an appropriate port
	        new Thread(() -> server.start(6666)).start(); // Start the server on a separate thread

	        Client client = new Client(null, 0, null);
	        Connect4Model model = new Connect4Model(client);
	        Connect4View view = new Connect4View();
	        new Connect4Controller(model, view, null); // Initially, no client is passed
	        
	     // Call the method to print active threads
	        printActiveThreads();
	    });
	    
	    
	}
	private static void printActiveThreads() {
        int activeThreadCount = Thread.activeCount();
        System.out.println("Number of active threads: " + activeThreadCount);

        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while (threadGroup.getParent() != null) {
            threadGroup = threadGroup.getParent();
        }

        Thread[] threads = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);

        for (Thread thread : threads) {
            if (thread != null) {
                System.out.println("Thread Name: " + thread.getName() + ", State: " + thread.getState());
            }
        }
    }
}
