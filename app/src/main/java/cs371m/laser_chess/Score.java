package cs371m.laser_chess;

/**
 * Created by Abhi on 11/30/2016.
 */

public class Score {

    private String name;
    private int wins;
    private int loses;

    public Score (String name, String scores) {
        this.name = name;
        String[] splitScores = scores.split(",");
        wins = Integer.parseInt(splitScores[0]);
        loses = Integer.parseInt(splitScores[1]);
    }

    public int getWins() {
        return wins;
    }

    public int getLoses() {
        return loses;
    }

    public String getName() {
        return name;
    }

}
