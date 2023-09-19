package blackjack;

public class Player {
    private int score;
    private String name;
    private int winCount;

    public Player(String name) {
        this.name = name;
    }

    public void resetScore() {
        this.score = 0;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public String getName() {
        return name;
    }

    public void incrementWinCount() {
        this.winCount++;
    }

    public int getWinCount() {
        return winCount;
    }

    public double getWinPercentage(int totalGames) {
        return ((double) winCount / totalGames) * 100;
    }
}
