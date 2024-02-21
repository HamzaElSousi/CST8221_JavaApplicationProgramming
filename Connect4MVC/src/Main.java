/*
 * Student name && ID : Hamza El Sousi , 040982818
 * Student name && ID : Mansi Joshi , 041091664
 * Lab Prof: Paulo Sousa
 * Assignment: A12
 * Lab Prof: Paulo Sousa
 * MVC Desgin: MAIN
 */
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
        SwingUtilities.invokeLater(() -> {
            Connect4Model model = new Connect4Model();
            Connect4View view = new Connect4View();
            @SuppressWarnings("unused") //used
			Connect4Controller controller = new Connect4Controller(model, view);
        });
    }
}
