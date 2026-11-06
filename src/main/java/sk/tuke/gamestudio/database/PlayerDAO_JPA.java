package sk.tuke.gamestudio.database;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.entity.Player;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Service
@Transactional
@Primary
public class PlayerDAO_JPA implements PlayerDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(PlayerDAO_JPA.class);
    @Override
    public boolean useCoinsForHint(String username, int numberOfHints) {
        int requiredCoins = 12 * numberOfHints; // 12 coins per hint
        Player player = entityManager.find(Player.class, username);

        if (player != null && player.getCoins() >= requiredCoins) {
            player.setCoins(player.getCoins() - requiredCoins);
            player.setHints(player.getHints() + numberOfHints);
            logger.info("Coins deducted for hints: User={}, Coins={}, Hints={}", username, requiredCoins, numberOfHints);
            return true;
        } else {
            logger.warn("Insufficient coins for hints: User={}, RequiredCoins={}, AvailableCoins={}", username, requiredCoins, player != null ? player.getCoins() : "User not found");
            return false;
        }
    }
    @Override
    public boolean useExperienceForHint(String username, int numberOfHints) {
        int requiredExperience = 500 * numberOfHints; // 500 experience points per hint
        Player player = entityManager.find(Player.class, username);

        if (player != null && player.getExperience() >= requiredExperience) {
            player.setExperience(player.getExperience() - requiredExperience);
            player.setHints(player.getHints() + numberOfHints);
            logger.info("Experience deducted for hints: User={}, Experience={}, Hints={}", username, requiredExperience, numberOfHints);
            return true;
        } else {
            logger.warn("Insufficient experience for hints: User={}, RequiredExperience={}, AvailableExperience={}", username, requiredExperience, player != null ? player.getExperience() : "User not found");
            return false;
        }
    }@Override
    public int getHintsForPlayer(String username) {
        Player player = entityManager.find(Player.class, username);
        if (player != null) {
            return player.getHints();
        } else {
            logger.info("No player found with username: {}", username);
            return 0;  // Return 0 if user not found
        }
    }
    @Override
    public void startNewGame(String username) {
        try {
            Player player = entityManager.find(Player.class, username);
            if (player != null) {
                // Assuming there is a field gamesPlayed in the Player entity
                player.setGamesPlayed(player.getGamesPlayed() + 1);
                entityManager.merge(player);
                logger.info("Incremented games played for user: {}", username);
            } else {
                logger.warn("User not found: {}", username);
            }
        } catch (Exception e) {
            logger.error("Error starting new game for user: " + username, e);
        }
    }
    @Override
    public void updateHints(String username, int hints) {
        try {
            Player player = entityManager.find(Player.class, username);
            if (player != null) {
                player.setHints(hints);
                entityManager.merge(player);
                logger.info("Updated hints for user: {}. New hints: {}", username, hints);
            } else {
                logger.warn("User not found: {}", username);
            }
        } catch (Exception e) {
            logger.error("Error updating hints for user: " + username, e);
        }
    }
}
