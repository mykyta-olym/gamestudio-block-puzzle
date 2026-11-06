package sk.tuke.gamestudio.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.gamestudio.entity.GameSession;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class GameSessionServiceJPA implements GameSessionService {

    private static final Logger logger = LoggerFactory.getLogger(GameSessionServiceJPA.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveGame(GameSession gameSession) {
        try {
            entityManager.persist(gameSession);
            logger.info("Game saved successfully for user: {}", gameSession.getUsername());
        } catch (Exception e) {
            logger.error("Failed to save the game for user: {}", gameSession.getUsername(), e);
        }
    }

    @Override
    @Transactional
    public GameSession loadGame(String username) {
        try {
            GameSession gameSession = entityManager.createQuery(
                    "SELECT gs FROM GameSession gs WHERE gs.username = :username ORDER BY gs.sessionId DESC",
                    GameSession.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getSingleResult();

            logger.info("Game loaded successfully for user: {}", username);
            return gameSession;
        } catch (Exception e) {
            logger.error("Failed to load the game for user: {}", username, e);
            return null;
        }
    }

}
