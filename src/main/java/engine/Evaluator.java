package engine;

public class Evaluator {

    public static int evaluate(Board board) {

        int[] p = board.pits();

        int score = 0;

        score += 20 * (p[6] - p[13]);

        int mySide = 0;
        int oppSide = 0;

        for(int i=0;i<6;i++) {
            mySide += p[i];

            if(p[i] == 6 - i)
                score += 15;
        }

        for(int i=7;i<13;i++) {
            oppSide += p[i];

            if(p[i] == 13 - i)
                score -= 15;
        }

        score += (mySide - oppSide);

        for(int i=0;i<6;i++) {

            if(p[i] == 0) {

                int opposite = 12 - i;

                if(p[opposite] > 0)
                    score += 5;
            }
        }

        return score;
    }
}