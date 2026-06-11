package engine;

public class KalahRules {

    public static MoveResult applyMove(
            Board board,
            int pit,
            boolean myTurn
    ) {

        Board next = board.copy();

        int[] p = next.pits();

        int stones = p[pit];

        if (stones == 0) {
            throw new IllegalArgumentException(
                    "Pit is empty"
            );
        }

        p[pit] = 0;

        int pos = pit;

        while (stones > 0) {

            pos = (pos + 1) % 14;

            if (myTurn && pos == 13)
                continue;

            if (!myTurn && pos == 6)
                continue;

            p[pos]++;
            stones--;
        }

        boolean extraTurn =
                (myTurn && pos == 6)
                        ||
                        (!myTurn && pos == 13);

        if (!extraTurn) {

            if (myTurn &&
                    pos >= 0 &&
                    pos <= 5 &&
                    p[pos] == 1) {

                int opposite = 12 - pos;

                if (p[opposite] > 0) {

                    p[6] += p[opposite] + 1;

                    p[pos] = 0;
                    p[opposite] = 0;
                }
            }

            if (!myTurn &&
                    pos >= 7 &&
                    pos <= 12 &&
                    p[pos] == 1) {

                int opposite = 12 - pos;

                if (p[opposite] > 0) {

                    p[13] += p[opposite] + 1;

                    p[pos] = 0;
                    p[opposite] = 0;
                }
            }
        }

        if (next.isGameOver()) {
            next.collectRemaining();
        }

        return new MoveResult(
                next,
                extraTurn
        );
    }
}