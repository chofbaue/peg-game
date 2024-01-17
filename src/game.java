import java.util.ArrayList;
import java.util.Scanner;

/**
 * game - A class which holds the solving methods to solve a given peg board,
 * as well as miscellaneous helper methods for solving the infamous peg game,
 * printing the board, and computing peg locations.
 */
public class game {

    static boolean solutionFound = false;
    static int iterationCount = 0;
    static boolean isPerfectBoard = false;

    public static int getStartingX(Axis a, Direction d, int destX) {

        switch (a) {
            case DIAGONAL_LEFT -> {
                return destX;
            }
            case DIAGONAL_RIGHT, HORIZONTAL -> {
                return destX + (d == Direction.FORWARD ? 2 : 0) - (d == Direction.BACKWARD ? 2 : 0);
            }
        }

        return -1;
    }

    public static int getStartingY(Axis a, Direction d, int destY) {

        switch (a) {
            case DIAGONAL_LEFT, DIAGONAL_RIGHT -> {
                return destY + (d == Direction.FORWARD ? 2 : 0) - (d == Direction.BACKWARD ? 2 : 0);
            }
            case HORIZONTAL -> {
                return destY;
            }
        }

        return -1;
    }

    public static ArrayList<Move> getValidMoves(int y, int x, boolean[][] board) {

        ArrayList<Move> validMoves = new ArrayList<>();

        for (Axis a : Axis.values()) {
            for (Direction d : Direction.values()) {

                // Get the position of the hypothetical peg starting position
                int furthestX = getStartingX(a, d, x);
                int furthestY = getStartingY(a, d, y);

                // Check if this move is legal
                if (furthestX < 0 || furthestY < 0
                        || furthestY >= board.length || furthestX >= board[furthestY].length) {
                    continue;
                }


                // Check if all the spaces are valid
                // 1) Destination must be empty
                // 2) The starting position must have a peg
                // 3) The position in between 1 & 2 must have a peg
                int middleX = (x + furthestX)/2;
                int middleY = (y + furthestY)/2;

                if (board[y][x] || board[furthestY][furthestX] != true || board[middleY][middleX] != true) {
                    continue;
                }

                // Now, proceed to add the move to the list of valid moves.
                validMoves.add(new Move(a, d, x, y));
            }
        }


        return validMoves;
    }

    public static ArrayList<Move> getAllMoves(boolean[][] board) {

        ArrayList<Move> allMoves = new ArrayList<>();
        ArrayList<Move> discoveredMoves;

        // Iterate through all possible spaces on the board
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {

                // A spot that is occupied by a peg is TRUE
                // Meaning that a move cannot be performed at
                // this space.
                if (board[i][j]) {
                    continue;
                }

                // Discover all valid move options
                discoveredMoves = getValidMoves(i, j, board);

                // Add all discovered moves into "allMoves"
                allMoves.addAll(discoveredMoves);
            }
        }

