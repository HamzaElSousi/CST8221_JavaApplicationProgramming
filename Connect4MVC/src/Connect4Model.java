/*
 * Student name && ID : Hamza El Sousi , 040982818
 * Student name && ID : Mansi Joshi , 041091664
 * Lab Prof: Paulo Sousa
 * Assignment: A12
 * Lab Prof: Paulo Sousa
 * MVC Desgin: MODEL 
 */


/**
 * Represents the model for a Connect Four game, holding the game's state including the board,
 * the current player, and the number of chips played by each player. This class provides methods
 * to initialize the game, make moves, check for a win or a draw, and switch the current player.
 */
public class Connect4Model {
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private char[][] board;
    private char currentPlayer;
    private int redChipsPlayed;
    private int yellowChipsPlayed;
    //public static final int EMPTY = -1;
    
    /**
     * Constant for Yellow player
     */
    public static final int RED_PLAYER = 0; // Add this constant
    /**
     * Constant for Yellow player
     */
    public static final int YELLOW_PLAYER = 1;

    /**
     * Initializes a new Connect4Model with an empty board and sets the starting player.
     */
    public Connect4Model() {
        board = new char[getRows()][getColumns()];
        currentPlayer = 'R'; // Starting player, R for Red, Y for Yellow
        initializeBoard();
        redChipsPlayed = 0;
        yellowChipsPlayed = 0;
    }

    /**
     * Initializes the game board to its starting state, with all cells empty.
     */
    void initializeBoard() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                board[i][j] = '.';
            }
        }
    }

    /**
     * Attempts to place a chip in the specified column for the current player. It automatically
     * calculates the appropriate row based on existing chips in the column.
     * 
     * @param column the column index where the current player wants to place a chip
     * @return true if the move was successful, false if the column is full or invalid
     */
    public boolean makeMove(int column) {
        if (column < 0 || column >= getColumns()) {
            return false; // Invalid column
        }

        for (int i = getRows() - 1; i >= 0; i--) {
            if (board[i][column] == '.') {
                board[i][column] = currentPlayer;
                if (currentPlayer == 'R') {
                    redChipsPlayed++;
                } else {
                    yellowChipsPlayed++;
                }
                return true;
            }
        }

        return false; // Column is full
    }


    /**
     * Checks if the game is a draw, meaning the board is full and no more moves are possible.
     * 
     * @return true if the game is a draw, false otherwise
     */
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

    /**
     * Checks for a winning condition for the current player. A win occurs if four chips
     * of the same player are aligned horizontally, vertically, or diagonally.
     * 
     * @return true if the current player has won, false otherwise
     */
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

    /**
     * Switches the current player from red to yellow or yellow to red.
     */
    public void switchPlayer() {
        currentPlayer = (currentPlayer == 'R') ? 'Y' : 'R';
    }
    
    /**
     * Prints the current state of the game board to the console, for debugging purposes.
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
     * Finds the next available row in the specified column that is empty.
     * 
     * @param column the column to check
     * @return the row index of the next empty space, or -1 if the column is full
     */
    int findNextRow(int column) {
        for (int i = getRows() - 1; i >= 0; i--) {
            if (getBoardValue(i, column) == '.') {
                return i;
            }
        }
        return -1; // Column is full
    }

 // Getter methods documentation...
    
    /**
     * Gets the value of the board at the specified row and column.
     * 
     * @param row the row index
     * @param column the column index
     * @return the character representing the chip at the specified location or '.' if empty
     */
    public char getBoardValue(int row, int column) {
        return board[row][column];
    }

    /**
     * Returns the current player's symbol, where 'R' represents the Red player and 'Y'
     * represents the Yellow player.
     * 
     * @return the character symbol of the current player
     */
    public char getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Retrieves the number of chips played by the Red player.
     * 
     * @return the total number of Red chips played on the board
     */
    public int getRedChipsPlayed() {
        return redChipsPlayed;
    }

    /**
     * Retrieves the number of chips played by the Yellow player.
     * 
     * @return the total number of Yellow chips played on the board
     */
    public int getYellowChipsPlayed() {
        return yellowChipsPlayed;
    }

    /**
     * Returns the number of columns in the Connect Four game board. This is a static
     * method because the board dimensions are constant and shared across all instances.
     * 
     * @return the number of columns in the game board
     */
    public static int getColumns() {
        return COLUMNS;
    }

    /**
     * Returns the number of rows in the Connect Four game board. Similar to {@code getColumns()},
     * this is a static method for the same reason.
     * 
     * @return the number of rows in the game board
     */
    public static int getRows() {
        return ROWS;
    }
}
