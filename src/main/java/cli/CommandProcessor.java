package cli;

import java.util.List;

import engine.Board;
import engine.KalahRules;
import engine.MoveResult;
import engine.SearchEngine;
import engine.SearchResult;
import engine.Evaluator;

public class CommandProcessor {
    private int searchDepth = 8;
    private Board board = new Board();
    private SearchResult lastSearch;

private void printPV(Board board, List<Integer> pv) {

    if (pv == null || pv.isEmpty()) {
        return;
    }

    System.out.println();
    System.out.println("Expected Line:");

    boolean myTurn = true;
    Board current = board.copy();

    for (int move : pv) {

        if (myTurn) {
            System.out.println("You -> pit " + move);

        } else {

            int oppPit = 12 - move;

            System.out.println("Opp -> pit " + oppPit);
        }

        MoveResult result = KalahRules.applyMove(current, move, myTurn);

        current = result.board();

        if(!result.extraTurn()) {
            myTurn = !myTurn;
        }
    }

    System.out.println();
}

    public void execute(String line) {

        String[] parts = line.trim().split("\\s+");

        if (parts.length == 0)
            return;

        try {

            switch (parts[0]) {

                case "new" -> {
                    board = new Board();
                    System.out.println("New game started.");
                }

                case "board" -> board.print();

                case "my" -> {

                    int pit = Integer.parseInt(parts[1]);

                    MoveResult result = KalahRules.applyMove(board, pit, true);
                    board = result.board();
                    System.out.println("Your move applied.");

                    if (result.extraTurn()) {
                        System.out.println("You earned an extra turn.");
                    }
                }

                case "opp" -> {

                    int pit = Integer.parseInt(parts[1]);
                    int internal = 12 - pit;
                    MoveResult result = KalahRules.applyMove(board, internal, false);
                    board = result.board();

                    System.out.println("Opponent move applied.");

                    if (result.extraTurn()) {
                        System.out.println("Opponent earned an extra turn.");
                    }
                }

                case "stores" -> {

                    int[] p = board.pits();

                    System.out.println("Your Store: " + p[6]);

                    System.out.println("Opponent Store: " + p[13]);
                }

                case "eval" -> {

                    int score = Evaluator.evaluate(board);

                    System.out.println("Evaluation = " + score);
                }

                case "best" -> {

                    System.out.println("Thinking...");
                    SearchEngine.DEBUG = false;
                    lastSearch = SearchEngine.iterativeDeepening(
                            board,
                            searchDepth);
                    SearchResult result = lastSearch;

                    if(SearchEngine.wasBookMoveUsed()) {
                        System.out.println("Book Move Used");
                    }
                    
                    System.out.println("Best move: " + result.move());

                    System.out.println("Score: " + result.score());

                    printPV(board,result.line());

                    System.out.println("Nodes: " + SearchEngine.getNodes());

                    System.out.println("TT Hits: " + SearchEngine.getTTHits());
                }

                case "benchmark" -> {

                    SearchEngine.DEBUG = false;

                    long start =
                            System.nanoTime();

                    lastSearch =
                            SearchEngine.iterativeDeepening(
                                    board,
                                    searchDepth
                            );

                    long elapsed =
                            System.nanoTime() - start;

                    SearchResult result = lastSearch;

                    System.out.println("Benchmark");

                    if(SearchEngine.wasBookMoveUsed()) {
                        System.out.println(
                                "Book Move Used"
                        );
                    }

                    System.out.println(
                            "Depth: " + searchDepth
                    );

                    System.out.println(
                            "Best Move: " + result.move()
                    );

                    System.out.println(
                            "Score: " + result.score()
                    );

                    System.out.println(
                            "Nodes: " + SearchEngine.getNodes()
                    );

                    System.out.println(
                            "TT Hits: " + SearchEngine.getTTHits()
                    );

                    System.out.println(
                            "Time: " + elapsed / 1_000_000 + " ms"
                    );

                    printPV(
                            board,
                            result.line()
                    );
                }

                case "playbest" -> {

                if(lastSearch == null) {
                SearchEngine.DEBUG = false;
                        lastSearch =
                SearchEngine.iterativeDeepening(board, searchDepth);
    }

    MoveResult moveResult =
        KalahRules.applyMove(
            board,
            lastSearch.move(),
            true
        );

    board = moveResult.board();

    System.out.println(
        "Played move "
        + lastSearch.move()
    );

    lastSearch = null;
}

                case "depth" -> {

                    searchDepth = Integer.parseInt(parts[1]);

                    System.out.println(
                            "Search depth set to "
                                    + searchDepth);
                }

                case "endgame" -> {

                    int threshold =
                            Integer.parseInt(parts[1]);

                    SearchEngine.setEndgameThreshold(
                            threshold
                    );

                    System.out.println(
                            "Endgame threshold set to "
                                    + threshold
                    );
                }

                case "settings" -> {

                    System.out.println(
                            "Search Depth: " + searchDepth
                    );

                    System.out.println(
                            "Endgame Threshold: "
                                    + SearchEngine.getEndgameThreshold()
                    );
                }

                default ->

                    System.out.println(
                            "Commands:\n" +
                                    "new\n" +
                                    "board\n" +
                                    "my <pit>\n" +
                                    "opp <pit>\n" +
                                    "stores\n" +
                                    "benchmark\n" +
                                    "endgame <threshold>\n" +
                                    "settings\n" +
                                    "quit");
            }

        } catch (Exception e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }
}
