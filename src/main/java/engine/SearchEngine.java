package engine;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SearchEngine {

    private static final int INF = 1_000_000;
    private static long nodes;
    public static boolean DEBUG = false;
    private static final Map<Long, TTEntry> tt = new HashMap<>();

    public static long getNodes() {
        return nodes;
    }
    public static SearchResult bestMove(
            Board board,
            int depth
    ) {
nodes = 0;

int bestMove = -1;
int bestScore = -INF;

List<Integer> bestLine =
        new ArrayList<>();

for(int move=0; move<6; move++) {

    if(board.pits()[move] == 0)
        continue;

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
        long key = board.hash();
        TTEntry entry = tt.get(key);

        if (entry != null && entry.depth() >= depth) {
                return entry.score();
        }
        if(depth == 0 || board.isGameOver()) {
                int eval = Evaluator.evaluate(board);
                tt.put(key, new TTEntry(depth,eval));
                return eval;
        }

        if(maximizing) {

            int value = -INF;

            for(int move=0; move<6; move++) {

                if(board.pits()[move] == 0)
                    continue;

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
                value
        )
);
            return value;
        }

        int value = INF;

        for(int move=7; move<13; move++) {

            if(board.pits()[move] == 0)
                continue;

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
                value
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

    long key = board.hash();

    TTEntry entry = tt.get(key);

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

        return new PVResult(
                eval,
                new ArrayList<>()
        );
    }

    List<Integer> bestLine =
            new ArrayList<>();

    if(maximizing) {

        int bestScore = -INF;

        for(int move=0; move<6; move++) {

            if(board.pits()[move] == 0)
                continue;

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

            if(beta <= alpha)
                break;
        }

        return new PVResult(
                bestScore,
                bestLine
        );
    }

    int bestScore = INF;

    for(int move=7; move<13; move++) {

        if(board.pits()[move] == 0)
            continue;

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

        if(beta <= alpha)
            break;
    }

    return new PVResult(
            bestScore,
            bestLine
    );
}

}

