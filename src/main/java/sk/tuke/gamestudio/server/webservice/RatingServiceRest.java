package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.services.RatingService;

@RestController
@RequestMapping("/rating")
public class RatingServiceRest implements RatingService {

    private final RatingService ratingService;

    @Autowired
    public RatingServiceRest(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void setRating(@RequestBody Rating rating) {
        if (rating == null || rating.getGame() == null || rating.getGame().trim().isEmpty() || rating.getUsername() == null || rating.getUsername().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating, game name, and username cannot be null or empty");
        }
        try {
            ratingService.setRating(rating);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error setting rating", e);
        }
    }

    @GetMapping("/average/{game}")
    public int getAverageRating(@PathVariable String game) {
        if (game == null || game.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game name cannot be empty");
        }

        try {
            return ratingService.getAverageRating(game);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error calculating average rating", e);
        }
    }

    @GetMapping("/{game}/{player}")
    public int getRating(@PathVariable String game, @PathVariable String player) {
        if (game == null || game.trim().isEmpty() || player == null || player.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game name and player cannot be empty");
        }

        try {
            return ratingService.getRating(game, player);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving rating", e);
        }
    }

    @PostMapping("/reset")
    public void reset() {
        try {
            ratingService.reset();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error resetting ratings", e);
        }
    }
}
