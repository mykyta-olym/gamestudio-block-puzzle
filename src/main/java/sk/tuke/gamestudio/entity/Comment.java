package sk.tuke.gamestudio.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "game_comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "username")
    @JsonProperty("player")  // This will map 'player' from JSON to 'username' in the entity
    private String username;


    @Column(name = "comment")
    private String comment;

    @Column(name = "game_mode")
    private String game;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn = new Date();

    public Comment() {}

    public Comment(String username, String game, String comment, Date createdOn) {
        this.username = username;
        this.game = game;
        this.comment = comment;
        this.createdOn = createdOn;
    }


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }

    public String getComment() { return comment; }

    public Date getCreatedOn() { return createdOn; }

    public void setCreatedOn(Date createdOn) { this.createdOn = createdOn; }
}
