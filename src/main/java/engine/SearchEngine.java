package engine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import engine.MoveOrdering;

public class SearchEngine {

    private static final int INF = 1_000_000;
    private static final int MAX_DEPTH = 64;
    private static final int[][] killerMoves =
            new int[MAX_DEPTH][2];
    private static long nodes;
    public static boolean DEBUG = false;
    private static final Map<Long, TTEntry> tt = new HashMap<>();

    static {
        for(int[] moves : killerMoves) {
            Arrays.fill(moves, -1);
        }
    }

    public static long getNodes() {
        return nodes;
    }

    private static void recordKiller(
            int depth,
            int move
    ) {
        if(depth < 0 || depth >= MAX_DEPTH ||
           killerMoves[depth][0] == move ||
           killerMoves[depth][1] == move) {
            return;
        }

        killerMoves[depth][1] =
                killerMoves[depth][0];

        killerMoves[depth][0] = move;
    }

    private static int killerScore(
            int depth,
            int move
    ) {
        if(depth < 0 || depth >= MAX_DEPTH) {
            return 0;
        }

        if(killerMoves[depth][0] == move) {
            return 100000;
        }

        if(killerMoves[depth][1] == move) {
            return 50000;
        }

        return 0;
    }

    private static List<Integer> orderMovesWithKillers(
            Board board,
            boolean myTurn,
            int ttMove,
            int depth
    ) {
        List<Integer> moves =
                MoveOrdering.orderedMoves(
                        board,
                        myTurn,
                        ttMove
                );

        moves.sort(
                Comparator
                        .comparingInt(
                                (Integer move) ->
                                        move == ttMove ? 1 : 0
                        )
                        .thenComparingInt(
                                move -> killerScore(
                                        depth,
                                        move
                                )
                        )
                        .reversed()
        );

        return moves;
    }

    public static SearchResult bestMove(
            Board board,
            int depth
    ) {
nodes = 0;
tt.clear();

int bestMove = -1;
int bestScore = -INF;

List<Integer> bestLine =
        new ArrayList<>();

for(int move :
        MoveOrdering.orderedMoves(
                board,
                true
        )) {

    MoveResult result =
            KalahRules.applyMove(
                    board,
                    move,
                    true
            );

    int nextDepth =
            result.extraTurn()
                    ? depth
                    : depth - 1;

    PVResult child =
            alphaBetaPV(
                    result.board(),
                    nextDepth,
                    -INF,
                    INF,
                    result.extraTurn()
            );

    if(child.score() > bestScore) {

        bestScore =
                child.score();

        bestMove =
                move;

        bestLine =
                new ArrayList<>();

        bestLine.add(move);

        bestLine.addAll(
                child.line()
        );
    }
}

return new SearchResult(
        bestMove,
        bestScore,
        bestLine
);
    }

