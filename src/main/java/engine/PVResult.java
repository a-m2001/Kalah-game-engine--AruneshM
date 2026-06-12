package engine;

import java.util.List;

public record PVResult(
        int score,
        List<Integer> line
) {}