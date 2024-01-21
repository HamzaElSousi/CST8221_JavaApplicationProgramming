import java.util.Scanner;

public class Connect4ConsoleUI {
    public static void main(String[] args) {
        Connect4Game game = new Connect4Game();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            game.displayBoard();
            System.out.println("Player " + game.getCurrentPlayer() + "'s turn. Choose a column (0-6): ");
            int column = scanner.nextInt();

            if (game.makeMove(column)) {
                if (game.checkWinner()) {
                    game.displayBoard();
                    System.out.println("Player " + game.getCurrentPlayer() + " wins!");
                    break;
                } else if (game.isDraw()) {
                    game.displayBoard();
                    System.out.println("The game is a draw!");
                    break;
                }
                game.switchPlayer();
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }

        scanner.close();
    }
}
