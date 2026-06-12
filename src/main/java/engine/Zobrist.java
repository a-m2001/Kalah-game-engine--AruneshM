package engine;

import java.util.Random;

public class Zobrist {

    private static final long[][] TABLE =
            new long[14][49];

    static {

        Random random =
                new Random(42);

        for(int pit=0; pit<14; pit++) {

            for(int stones=0;
                stones<49;
                stones++) {

                TABLE[pit][stones] =
                        random.nextLong();
            }
        }
    }

    public static long hash(
            Board board
    ) {

        long h = 0;

        int[] p =
                board.pits();

        for(int pit=0;
            pit<14;
            pit++) {

            int stones =
                    Math.min(
                            p[pit],
                            48
                    );

            h ^= TABLE[pit][stones];
        }

        return h;
    }
}