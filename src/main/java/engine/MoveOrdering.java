package engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MoveOrdering {

    public static List<Integer> orderedMoves(
            Board board,
            boolean myTurn
    ) {

        List<Integer> moves =
                new ArrayList<>();

        int start =
                myTurn ? 0 : 7;

        int end =
                myTurn ? 6 : 13;

        for(int move=start;
            move<end;
            move++) {

            if(board.pits()[move] > 0) {
                moves.add(move);
            }
        }

        moves.sort(
                Comparator.comparingInt(
                        m -> -scoreMove(
                                board,
                                m,
                                myTurn
                        )
                )
        );

        return moves;
    }

    private static int scoreMove(
            Board board,
            int move,
            boolean myTurn
    ) {

        int score = 0;

        int stones =
                board.pits()[move];

        int store =
                myTurn ? 6 : 13;

        int distance =
                store - move;

        if(distance < 0)
            distance += 14;

        if(stones == distance) {
            score += 1000;
        }

        MoveResult result =
                KalahRules.applyMove(
                        board,
                        move,
                        myTurn
                );

        int beforeStore =
                board.pits()[store];

        int afterStore =
                result.board().pits()[store];

        if(afterStore - beforeStore > 1) {
            score += 500;
        }

        score += stones;

        return score;
    }
}