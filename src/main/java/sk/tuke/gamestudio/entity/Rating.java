package sk.tuke.gamestudio.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "game_ratings")
public class Rating {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "game_mode")
    private String game;

    @Column(name = "rating")
    private int rating;

    // Default constructor is required by JPA
    public Rating() {}

    public Rating(String username, String game, int rating) {
        this.username = username;
        this.game = game;
        this.rating = rating;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
