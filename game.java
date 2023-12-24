import java.lang.reflect.Array;
import java.util.*;

/**
 * Axis - Represents a valid movement vector for a given move.
 * In the peg game, there are three defined movement paths.
 */
enum Axis {
    /**
     * DIAGONAL_LEFT - The axis in which the y-value changes, while the x-value remains constant.
     * As seen from a traditional board, this move would be going either "down and left" or "up and right".
     */
    DIAGONAL_LEFT,
    /**
     * DIAGONAL_RIGHT - The axis in which the y-value changes, while also the x-values change in the same manner.
     * As seen from a traditional board, this move would be going either "down and right" or "up and left".
     */
    DIAGONAL_RIGHT,
    /**
     * HORIZONTAL - The axis in which the x-value changes, while the y-value remains constant.
     * As seen from a traditional board, this move would be going either "right" or "left"
     */
    HORIZONTAL
}

/**
 * Direction - Specifies the movement in either an increasing or decreasing manner on a given axis.
 * In the peg game, there are two directions.
 */
enum Direction {
    /**
     * FORWARD - The direction of increasing axis values.
     *   - For DIAGONAL_LEFT axis: y-values increases.
     *   - For DIAGONAL_RIGHT axis: y and x-values increases.
     *   - For HORIZONTAL values, x-values increases.
     */
    FORWARD,
    /**
     * FORWARD - The direction of decreasing axis values.
     *   - For DIAGONAL_LEFT axis: y-values decreases.
     *   - For DIAGONAL_RIGHT axis: y and x-values decreases.
     *   - For HORIZONTAL values, x-values decreases.
     */
    BACKWARD
}

/**
 * Move - A class which represents the destination x and y values for a target peg,
 * as well as the axis and direction of peg activity.
 */
class Move {
    /**
     * this.axis - The axis of movement for this move
     */
    Axis axis;
    /**
     * this.direction - The direction of movement for this move
     */
    Direction direction;
    /**
     * this.x - The x-coordinate of the end of a move, where the peg movement completes.
     */
    int x;
    /**
     * Move.y - The y-coordinate of the end of a move, where the peg movement completes.
     */
    int y;
    public Move(Axis axis, Direction direction, int destX, int destY) {
        this.axis = axis;
        this.direction = direction;
        this.x = destX;
        this.y = destY;
    }

    /**
     * toString() - returns a formatted string specifying the peg movement from
     * the <code>(starting, pos) -> (ending, pos)</code>.
     * <strong>Note:</strong> the formatted coordinates are specified in <code>(y,x)</code> fashion.
     *
     * @return Formatted string, showing peg movement for this move.
     */
    public String toString() {
        return "(" + game.getStartingY(axis, direction, y) + ", " + game.getStartingX(axis, direction, x) + ") " +
                "-> (" + y + ", " + x + ")";
    }
}

/**
 * game - A class which holds the solving methods to solve a given peg board,
 * as well as miscellaneous helper methods for solving the infamouspeg game,
 * printing the board, and computing peg locations.
 */
public class game {

    static boolean solutionFound = false;
    static int iterationCount = 0;

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

                if (board[y][x] != false || board[furthestY][furthestX] != true || board[middleY][middleX] != true) {
                    continue;
                }

                // Now, proceed to add the move to the list of valid moves.
                validMoves.add(new Move(a, d, x, y));
            }
        }


        return validMoves;
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

    private static void undoMove(boolean[][] board, ArrayList<Move> movesTaken, Move m) {
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

        // Remove this move from the list of moves taken
        movesTaken.remove(m);
    }

    public static ArrayList<Move> solveHelper(boolean[][] board, ArrayList<Move> movesTaken, int level) {

        // Check iteration counter
        if (iterationCount++ % 1000000 == 0) {
            System.out.println("Iteration: " + iterationCount);
            printBoard(board);
        }

        // Base case: check if no more moves can be made
        if (movesTaken.size() == numMoves(board)) {
            //if (numPegs(board) == 1) {
            System.out.println("SOLUTION FOUND:\nFinal Iteration: " + iterationCount);
            solutionFound = true;
            return movesTaken;
        }

        // Get the list of all possible moves
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

        // Randomize all moves list
        //Collections.shuffle(allMoves);

        // From the list of all moves, recursively call
        // this method by making all possible moves
        ArrayList<Move> returned;
        for (Move m : allMoves) {
            // Take move
            takeMove(board, m, movesTaken);

            // Find next moves
            returned = solveHelper(board, movesTaken, level+1);

            // If the solution was found in the returned list of solutions,
            // return the list of solutions
            if (solutionFound)  return returned;

            // Undo this move
            undoMove(board, movesTaken, m);
        }

        return null;
    }

    public static boolean solveBoard(boolean[][] board) {

        ArrayList<Move> moves;  // Keep a list of moves taken
        boolean[][] originalBoard = copyBoard(board);  // Keep the original board state if a solution is found.


        // Solve board
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

    private static boolean[][] copyBoard(boolean[][] board) {
        boolean[][] newBoard = new boolean[board.length][];

        for (int i = 0; i < board.length; i++) {

            newBoard[i] = new boolean[board[i].length];

            for (int j = 0; j < board[i].length; j++) {
                newBoard[i][j] = board[i][j];
            }
        }

        return newBoard;
    }

    public static void main(String[] args) {

        boolean[][] selectedBoard;

        Scanner s = new Scanner(System.in);

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
                {true, true, true},
                {true, true, true, true},
                {true, true, true, true, true},
                {true, true, true, true, true, true},
                {true, true, true, true, true, true, false}
        };

        selectedBoard = smallBoard;


        System.out.println(" Starting Board:");

        printBoard(selectedBoard);
        solveBoard(selectedBoard);
    }

    private static void printBoard(boolean[][] board) {
        int magicNum = board.length;

        System.out.println("-----------------");
        System.out.println();

        StringBuilder line = new StringBuilder();

        for (int i = 0; i < board.length; i++) {

            line.append("   ");

            for (int j = 0; j < magicNum - i; j++) {
                line.append(" ");
            }

            for (int j = 0; j < board[i].length; j++) {
                line.append(board[i][j]?"| ":"o ");
            }

            System.out.println(line);
            line.delete(0, line.length());
        }

        System.out.println();
        System.out.println("-----------------");
        System.out.println();
    }
}
