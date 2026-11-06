package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.services.ScoreException;
import sk.tuke.gamestudio.services.ScoreService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/scores")
public class ScoreServiceRest {

    private final ScoreService scoreService;

    @Autowired
    public ScoreServiceRest(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping("/{columnName}")
    public List<Score> getTopScores(@PathVariable String columnName) {
        if (columnName == null || columnName.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Column name cannot be empty");
        }

        try {
            return scoreService.getTopScores(columnName);
        } catch (ScoreException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching top scores", e);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addScore(@RequestBody Score score) {
        try {
            scoreService.addScore(score);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Score updated successfully");
            return ResponseEntity.ok(response);
        } catch (ScoreException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/fetch/{criterion}")
    public List<Score> fetchTopScores(@PathVariable String criterion) {
        if (criterion == null || criterion.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Criterion cannot be empty");
        }

        try {
            return scoreService.fetchTopScores(criterion);
        } catch (ScoreException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching top scores by criterion", e);
        }
    }

    @PostMapping("/reset")
    public void reset() {
        try {
            scoreService.reset();
        } catch (ScoreException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error resetting scores", e);
        }
    }
}
