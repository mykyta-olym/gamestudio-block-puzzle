package sk.tuke.gamestudio.database;

public interface PlayerDAO {
    boolean useCoinsForHint(String username, int numberOfHints);
    boolean useExperienceForHint(String username, int numberOfHints);
    int getHintsForPlayer(String username);
    void startNewGame(String username);
    void updateHints(String username, int hints);

}
