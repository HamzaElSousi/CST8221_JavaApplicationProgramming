public class Connect4Game {
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private char[][] board;
    private char currentPlayer;

    public Connect4Game() {
        board = new char[getRows()][getColumns()];
        currentPlayer = 'R'; // Starting player, R for Red, Y for Yellow
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                board[i][j] = '.';
            }
        }
    }

    public boolean makeMove(int column) {
        if (column < 0 || column >= getColumns()) {
            return false; // Invalid column
        }

        for (int i = getRows() - 1; i >= 0; i--) {
            if (board[i][column] == '.') {
                board[i][column] = currentPlayer;
                return true;
            }
        }

        return false; // Column is full
    }

    public boolean isDraw() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                if (board[i][j] == '.') {
                    return false; // There is still an empty space
                }
            }
        }
        return true; // No empty spaces
    }

    public boolean checkWinner() {
        // Horizontal check
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns() - 3; j++) {
                if (board[i][j] == currentPlayer && 
                    board[i][j+1] == currentPlayer && 
                    board[i][j+2] == currentPlayer && 
                    board[i][j+3] == currentPlayer) {
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
                    return true;
                }
            }
        }

        return false;
    }


    public void switchPlayer() {
        currentPlayer = (currentPlayer == 'R') ? 'Y' : 'R';
    }

    public void displayBoard() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(); // Add an extra line for better readability
    }

    public char getBoardValue(int row, int column) {
        return board[row][column];
    }

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
}
