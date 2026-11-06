package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.entity.GameSession;
import sk.tuke.gamestudio.services.GameSessionService;

@RestController
@RequestMapping("/gameSession")
public class GameSessionServiceRest {

    private final GameSessionService gameSessionService;

    @Autowired
    public GameSessionServiceRest(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @PostMapping("/save")
    public ResponseEntity<Void> saveGame(@RequestBody GameSession gameSession) {
        if (gameSession == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            gameSessionService.saveGame(gameSession);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/load/{username}")
    public ResponseEntity<GameSession> loadGame(@PathVariable String username) {
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            GameSession gameSession = gameSessionService.loadGame(username);
            if (gameSession != null) {
                return ResponseEntity.ok(gameSession);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
