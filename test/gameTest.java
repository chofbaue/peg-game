import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class gameTest {

    @Test
    public void testGame() {

        // This board should have 2 possible moves to make
        final int boardMoves = 2;
        boolean[][] board = {
                {false},
                {true, false},
                {true, false, false},
                {false, true, false, false}
        };

        // This board should have 1 possible move to make
        final int outcome1Moves = 1;
        boolean[][] outcome1 = {
                {false},
                {false, false},
                {false, false, false},
                {true, true, false, false}
        };

        // This board should not have moves to make
        final int outcome2Moves = 0;
        boolean[][] outcome2 = {
                {true},
                {false, false},
                {false, false, false},
                {false, true, false, false}
        };

        ArrayList<Move> allMoves = game.getAllMoves(board);
        ArrayList<Move> allMoves1 = game.getAllMoves(outcome1);
        ArrayList<Move> allMoves2 = game.getAllMoves(outcome2);
        System.out.println(Arrays.toString(allMoves.toArray()));
        System.out.println(Arrays.toString(allMoves1.toArray()));
        System.out.println(Arrays.toString(allMoves2.toArray()));

        assertEquals(allMoves.size(), boardMoves);
        assertEquals(allMoves1.size(), outcome1Moves);
        assertEquals(allMoves2.size(), outcome2Moves);
    }
}
