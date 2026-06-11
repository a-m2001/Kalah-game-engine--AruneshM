package engine;

import java.util.Arrays;

public class Board {

    private final int[] pits;

    public Board() {
        pits = new int[14];

        for (int i = 0; i < 6; i++) {
            pits[i] = 4;
            pits[i + 7] = 4;
        }
    }

    public Board(Board other) {
        pits = Arrays.copyOf(other.pits, 14);
    }

    public Board copy() {
        return new Board(this);
    }

    public int[] pits() {
        return pits;
    }

    public boolean isGameOver() {

        boolean myEmpty = true;
        boolean oppEmpty = true;

        for (int i = 0; i < 6; i++) {
            if (pits[i] != 0) {
                myEmpty = false;
                break;
            }
        }

        for (int i = 7; i < 13; i++) {
            if (pits[i] != 0) {
                oppEmpty = false;
                break;
            }
        }

        return myEmpty || oppEmpty;
    }

    public void collectRemaining() {

        int my = 0;
        int opp = 0;

        for (int i = 0; i < 6; i++) {
            my += pits[i];
            pits[i] = 0;
        }

        for (int i = 7; i < 13; i++) {
            opp += pits[i];
            pits[i] = 0;
        }

        pits[6] += my;
        pits[13] += opp;
    }

    public void print() {

        System.out.println();

        System.out.println("            Opponent");

        System.out.printf(
                "      [%2d][%2d][%2d][%2d][%2d][%2d]%n",
                pits[12], pits[11], pits[10],
                pits[9], pits[8], pits[7]
        );

        System.out.printf(
                " [%2d]                         [%2d]%n",
                pits[13], pits[6]
        );

        System.out.printf(
                "      [%2d][%2d][%2d][%2d][%2d][%2d]%n",
                pits[0], pits[1], pits[2],
                pits[3], pits[4], pits[5]
        );

        System.out.println("              You");
        System.out.println();

        System.out.println(
                "Your pits: 0 1 2 3 4 5"
        );

        System.out.println(
                "Opponent pits: 0 1 2 3 4 5"
        );

        System.out.println();
    }
}