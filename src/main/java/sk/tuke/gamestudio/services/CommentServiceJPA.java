package sk.tuke.gamestudio.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.entity.Comment;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CommentServiceJPA implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceJPA.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addComment(Comment comment) throws CommentException {
        if (comment == null || comment.getGame() == null || comment.getComment() == null) {
            throw new CommentException("Comment or game cannot be null");
        }
        logger.info("Received comment with username: {}", comment.getUsername());
        logger.info("Adding comment for game: {}", comment.getGame());
        try {
            entityManager.persist(comment);
            logger.info("Comment added successfully for game: {}", comment.getGame());
        } catch (Exception e) {
            logger.error("Failed to save the comment for game: {}", comment.getGame(), e);
            throw new CommentException("Failed to save the comment.", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        if (game == null || game.trim().isEmpty()) {
            throw new CommentException("Game name cannot be empty");
        }

        logger.info("Retrieving comments for game: {}", game);
        try {
            String jpql = "SELECT c FROM Comment c WHERE c.game = :game ORDER BY c.createdOn DESC";
            TypedQuery<Comment> query = entityManager.createQuery(jpql, Comment.class);
            query.setParameter("game", game);
            List<Comment> comments = query.getResultList();
            logger.info("Comments retrieved successfully for game: {}", game);
            return comments;
        } catch (Exception e) {
            logger.error("Failed to retrieve comments for game: {}", game, e);
            throw new CommentException("Failed to retrieve comments.", e);
        }
    }

    @Override
    public void reset() throws CommentException {
        logger.info("Resetting comments");
        try {
            entityManager.createQuery("DELETE FROM Comment").executeUpdate();
            logger.info("Comments reset successfully");
        } catch (Exception e) {
            logger.error("Failed to reset comments", e);
            throw new CommentException("Failed to reset comments.", e);
        }
    }
}

