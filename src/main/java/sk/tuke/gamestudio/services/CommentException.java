package sk.tuke.gamestudio.services;

public class CommentException extends RuntimeException {
    // Constructor with message and cause
    public CommentException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with only message
    public CommentException(String message) {
        super(message);
    }
}
