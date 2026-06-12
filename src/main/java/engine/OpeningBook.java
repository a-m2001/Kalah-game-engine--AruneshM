package engine;

import java.util.HashMap;
import java.util.Map;

public class OpeningBook {

    private static final Map<Long, Integer> moves =
            new HashMap<>();

    static {
        add(new Board(), 2);
    }

    public static Integer lookup(Board board) {
        return moves.get(
                Zobrist.hash(board)
        );
    }

    public static void add(
            Board board,
            int move
    ) {
        moves.put(
                Zobrist.hash(board),
                move
        );
    }
}
