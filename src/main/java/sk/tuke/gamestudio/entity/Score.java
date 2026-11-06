package sk.tuke.gamestudio.entity;

import javax.persistence.*;

@Entity
@Table(name = "players")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "games_played")
    private long playTime;

    @Column(name = "total_experience")
    private int experience;

    @Column(name = "hints")
    private int hints;

    @Column(name = "coins")
    private int coins;

    @Column(name = "completed_games")
    private int gameCompleted;

    public Score() {}

    public Score(String username, long playTime, int experience, int hints, int coins, int gameCompleted) {
        this.username = username;
        this.playTime = playTime;
        this.experience = experience;
        this.hints = hints;
        this.coins = coins;
        this.gameCompleted = gameCompleted;
    }

    // Getters

    public String getUsername() {
        return username;
    }

    public long getPlayTime() {
        return playTime;
    }

    public int getExperience() {
        return experience;
    }

    public int getHints() {
        return hints;
    }

    public int getCoins() {
        return coins;
    }
    public int getGameCompleted() {
        return gameCompleted;
    }
    // Setters
    public boolean isGameCompleted() {
        return gameCompleted > 0; // Returns true if there is at least one completed game
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setHints(int hints) {
        this.hints = hints;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setGameCompleted(int gameCompleted) {
        this.gameCompleted = gameCompleted;
    }

}
