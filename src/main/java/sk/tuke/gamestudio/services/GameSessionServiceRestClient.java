package sk.tuke.gamestudio.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.GameSession;

@Service
public class GameSessionServiceRestClient implements GameSessionService {

    private static final Logger logger = LoggerFactory.getLogger(GameSessionServiceRestClient.class);
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/api/gameSession";

    @Autowired
    public GameSessionServiceRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void saveGame(GameSession gameSession) {
        try {
            String url = BASE_URL + "/save";
            restTemplate.postForObject(url, gameSession, Void.class);
            logger.info("Game saved successfully for user: {}", gameSession.getUsername());
        } catch (Exception e) {
            logger.error("Failed to save the game for user: {}", gameSession.getUsername(), e);
        }
    }

    @Override
    public GameSession loadGame(String username) {
        try {
            String url = BASE_URL + "/load/" + username;
            return restTemplate.getForObject(url, GameSession.class);
        } catch (Exception e) {
            logger.error("Failed to load the game for user: {}", username, e);
            return null;
        }
    }
}
