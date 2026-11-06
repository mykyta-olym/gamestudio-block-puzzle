package sk.tuke.gamestudio.services;

public class ScoreException extends RuntimeException {
    // Constructor with message and cause
    public ScoreException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with only message
    public ScoreException(String message) {
        super(message);
    }
}
