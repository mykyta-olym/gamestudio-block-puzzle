package sk.tuke.gamestudio.services;

import sk.tuke.gamestudio.entity.Comment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService.reset();
    }

    @AfterEach
    void tearDown() {
        commentService.reset(); // Clean up after tests
    }

    @Test
    void addAndGetComments() {
        Comment comment1 = new Comment("testUser1", "testGame", "Great game!", new Date());
        Comment comment2 = new Comment("testUser2", "testGame", "Needs improvement.", new Date());

        commentService.addComment(comment1);
        commentService.addComment(comment2);

        List<Comment> comments = commentService.getComments("testGame");
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("Great game!", comments.get(0).getComment());
        assertEquals("Needs improvement.", comments.get(1).getComment());
    }

    @Test
    void resetComments() {
        Comment comment = new Comment("testUser", "testGame", "Nice!", new Date());
        commentService.addComment(comment);

        commentService.reset();
        List<Comment> comments = commentService.getComments("testGame");
        assertTrue(comments.isEmpty());
    }

    @Test
    void getCommentsForGameWithNoCommentsReturnsEmptyList() {
        // Setup: Assuming "newGame" is a game mode with no comments added yet
        String gameModeWithNoComments = "newGame";

        // Act: Attempt to retrieve comments for "newGame"
        List<Comment> comments = commentService.getComments(gameModeWithNoComments);

        // Assert: Verify that the retrieved list is empty
        assertTrue(comments.isEmpty(), "Expected an empty list of comments for a game with no comments.");
    }
}
