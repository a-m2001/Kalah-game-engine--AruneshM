package cli;

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

    public void execute(String line) {

        String[] parts = line.trim().split("\\s+");

        if (parts.length == 0)
            return;

        try {

            switch (parts[0]) {

                case "new" -> {

                    board = new Board();

                    System.out.println(
                            "New game started.");
                }

                case "board" -> board.print();

                case "my" -> {

                    int pit = Integer.parseInt(parts[1]);

                    MoveResult result = KalahRules.applyMove(
                            board,
                            pit,
                            true);

                    board = result.board();

                    System.out.println(
                            "Your move applied.");

                    if (result.extraTurn()) {
                        System.out.println(
                                "You earned an extra turn.");
                    }
                }

                case "opp" -> {

                    int pit = Integer.parseInt(parts[1]);

                    int internal = 12 - pit;

                    MoveResult result = KalahRules.applyMove(
                            board,
                            internal,
                            false);

                    board = result.board();

                    System.out.println(
                            "Opponent move applied.");

                    if (result.extraTurn()) {
                        System.out.println(
                                "Opponent earned an extra turn.");
                    }
                }

                case "stores" -> {

                    int[] p = board.pits();

                    System.out.println(
                            "Your Store: " + p[6]);

                    System.out.println(
                            "Opponent Store: " + p[13]);
                }

                case "eval" -> {

                    int score = Evaluator.evaluate(board);

                    System.out.println(
                            "Evaluation = " + score);
                }

                case "best" -> {

                    System.out.println(
                            "Thinking...");
                    SearchEngine.DEBUG = false;
                    lastSearch = SearchEngine.iterativeDeepening(
                            board,
                            searchDepth);
                    SearchResult result = lastSearch;
                    
                    System.out.println(
                            "Best move: "
                                    + result.move());

                    System.out.println(
                            "Score: "
                                    + result.score());

                        System.out.println(
        "Nodes: "
        + SearchEngine.getNodes()
);
                }

case "playbest" -> {

    if(lastSearch == null) {
        SearchEngine.DEBUG = false;
        lastSearch =
            SearchEngine.iterativeDeepening(
                board,
                searchDepth
            );
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

                default ->

                    System.out.println(
                            "Commands:\n" +
                                    "new\n" +
                                    "board\n" +
                                    "my <pit>\n" +
                                    "opp <pit>\n" +
                                    "stores\n" +
                                    "quit");
            }

        } catch (Exception e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }
}