        return allMoves;
    }

    /**
     * numPegs - A bloated method of <code>numMoves</code>, which counts each spot on the board which has a peg.
     * @param board - The board to solve
     * @return count - Maximum number of moves to achieve an optimal solution.
     */
    private static int numPegs(boolean[][] board) {
        int count = 0;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                count += board[i][j] ? 1 : 0;
            }
        }

        return count;
    }

    /**
     * numMoves - A simplified method of <code>numPegs</code>, which adds the length of every row
     * (number of possible x-values on each row), iterating on each row (the y-axis).
     * This method should be used specifically on "perfect boards".
     *
     * @param board The board to solve
     * @return Maximum number of moves to achieve an optimal solution.
     */
    private static int numMoves(boolean[][] board) {
        int count = 0;

        for (int i = 0; i < board.length; i++) {
            count+=(i+1);
        }

        return count-2;
    }

    /**
     * takeMove - Will take move on the board
     *
     * @param board The board to make a move on
     * @param m Individual move to make on the specified board
     */
    private static void takeMove(boolean[][] board, Move m) {
        // Coordinate of selected peg to move
        int x1 = getStartingX(m.axis, m.direction, m.x);
        int y1 = getStartingY(m.axis, m.direction, m.y);

        // Coordinate of peg in between selected peg and destination
        int x2 = (x1 + m.x)/2;
        int y2 = (y1 + m.y)/2;

        // Coordinate of destination location which selected peg
        // will move to
        int x3 = m.x;
        int y3 = m.y;

        // Make move on board
        board[y1][x1] = false;
        board[y2][x2] = false;
        board[y3][x3] = true;
    }

    /**
     * takeMove - Will first take move on the board, then will add the move to the list of moves taken.
     *
     * @param board The board to make a move on.
     * @param m Individual move to make on the specified board
     * @param movesTaken The list of moves to add the specified move (m) to.
     */
    private static void takeMove(boolean[][] board, Move m, ArrayList<Move> movesTaken) {
        takeMove(board, m);

        // add this move to the list of moves taken.
        movesTaken.add(m);
    }

    private static void undoMove(boolean[][] board, Move m) {
        // Coordinate of original peg location
        int x1 = getStartingX(m.axis, m.direction, m.x);
        int y1 = getStartingY(m.axis, m.direction, m.y);

        // Coordinate of peg in between moved peg and original position
        int x2 = (x1 + m.x)/2;
        int y2 = (y1 + m.y)/2;

        // Coordinate of destination location which selected peg
        // moved to
        int x3 = m.x;
        int y3 = m.y;

        // Undo move on board
        board[y1][x1] = true;
        board[y2][x2] = true;
        board[y3][x3] = false;
    }

    private static void undoMove(boolean[][] board, Move m, ArrayList<Move> movesTaken) {

        undoMove(board, m);

        // Remove this move from the list of moves taken
        movesTaken.remove(m);
    }

    public static ArrayList<Move> solveHelper(boolean[][] board, ArrayList<Move> movesTaken, int level) {

        // Check iteration counter
        if (iterationCount++ % 1000000 == 0) {
            System.out.println("Iteration: " + iterationCount + "\nDepth: " + level);
            printBoard(board);
        }

        // Base case: check if no more moves can be made.
        // Base case uses two different algorithms: one is a "shortcut" if the
        // board is a shape of an equilateral triangle, and the other counts
        // each individual space occupied by a peg.
        if ((isPerfectBoard && movesTaken.size() == numMoves(board)) ||
                (!isPerfectBoard && movesTaken.size() == numPegs(board))) {
            System.out.println("SOLUTION FOUND:\nFinal Iteration: " + iterationCount);
            solutionFound = true;
            return movesTaken;
        }

        // Get the list of all possible moves
        ArrayList<Move> allPossibleMoves;
        allPossibleMoves = getAllMoves(board);

        // Sort allPossibleMoves list
        //Collections.shuffle(allPossibleMoves);    // Randomize allPossibleMoves list
        //sortByMaxMoves(board, allPossibleMoves);    // Sort allPossibleMoves list by max order
        allPossibleMoves = sortByMaxMoves(board, allPossibleMoves);

        // From the list of all moves, recursively call
        // this method by making all possible moves
        ArrayList<Move> nextMoves;

        for (Move m : allPossibleMoves) {
            // Take move
            takeMove(board, m, movesTaken);

            // Find next moves
            // The nextMoves arraylist can be null, however this is the edge case
            // where all move choices are exhausted with no further solution.
            nextMoves = solveHelper(board, movesTaken, level+1);

            // If the solution was found in the nextMoves list of solutions,
            // return the list of solutions
            if (solutionFound)  return nextMoves;

            // Undo this move
            undoMove(board, m, movesTaken);
        }

        // After exhausting through allPossibleMoves list, there are no other
        // options to make. In this case, return null.
        return null;
    }

    /**
     * sortByMaxMoves - Sorts all possible next moves as a max heap,
     * based on the number of possible moves when one move is taken
     * from the list of all possible moves.
     *
     * @param board
     * @param allMoves
     */
    public static ArrayList<Move> sortByMaxMoves(boolean[][] board, ArrayList<Move> allMoves) {

        ArrayList<Move> sortedMoves = new ArrayList<>();
        ArrayList<Integer> sortedNumMoves = new ArrayList<>();

        // nextMoveBoard - copy of the inputted board which
        // simulates any next possible move
        boolean[][] nextMoveBoard = copyBoard(board);

        // Records the number of moves (i.e., the size of the
        // number of all possible move combinations, resulting
        // from the call to getAllMoves)
        int numMoves;

        // Iterate through each move in inputted moves list
        for (Move m : allMoves) {

            // Simulate taking next move
            takeMove(nextMoveBoard, m);

            // Find out how many moves we can yield after
            // taking next move from the simulated board
            numMoves = getAllMoves(nextMoveBoard).size();

            // Insert as a max heap
            insertByNumMoves(m, numMoves, sortedMoves, sortedNumMoves);

            // Reset board
            undoMove(board, m);
        }

        return sortedMoves;

    }

    /**
     *
     * insertByNumMoves - mutates inputted lists by inserting each
     * element as a "max heap" with respect to numMoves
     *
     * @param m
     * @param numMoves
     * @param sortedMoves
     * @param sortedNumMoves
     */
    private static void insertByNumMoves(
            Move m,
            int numMoves,
            ArrayList<Move> sortedMoves,
            ArrayList<Integer> sortedNumMoves) {

        if (sortedMoves.isEmpty()) {
            sortedMoves.add(m);
            sortedNumMoves.add(numMoves);
            return;
        }

        int index = 0;

        for (Integer i : sortedNumMoves) {

            if (numMoves > i) {
                break;
            }

            index++;
        }

        sortedMoves.add(index, m);
        sortedNumMoves.add(index, numMoves);
    }

    /**
     * solveBoard - A method to solve the board and print out detailed
     * steps (per move) on how to solve the board
     * @param board
     * @return
     */
    public static boolean solveBoard(boolean[][] board) {

        ArrayList<Move> moves;  // Keep a list of moves taken
        boolean[][] originalBoard = copyBoard(board);  // Keep the original board state if a solution is found.

        // Print out the starting board
        System.out.println(" Starting Board:");
        printBoard(board);

        // Check if the board is "perfect", meaning it takes a shape of an equilateral triangle
        // As described in the method's JavaDoc, this will select the proper algorithm to execute,
        // where a perfect board has a theoretically more optimized algorithm.
        setIsPerfectBoard(board);

        // Solve the board (utilizing recursion).
        // Get the list of moves (or null if no solution).
        moves = solveHelper(board, new ArrayList<>(), 0);

        // If the board is unsolveable, there is no solution
        // Return false to indicate this, as well as printing to STDOUT
        if (moves == null) {
            System.out.println("No solution");
            return false;
        }

        // A solution is found, print out step by step
        // the moves and board states corresponding to
        // the solution
        int counter = 1;


        System.out.println("Moves:");
        System.out.println("-----------------");
        System.out.println("(y, x) -> (y, x)");
        System.out.println("=================");
        System.out.println();

        for (Move m : moves) {
            System.out.println("Move: " + counter++);
            System.out.println(m);
            takeMove(originalBoard, m);
            printBoard(originalBoard);
        }

        // Return true, indicating a solution has been found
        return true;
    }

    /**
     * setIsPerfectBoard - checks if the board takes the shape of an equilateral triangle.
     * This determines the algorithm to check the number of moves, where a "perfect board"
     * has a theoretically faster algorithm.
     *
     * @param board
     */
    private static void setIsPerfectBoard(boolean[][] board) {

        int prev = -1;

        for (boolean[] row : board) {

            if (prev != -1 && Math.abs(row.length - prev) != 1) {
                isPerfectBoard = false;
                return;
            }

            prev = row.length;
        }

        isPerfectBoard = true;
    }

    private static boolean[][] copyBoard(boolean[][] board) {
        boolean[][] newBoard = new boolean[board.length][];

        for (int i = 0; i < board.length; i++) {

            newBoard[i] = new boolean[board[i].length];

            System.arraycopy(board[i], 0, newBoard[i], 0, board[i].length);
        }

        return newBoard;
    }

    public static void main(String[] args) {

        final boolean stdinInput = true;
        final String terminatingStr = "q";

        boolean[][] selectedBoard;
        Scanner s;
        StringBuilder builder;
        String line;

        //In the future: Check args[] to determine whether to read from STDIN or not.

        boolean[][] smallBoard = {
                {true},
                {false, true},
                {true, true, true},
                {true, true, true, true}
        };

        boolean[][] standardBoard = {
                {false},
                {true, true},
                {true, true, true},
                {true, true, true, true},
                {true, true, true, true, true}
        };

        boolean[][] largeBoard = {
                {false},
                {true, true},
                {true, true, true},
                {true, true, true, true},
                {true, true, true, true, true},
                {true, true, true, true, true, true}
        };

        boolean[][] megaBoard = {
                {true},
                {true, true},
                {false, true, true},
                {true, true, true, true},
                {true, true, true, true, true},
                {true, true, true, true, true, true},
                {true, true, true, true, true, true, true}
        };

        // If stdinInput flag is selected, input board from STDIN using Util.Scanner
        if (stdinInput) {
            builder = new StringBuilder();
            s = new Scanner(System.in);

            System.out.println("""
                ====================
                Please enter the board in the form of 0's and 1's, terminated by a 'q' on a separate line.
                For example:
                0
                11
                111
                1111
                q
                ===================="""
            );

            while (s.hasNextLine()) {
                // Extract input from scanner
                line = s.next();

                // Check if the input string is the terminating 'q'.
                if (line.compareTo(terminatingStr) == 0) {
                    break;
                }

                // Append the line to the builder, followed by a newline char.
                builder.append(line).append("\n");
            }

            // It is most appropriate to close the scanner.
            s.close();

            // selectedBoard - Defines which board will be solved.
            selectedBoard = createBoardFromInput(builder.toString());

            // Proceed to solve the board.
            solveBoard(selectedBoard);

            return;
        }

        // selectedBoard - Defines which board will be solved.
        selectedBoard = megaBoard;

        // Proceed to solve the board.
        solveBoard(selectedBoard);
    }

    private static boolean[][] createBoardFromInput(String s) {

        boolean[][] rows = new boolean[0][];
        boolean[] col = new boolean[0];

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            // In the future: Refactor this switch statement for readability
            switch (c) {

                case '\n' -> {
                    // Expand rows table by 1 row
                    boolean[][] newRows = new boolean[rows.length + 1][];

                    // Copy contents of rows table into newRows table
                    System.arraycopy(rows, 0, newRows, 0, rows.length);

                    // Insert col array into open position in rows table
                    newRows[newRows.length - 1] = col;

                    // Set rows table as newRows
                    rows = newRows;

                    // Set col to be an empty array
                    col = new boolean[0];
                }

                case ' ' -> { /* Empty, continue through loop */ }

                case '1' -> {
                    // Expand col array by 1 to accommodate for new value
                    boolean[] newCol = new boolean[col.length + 1];

                    // Copy contents of col array to newCol array
                    System.arraycopy(col, 0, newCol, 0, col.length);

                    // Set value at end of array to be true, since the
                    // input specified this position to be occupied by a peg
                    newCol[newCol.length - 1] = true;

                    // Set col array as newCol
                    col = newCol;
                }

                case '0' -> {
                    // Expand col array by 1 to accommodate for new value
                    boolean[] newCol = new boolean[col.length + 1];

                    // Copy contents of col array to newCol array
                    System.arraycopy(col, 0, newCol, 0, col.length);

                    // Set value at end of array to be false, since the
                    // input specified this position to be unoccupied
                    newCol[newCol.length - 1] = false;

                    // Set col array as newCol
                    col = newCol;
                }

                default -> {
                    System.err.println("Error: Invalid Character! (" + c + ") is not a valid board input");
                    System.exit(1);
                }
            }
        }


        return rows;
    }

    private static void printBoard(boolean[][] board) {
        int magicNum = board.length;

        // Construct the boarder which surrounds the game board.
        // This boarder stretches as needed to accommodate for the board size.
        StringBuilder dynamicBoarder = getDynamicBoarder(board);

        System.out.println(dynamicBoarder);
        System.out.println();

        StringBuilder line = new StringBuilder();

        for (int i = 0; i < board.length; i++) {

            line.append("   ");

            line.append(" ".repeat(magicNum - i));

            for (int j = 0; j < board[i].length; j++) {
                line.append(board[i][j]?"| ":"o ");
            }

            System.out.println(line);
            line.delete(0, line.length());
        }

        System.out.println();
        System.out.println(dynamicBoarder);
        System.out.println();
    }

    /**
     * getDynamicBoarder - Constructs the dynamic boarder used by printBoard().
     * This boarder stretches as needed to accommodate for the size of the board.
     * 
     * @param board - The board to construct the boarder with.
     * 
     * @return A StringBuilder Object that contains the "fitted" boarder String.
     */
    private static StringBuilder getDynamicBoarder(boolean[][] board) {
        final String boarderUnit = "--";
        final String boarderFront = "---";
        final String boarderTail = "----";
        StringBuilder dynamicBoarder = new StringBuilder(boarderFront);

        dynamicBoarder.append(boarderUnit.repeat(board[board.length - 1].length));

        dynamicBoarder.append(boarderTail);
        return dynamicBoarder;
    }
}
