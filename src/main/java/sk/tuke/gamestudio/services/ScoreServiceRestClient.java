package sk.tuke.gamestudio.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import sk.tuke.gamestudio.entity.Score;

import java.util.List;

@Service
public class ScoreServiceRestClient implements ScoreService {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/api/scores";

    @Autowired
    public ScoreServiceRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Score> getTopScores(String columnName) {
        try {
            String url = BASE_URL + "/" + columnName;
            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Score>>() {}).getBody();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error calling external API for getTopScores", e);
        }
    }

    @Override
    public void addScore(Score score) {
        try {
            String url = BASE_URL + "/add";
            restTemplate.postForObject(url, score, Void.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error calling external API for addScore", e);
        }
    }

    @Override
    public List<Score> fetchTopScores(String criterion) {
        try {
            String url = "http://localhost:8080/api/scores/fetch/" + criterion;
            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Score>>() {}).getBody();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error calling external API for fetchTopScores", e);
        }
    }
    @Override
    public void reset() {
        try {
            String url = BASE_URL + "/reset";
            restTemplate.postForObject(url, null, Void.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error calling external API for reset", e);
        }
    }
}