    private static int alphaBeta(
            Board board,
            int depth,
            int alpha,
            int beta,
            boolean maximizing
    ) {
        nodes++;
        long key =
        Zobrist.hash(
                board
        );
        TTEntry entry = tt.get(key);

        if (entry != null && entry.depth() >= depth) {
                return entry.score();
        }
        if(depth == 0 || board.isGameOver()) {
                int eval = Evaluator.evaluate(board);
                tt.put(key, new TTEntry(depth,eval, -1));
                return eval;
        }

        if(maximizing) {

            int value = -INF;

for(int move :
        MoveOrdering.orderedMoves(
                board,
                true
        )) {

                MoveResult result =
                        KalahRules.applyMove(
                                board,
                                move,
                                true
                        );

int nextDepth =
        result.extraTurn()
                ? depth
                : depth - 1;

int score =
        alphaBeta(
                result.board(),
                nextDepth,
                alpha,
                beta,
                result.extraTurn()
        );

                value = Math.max(
                        value,
                        score
                );

                alpha = Math.max(
                        alpha,
                        value
                );

                if(beta <= alpha)
                    break;
            }
            tt.put(
        key,
        new TTEntry(
                depth,
                value,
                -1
        )
);
            return value;
        }

        int value = INF;

for(int move :
        MoveOrdering.orderedMoves(
                board,
                false
        )) {

            MoveResult result =
                    KalahRules.applyMove(
                            board,
                            move,
                            false
                    );
int nextDepth =
        result.extraTurn()
                ? depth
                : depth - 1;

int score =
        alphaBeta(
                result.board(),
                nextDepth,
                alpha,
                beta,
                !result.extraTurn()
        );

            value = Math.min(
                    value,
                    score
            );

            beta = Math.min(
                    beta,
                    value
            );

            if(beta <= alpha)
                break;
        }

tt.put(
        key,
        new TTEntry(
                depth,
                value,
                -1
        )
);

        return value;
    }

public static SearchResult iterativeDeepening(
        Board board,
        int maxDepth
) {

    SearchResult best = null;

    for(int depth = 1;
        depth <= maxDepth;
        depth++) {

        best =
                bestMove(
                        board,
                        depth
                );
        if(DEBUG){
        System.out.println(
                "Depth "
                + depth
                + " complete"
        );
}      
    }

    return best;
}


private static PVResult alphaBetaPV(
        Board board,
        int depth,
        int alpha,
        int beta,
        boolean maximizing
) {

    nodes++;

    long key =
        Zobrist.hash(
                board
        );

    TTEntry entry = tt.get(key);

    int ttMove = -1;
    if(entry != null) {
        ttMove = entry.bestMove();
    }

    if(entry != null &&
       entry.depth() >= depth) {

        return new PVResult(
                entry.score(),
                new ArrayList<>()
        );
    }

if(depth == 0 || board.isGameOver()) {

    int eval =
            Evaluator.evaluate(board);

    tt.put(
            key,
            new TTEntry(
                    depth,
                    eval,
                    -1
            )
    );

    return new PVResult(
            eval,
            new ArrayList<>()
    );
}

    List<Integer> bestLine =
            new ArrayList<>();

    if(maximizing) {

        int bestScore = -INF;

for(int move :
        orderMovesWithKillers(
                board,
                true,
                ttMove,
                depth
        )) {

            MoveResult result =
                    KalahRules.applyMove(
                            board,
                            move,
                            true
                    );

            int nextDepth =
                    result.extraTurn()
                            ? depth
                            : depth - 1;

            PVResult child =
                    alphaBetaPV(
                            result.board(),
                            nextDepth,
                            alpha,
                            beta,
                            result.extraTurn()
                    );

            if(child.score() > bestScore) {

                bestScore =
                        child.score();

                bestLine =
                        new ArrayList<>();

                bestLine.add(move);
                bestLine.addAll(
                        child.line()
                );
            }

            alpha =
                    Math.max(
                            alpha,
                            bestScore
                    );

            if(beta <= alpha) {
                recordKiller(
                        depth,
                        move
                );
                break;
            }
        }

tt.put(
        key,
new TTEntry(
        depth,
        bestScore,
        bestLine.isEmpty()
                ? -1
                : bestLine.get(0)
)
);

return new PVResult(
        bestScore,
        bestLine
);
    }

    int bestScore = INF;

for(int move :
        orderMovesWithKillers(
                board,
                false,
                ttMove,
                depth
        )) {

        MoveResult result =
                KalahRules.applyMove(
                        board,
                        move,
                        false
                );

        int nextDepth =
                result.extraTurn()
                        ? depth
                        : depth - 1;

        PVResult child =
                alphaBetaPV(
                        result.board(),
                        nextDepth,
                        alpha,
                        beta,
                        !result.extraTurn()
                );

        if(child.score() < bestScore) {

            bestScore =
                    child.score();

            bestLine =
                    new ArrayList<>();

            bestLine.add(
                    move
            );

            bestLine.addAll(
                    child.line()
            );
        }

        beta =
                Math.min(
                        beta,
                        bestScore
                );

        if(beta <= alpha) {
            recordKiller(
                    depth,
                    move
            );
            break;
        }
    }

tt.put(
        key,
new TTEntry(
        depth,
        bestScore,
        bestLine.isEmpty()
                ? -1
                : bestLine.get(0)
)
);

return new PVResult(
        bestScore,
        bestLine
);
}

}
