package sk.tuke.gamestudio.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class RatingServiceJPA implements RatingService {

    private static final Logger logger = LoggerFactory.getLogger(RatingServiceJPA.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) {
        logger.info("Setting rating for game: {} by user: {}", rating.getGame(), rating.getUsername());
        try {
            Rating existingRating = entityManager.find(Rating.class, rating.getUsername());
            if (existingRating != null) {
                existingRating.setRating(rating.getRating());
                entityManager.merge(existingRating);
                logger.info("Updated existing rating for user: {}", rating.getUsername());
            } else {
                entityManager.persist(rating);
                logger.info("Persisted new rating for user: {}", rating.getUsername());
            }
        } catch (Exception e) {
            logger.error("Error setting rating for game: {}", rating.getGame(), e);
        }
    }

    @Override
    public int getAverageRating(String game) {
        logger.info("Calculating average rating for game: {}", game);
        try {
            List<Rating> ratings = entityManager.createQuery(
                    "SELECT r FROM Rating r WHERE r.game = :game", Rating.class)
                .setParameter("game", game)
                .getResultList();
            int averageRating = (int) ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0);
            logger.info("Average rating calculated successfully for game: {}", game);
            return averageRating;
        } catch (Exception e) {
            logger.error("Error calculating average rating for game: {}", game, e);
            return 0;
        }
    }

    @Override
    public int getRating(String game, String player) {
        logger.info("Retrieving rating for game: {} by user: {}", game, player);
        try {
            Rating rating = entityManager.createQuery(
                    "SELECT r FROM Rating r WHERE r.game = :game AND r.username = :username", Rating.class)
                .setParameter("game", game)
                .setParameter("username", player)
                .getResultStream()
                .findFirst()
                .orElse(null);
            if (rating != null) {
                logger.info("Rating retrieved successfully for user: {}", player);
                return rating.getRating();
            } else {
                logger.info("No rating found for user: {}", player);
                return 0;
            }
        } catch (Exception e) {
            logger.error("Error retrieving rating for game: {}", game, e);
            return 0;
        }
    }

    @Override
    public void reset() {
        logger.info("Resetting all ratings");
        try {
            entityManager.createQuery("DELETE FROM Rating").executeUpdate();
            logger.info("All ratings reset successfully");
        } catch (Exception e) {
            logger.error("Error resetting ratings", e);
        }
    }
}
