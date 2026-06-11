package engine;

import java.util.List;

public record SearchResult(
        int move,
        int score,
        List<Integer> line
) {
}