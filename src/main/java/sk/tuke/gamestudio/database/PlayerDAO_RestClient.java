package sk.tuke.gamestudio.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service

public class PlayerDAO_RestClient implements PlayerDAO {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(PlayerDAO_RestClient.class);
    private static final String BASE_URL = "http://localhost:8080/api/player";

    @Autowired
    public PlayerDAO_RestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Override
    public boolean useCoinsForHint(String username, int numberOfHints) {
        try {
            String url = BASE_URL + "/useCoinsForHint/" + username + "/" + numberOfHints;
            return restTemplate.postForObject(url, null, Boolean.class);
        } catch (Exception e) {
            logger.error("Error in useCoinsForHint for user: {}", username, e);
            return false;
        }
    }
    @Override
    public boolean useExperienceForHint(String username, int numberOfHints) {
        try {
            String url = BASE_URL + "/useExperienceForHint/" + username + "/" + numberOfHints;
            return restTemplate.postForObject(url, null, Boolean.class);
        } catch (Exception e) {
            logger.error("Error in useExperienceForHint for user: {}", username, e);
            return false;
        }
    }
    @Override
    public int getHintsForPlayer(String username) {
        try {
            String url = BASE_URL + "/getHintsForPlayer/" + username;
            return restTemplate.getForObject(url, Integer.class);
        } catch (Exception e) {
            logger.error("Error in getHintsForPlayer for user: {}", username, e);
            return 0;
        }
    }
    @Override
    public void startNewGame(String username) {
        try {
            String url = BASE_URL + "/startNewGame/" + username;
            restTemplate.postForObject(url, null, Void.class);
        } catch (Exception e) {
            logger.error("Error starting new game for user: {}", username, e);
        }
    }
    @Override
    public void updateHints(String username, int hints) {
        try {
            String url = BASE_URL + "/updateHints/" + username + "/" + hints;
            restTemplate.postForObject(url, null, Void.class);
        } catch (Exception e) {
            logger.error("Error updating hints for user: {}", username, e);
        }
    }
}
