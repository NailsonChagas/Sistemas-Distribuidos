package models;

public class Score {
    private int wins;
    private int losses;

    public Score() {
        this.wins = 0;
        this.losses = 0;
    }

    public void addWin() {
        wins++;
    }

    public void addLoss() {
        losses++;
    }

    @Override
    public String toString() {
        return "Vitórias: " + wins + " | Derrotas: " + losses;
    }
}