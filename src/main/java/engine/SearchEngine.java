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
        List<Integer> bestLine = new ArrayList<>();
        int bestMove = -1;
        int bestScore = -INF;
        nodes = 0;

        for(int move=0; move<6; move++) {

            if(board.pits()[move] == 0)
                continue;

            MoveResult result =
                    KalahRules.applyMove(
                            board,
                            move,
                            true
                    );

            int score =
                    alphaBeta(
                            result.board(),
                            depth - 1,
                            -INF,
                            INF,
                            result.extraTurn()
                    );

            if(score > bestScore) {

                bestScore = score;
                bestMove = move;
                bestLine.clear();
                bestLine.add(move);
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

                int score =
                        alphaBeta(
                                result.board(),
                                depth - 1,
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

            int score =
                    alphaBeta(
                            result.board(),
                            depth - 1,
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


}

