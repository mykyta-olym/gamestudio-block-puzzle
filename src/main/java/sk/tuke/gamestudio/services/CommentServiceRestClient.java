package sk.tuke.gamestudio.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import sk.tuke.gamestudio.entity.Comment;

import java.util.List;

@RestController
@Service
public class CommentServiceRestClient implements CommentService {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/comments";

    @Autowired
    public CommentServiceRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void addComment(Comment comment) {
        try {
            String url = BASE_URL;
            restTemplate.postForObject(url, comment, Void.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error calling external API for addComment", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) {
        try {
            String url = BASE_URL + "/" + game;
            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Comment>>() {}).getBody();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error calling external API for getComments", e);
        }
    }

    @Override
    public void reset() {
        try {
            String url = BASE_URL + "/reset";
            restTemplate.postForObject(url, null, Void.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error calling external API for resetComments", e);
        }
    }
}
