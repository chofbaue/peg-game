/**
 * Move - A class which represents the destination x and y values for a target peg,
 * as well as the axis and direction of peg activity.
 */
public class Move {
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
        return "(" + Game.getStartingY(axis, direction, y) + ", " + Game.getStartingX(axis, direction, x) + ") " +
                "-> (" + y + ", " + x + ")";
    }
}
