package sk.tuke.gamestudio.game.BlockPuzzle.ui;

import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.services.CommentException;
import sk.tuke.gamestudio.services.CommentService;
import sk.tuke.gamestudio.services.RatingException;
import sk.tuke.gamestudio.services.RatingService;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class CommentAndRatingUI {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    private final CommentService commentService;
    private final RatingService ratingService;
    private final Scanner scanner = new Scanner(System.in);
    private final String username;
    private final String gameMode;

    // ANSI color codes for styling the console output
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public CommentAndRatingUI(CommentService commentService, RatingService ratingService, String username, String gameMode) {
        this.commentService = commentService;
        this.ratingService = ratingService;
        this.username = username;
        this.gameMode = gameMode;
    }

    public void handleCommentsAndRatings() {
        System.out.println(ANSI_CYAN + "=======================================");
        System.out.println("         Comments and Ratings          ");
        System.out.println("=======================================" + ANSI_RESET);

        System.out.println(ANSI_YELLOW + "Would you like to rate the game, leave a comment, or both? (rate/comment/both/none)" + ANSI_RESET);
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.isEmpty()) {
            System.out.println("Please enter a valid option.");
            handleCommentsAndRatings();
            return;
        }

        switch (response) {
            case "rate":
                promptForRating();
                break;
            case "comment":
                promptForComment();
                break;
            case "both":
                promptForRating();
                promptForComment();
                break;
            case "none":
                System.out.println("No action taken.");
                break;
            default:
                System.out.println("Invalid option. Please choose again.");
                handleCommentsAndRatings();
                break;
        }

        displayAndBrowseComments();
        displayRatings();
    }

    private void promptForComment() {
        System.out.println(ANSI_PURPLE + "Please enter your comment (or leave blank to cancel):" + ANSI_RESET);
        String commentText = scanner.nextLine().trim();
        if (commentText.isEmpty()) {
            System.out.println("Comment submission cancelled.");
            return;
        }

        Comment comment = new Comment(username, gameMode, commentText, new Date());
        try {
            commentService.addComment(comment);
            System.out.println("Your comment has been saved successfully.");
        } catch (CommentException e) {
            System.out.println("Error saving your comment: " + e.getMessage());
        }
    }

    private void promptForRating() {
        System.out.println(ANSI_PURPLE + "Please rate the game on a scale of 1 to 5 (or enter 0 to cancel):" + ANSI_RESET);
        int ratingValue;
        try {
            ratingValue = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            promptForRating();
            return;
        }

        if (ratingValue == 0) {
            System.out.println("Rating submission cancelled.");
            return;
        } else if (ratingValue < 1 || ratingValue > 5) {
            System.out.println("Invalid rating. Please enter a number between 1 and 5.");
            promptForRating();
            return;
        }

        Rating rating = new Rating(username, gameMode, ratingValue);
        try {
            ratingService.setRating(rating);
            System.out.println("Your rating has been saved successfully.");
        } catch (RatingException e) {
            System.out.println("Error saving your rating: " + e.getMessage());
        }
    }


    private void displayAndBrowseComments() {
        System.out.println(ANSI_CYAN + "Recent comments for " + gameMode + ":" + ANSI_RESET);
        try {
            List<Comment> comments = commentService.getComments(gameMode);
            if (comments.isEmpty()) {
                System.out.println("There are no comments to display.");
                return;
            }

            System.out.println(ANSI_PURPLE + "Would you like to browse comments? (yes/no)" + ANSI_RESET);
            String browseResponse = scanner.nextLine().trim().toLowerCase();

            if (!"yes".equals(browseResponse)) {
                // Display all comments without browsing
                for (Comment comment : comments) {
                    System.out.printf(ANSI_YELLOW + "[%s] %s: %s" + ANSI_RESET + "\n",
                        comment.getCreatedOn(), comment.getUsername(), comment.getComment());
                }
                return;
            }

            // Interactive browsing
            int index = 0;
            while (true) {
                Comment comment = comments.get(index);
                System.out.printf(ANSI_YELLOW + "[%s] %s: %s" + ANSI_RESET + "\n",
                    comment.getCreatedOn(), comment.getUsername(), comment.getComment());

                System.out.println("(n)ext, (p)revious, (q)uit");
                String input = scanner.nextLine().trim().toLowerCase();

                switch (input) {
                    case "n":
                        index = Math.min(comments.size() - 1, index + 1);
                        break;
                    case "p":
                        index = Math.max(0, index - 1);
                        break;
                    case "q":
                        return;
                    default:
                        System.out.println("Invalid input.");
                        break;
                }
            }
        } catch (CommentException e) {
            System.out.println("Error retrieving comments: " + e.getMessage());
        }
    }


    private void displayRatings() {
        try {
            int averageRating = ratingService.getAverageRating(gameMode);
            System.out.println(ANSI_CYAN + "Average rating for " + gameMode + ": " + generateStars(averageRating) + ANSI_RESET);
        } catch (RatingException e) {
            System.out.println("Error retrieving ratings: " + e.getMessage());
        }
    }



    private String generateStars(int rating) {
        final String FILLED_SYMBOL = "●"; // Solid circle for filled rating
        final String UNFILLED_SYMBOL = "○"; // Outlined circle for unfilled rating
        final String HIGH_RATING_COLOR = ANSI_GREEN;
        final String MEDIUM_RATING_COLOR = ANSI_YELLOW;
        final String LOW_RATING_COLOR = ANSI_RED;

        StringBuilder stars = new StringBuilder();

        // Apply color coding based on rating
        if (rating >= 4) {
            stars.append(HIGH_RATING_COLOR);
        } else if (rating >= 2) {
            stars.append(MEDIUM_RATING_COLOR);
        } else {
            stars.append(LOW_RATING_COLOR);
        }

        for (int i = 0; i < rating; i++) {
            stars.append(FILLED_SYMBOL);
        }
        for (int i = rating; i < 5; i++) {
            stars.append(UNFILLED_SYMBOL);
        }

        stars.append(ANSI_RESET); // Reset the color to default after displaying the rating
        return stars.toString();
    }


}
