package sk.tuke.gamestudio.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "players")
public class Player {
    @Id
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "coins")
    private int coins;

    @Column(name = "total_experience")
    private int experience;

    @Column(name = "hints")
    private int hints;

    @Column(name = "games_played")
    private int gamesPlayed;  // Added field for tracking the number of games played

    @Column(name = "total_time_played")
    private int totalTimePlayed;

    @Column(name = "completed_games")
    private int completedGames;

    // Default constructor
    public Player() {
    }

    public Player(String username, String passwordHash) {
        this.username = username;
        this.password = passwordHash;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public int getTotalTimePlayed() {
        return totalTimePlayed;
    }
    public int getCompletedGames() {
        return completedGames;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getHints() {
        return hints;
    }

    public void setHints(int hints) {
        this.hints = hints;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
