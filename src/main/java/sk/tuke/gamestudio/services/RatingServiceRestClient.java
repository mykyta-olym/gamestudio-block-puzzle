package sk.tuke.gamestudio.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Rating;

@Service
public class RatingServiceRestClient implements RatingService {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/api/rating";

    @Autowired
    public RatingServiceRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void setRating(Rating rating) {
        restTemplate.postForObject(BASE_URL, rating, Void.class);
    }

    @Override
    public int getAverageRating(String game) {
        String url = BASE_URL + "/average/" + game;
        Integer result = restTemplate.getForObject(url, Integer.class);
        return (result != null) ? result : 0;
    }

    @Override
    public int getRating(String game, String player) {
        String url = BASE_URL + "/" + game + "/" + player;
        return restTemplate.getForObject(url, Integer.class);
    }

    @Override
    public void reset() {
        String url = BASE_URL + "/reset";
        restTemplate.postForObject(url, null, Void.class);
    }
}
