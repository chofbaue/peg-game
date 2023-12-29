/**
 * Axis - Represents a valid movement vector for a given move.
 * In the peg game, there are three defined movement paths.
 */
public enum Axis {
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
