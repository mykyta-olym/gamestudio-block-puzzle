package sk.tuke.gamestudio.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.entity.Score;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
@Service
public class ScoreServiceJPA implements ScoreService {

    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(ScoreServiceJPA.class);

    @Override
    @Transactional
    public List<Score> getTopScores(String columnName) throws ScoreException {
        logger.info("Fetching top scores for column: {}", columnName);
        try {
            String queryString = String.format("SELECT s FROM Score s ORDER BY s.%s DESC", columnName);
            logger.info("Executing query: {}", queryString);
            List<Score> scores = entityManager.createQuery(queryString, Score.class)
                .setMaxResults(10)
                .getResultList();
            logger.info("Top scores fetched successfully for column: {}", columnName);
            return scores;
        } catch (Exception e) {
            logger.error("Error fetching leaderboard data for column: {}", columnName, e);
            throw new ScoreException("Error fetching leaderboard data.", e);
        }
    }


    @Override
    @Transactional
    public void addScore(Score score) throws ScoreException {
        try {
            List<Score> existingScores = entityManager.createQuery(
                    "SELECT s FROM Score s WHERE s.username = :username", Score.class)
                .setParameter("username", score.getUsername())
                .getResultList();

            if (!existingScores.isEmpty()) {
                Score existingScore = existingScores.get(0);
                existingScore.setPlayTime(existingScore.getPlayTime() + score.getPlayTime());
                existingScore.setExperience(existingScore.getExperience() + score.getExperience());
                existingScore.setGameCompleted(existingScore.getGameCompleted() + (score.isGameCompleted() ? 1 : 0));
                entityManager.merge(existingScore);
                entityManager.flush();
                logger.info("Updated score for username: {}", score.getUsername());
            } else {
                entityManager.persist(score);
                logger.info("Persisted new score for username: {}", score.getUsername());
            }
        } catch (Exception e) {
            logger.error("Failed to update or persist score for username: {}", score.getUsername(), e);
            throw new ScoreException("Error updating or persisting score.", e);
        }
    }
    @Override
    @Transactional
    public void reset() throws ScoreException {
        logger.info("Resetting all player scores");
        try {
            entityManager.createQuery("DELETE FROM Score").executeUpdate();
            logger.info("All player scores reset successfully");
        } catch (Exception e) {
            logger.error("Error resetting player scores", e);
            throw new ScoreException("Error resetting player scores.", e);
        }
    }

    @Override
    @Transactional
    public List<Score> fetchTopScores(String criterion) throws ScoreException {
        logger.info("Fetching top scores by {}", criterion);

        String field;
        switch (criterion) {
            case "total_time_played":
                field = "playTime";
                break;
            case "total_experience":
                field = "experience";
                break;
            case "coins":
                field = "coins";
                break;
            case "completed_games":
                field = "gameCompleted";
                break;
            default:
                logger.error("Invalid criterion: {}", criterion);
                throw new ScoreException("Invalid criterion: " + criterion);
        }

        String queryString = String.format("SELECT s FROM Score s ORDER BY s.%s DESC", field);
        List<Score> scores = entityManager.createQuery(queryString, Score.class)
            .setMaxResults(10)
            .getResultList();

        logger.info("Successfully fetched top scores for criterion: {}", criterion);
        return scores;
    }
}
