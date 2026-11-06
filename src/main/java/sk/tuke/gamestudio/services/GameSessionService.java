package sk.tuke.gamestudio.services;

import sk.tuke.gamestudio.entity.GameSession;

public interface GameSessionService {

    void saveGame(GameSession gameSession);

    GameSession loadGame(String username);

}
