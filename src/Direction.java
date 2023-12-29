/**
 * Direction - Specifies the movement in either an increasing or decreasing manner on a given axis.
 * In the peg game, there are two directions.
 */
public enum Direction {
    /**
     * FORWARD - The direction of increasing axis values.
     * - For DIAGONAL_LEFT axis: y-values increases.
     * - For DIAGONAL_RIGHT axis: y and x-values increases.
     * - For HORIZONTAL values, x-values increases.
     */
    FORWARD,
    /**
     * FORWARD - The direction of decreasing axis values.
     * - For DIAGONAL_LEFT axis: y-values decreases.
     * - For DIAGONAL_RIGHT axis: y and x-values decreases.
     * - For HORIZONTAL values, x-values decreases.
     */
    BACKWARD
}
