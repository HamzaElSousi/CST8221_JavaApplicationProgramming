/**
 * The {@code Connect4Model} class represents the model component in the MVC (Model-View-Controller)
 * design pattern for the Connect4 game. It encapsulates the game's state, including the game board,
 * the current player, and the logic for making moves, checking for a winner, and determining a draw.
 *
 * <p>This class maintains a 2D array to represent the game board where each cell can be empty, occupied
 * by player 'R' (Red), or occupied by player 'Y' (Yellow). The class provides methods to initialize the
 * game board, make moves, switch players, and check for game-ending conditions.</p>
 *
 * <p><b>Student names and IDs:</b>
 * <ul>
 * <li>Hamza El Sousi, 040982818</li>
 * <li>Mansi Joshi, 041091664</li>
 * </ul>
 *
 * <p><b>Lab Professors:</b> Paulo Sousa && Daniel Cormier</p>
 * <p><b>Assignment:</b> A22</p>
 * <p><b>MVC Design:</b> MODEL</p>
 */
public class Connect4Model {
	/**
	 * Constant to represent the amount of rows in game
	 */
    private static final int ROWS = 6;
    /**
	 * Constant to represent the amount of columns in game
	 */
    private static final int COLUMNS = 7;
    /**
	 * two dimension array to represent grid/board in game
	 */
    private char[][] board;
    /**
	 * currentPlayer represents the players turn at a given point in time during game 
	 */
    private char currentPlayer;

    // Add fields for network communication
    private Client client;
    /**
     * Constructs a new Connect4Model and initializes the game board and the starting player.
     */
//    public Connect4Model() {
//        board = new char[getRows()][getColumns()];
//        currentPlayer = 'R'; // Starting player, R for Red, Y for Yellow
//        initializeBoard();
//    }
    

    // Add constructor to initialize the client
    public Connect4Model(Client client) {
        this.client = client;
        board = new char[getRows()][getColumns()];
        currentPlayer = 'R'; // Starting player, R for Red, Y for Yellow
        initializeBoard();
    }

    public synchronized String getSerializedGameState() {
        StringBuilder builder = new StringBuilder();
        for (char[] row : board) {
            for (int cell : row) {
                builder.append(cell);
            }
            builder.append(";"); // Use semicolon as a row delimiter
        }
        return builder.toString();
    }

    /**
     * Initializes the game board to an empty state, with each cell set to '.'.
     */
    void initializeBoard() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                board[i][j] = '.';
            }
        }
    }

    /**
     * Attempts to make a move for the current player at the specified column. If the move is successful,
     * the game board is updated and the method returns {@code true}. If the move cannot be made (e.g.,
     * the column is full or invalid), the method returns {@code false}.
     *
     * @param column the column where the move is to be made
     * @return {@code true} if the move was successful, {@code false} otherwise
     */
    public boolean makeMove(int column) {
        if (column < 0 || column >= getColumns()) {
            return false; // Invalid column
        }

        for (int i = getRows() - 1; i >= 0; i--) {
            if (board[i][column] == '.') {
                board[i][column] = currentPlayer;
                // Send move to the server
                client.sendMoveMessage(column);
                return true;
            }
        }
        client.sendMoveMessage(column);
        return false; // Column is full
    }


    /**
     * Checks if the game has ended in a draw. A draw occurs when there are no empty cells left on the
     * game board without any player winning.
     *
     * @return {@code true} if the game is a draw, {@code false} otherwise
     */
    public boolean isDraw() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                if (board[i][j] == '.') {
                    return false; // There is still an empty space
                }
            }
            client.sendDrawMessage();
        }
        return true; // No empty spaces
    }

    /**
     * Checks if the current player has won the game. This method checks for 4 consecutive markers
     * of the current player horizontally, vertically, or diagonally.
     *
     * @return {@code true} if the current player has won, {@code false} otherwise
     */
    public boolean checkWinner() {
        // Horizontal check
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns() - 3; j++) {
                if (board[i][j] == currentPlayer && 
                    board[i][j+1] == currentPlayer && 
                    board[i][j+2] == currentPlayer && 
                    board[i][j+3] == currentPlayer) {
                	client.sendWinMessage();
                    return true;
                }
            }
        }

        // Vertical check
        for (int i = 0; i < getRows() - 3; i++) {
            for (int j = 0; j < getColumns(); j++) {
                if (board[i][j] == currentPlayer && 
                    board[i+1][j] == currentPlayer && 
                    board[i+2][j] == currentPlayer && 
                    board[i+3][j] == currentPlayer) {
                	client.sendWinMessage();
                    return true;
                }
            }
        }

        // Diagonal (up-right) check
        for (int i = 3; i < getRows(); i++) {
            for (int j = 0; j < getColumns() - 3; j++) {
                if (board[i][j] == currentPlayer && 
                    board[i-1][j+1] == currentPlayer && 
                    board[i-2][j+2] == currentPlayer && 
                    board[i-3][j+3] == currentPlayer) {
                	client.sendWinMessage();
                	return true;
                }
            }
        }

        // Diagonal (up-left) check
        for (int i = 3; i < getRows(); i++) {
            for (int j = 3; j < getColumns(); j++) {
                if (board[i][j] == currentPlayer && 
                    board[i-1][j-1] == currentPlayer && 
                    board[i-2][j-2] == currentPlayer && 
                    board[i-3][j-3] == currentPlayer) {
                	client.sendWinMessage();
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Switches the turn to the next player.
     */
    public void switchPlayer() {
        currentPlayer = (currentPlayer == 'R') ? 'Y' : 'R';
        client.sendPlayerSwitchMessage(currentPlayer);
    }

    /**
     * Prints the current state of the game board to the console. This method is primarily for
     * debugging purposes.
     */
    public void displayBoard() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(); // Add an extra line for better readability
    }
    
    /**
     * Finds the next available row in the specified column that can be occupied by a player's move.
     * If the column is full, this method returns -1.
     *
     * @param column the column to check
     * @return the index of the next available row, or -1 if the column is full
     */
    int findNextRow(int column) {
        for (int i = getRows() - 1; i >= 0; i--) {
            if (getBoardValue(i, column) == '.') {
                return i;
            }
        }
        return -1; // Column is full
    }

    /**
     * Retrieves the value at the specified row and column on the game board.
     *
     * @param row the row index
     * @param column the column index
     * @return the character value at the specified location on the board
     */
    public char getBoardValue(int row, int column) {
        return board[row][column];
    }

    /**
     * Gets the current player ('R' or 'Y').
     *
     * @return the current player
     */
    public char getCurrentPlayer() {
        return currentPlayer;
    }

	/**
	 * @return the columns
	 */
	public static int getColumns() {
		return COLUMNS;
	}

	/**
	 * @return the rows
	 */
	public static int getRows() {
		return ROWS;
	}
	
	public void updateGameState(char[][] updatedBoard, char updatedPlayer) {
	    this.board = updatedBoard;
	    this.currentPlayer = updatedPlayer;
	}

	/**
	 * @return the board
	 */
	char[][] getBoard() {
		return board;
	}
}
