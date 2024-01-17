//import org.junit.Test;

import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;

public class gameTest {

    @Test
    public void testGame() {
        boolean[][] board = {
                {false},
                {true, false},
                {true, false, false},
                {false, true, false, false}
        };

        boolean[][] outcome1 = {
                {false},
                {false, false},
                {false, false, false},
                {true, true, false, false}
        };

        boolean[][] outcome2 = {
                {true},
                {false, false},
                {false, false, false},
                {false, true, false, false}
        };

        ArrayList<Move> allMoves = game.getAllMoves(board);
        System.out.println(Arrays.toString(allMoves.toArray()));

        //assertEquals()
    }
}
