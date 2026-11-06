package sk.tuke.gamestudio.game.BlockPuzzle.ui;

import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.entity.Player;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.services.ScoreException;
import sk.tuke.gamestudio.services.ScoreService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Scanner;
@Service
public class ScoreUI {
    private final ScoreService scoreService;
    private final Scanner scanner;

    @PersistenceContext
    private EntityManager entityManager;

    public ScoreUI(ScoreService scoreService) {
        this.scoreService = scoreService;
        this.scanner = new Scanner(System.in);
    }

    public void showLeaderboardSelection() {
        String response = "";
        while (!"yes".equals(response) && !"no".equals(response)) {
            System.out.println("Would you like to view a leaderboard before starting the game? (yes/no)");
            response = scanner.nextLine().trim().toLowerCase();
            if ("yes".equals(response)) {
                showLeaderboardMenu();
            } else if ("no".equals(response)) {
                System.out.println("Proceeding to the game...");
            } else {
                System.out.println("Invalid response. Please answer 'yes' or 'no'.");
            }
        }
    }

    public void showLeaderboardMenu() {
        boolean continueViewing = true;

        while (continueViewing) {
            System.out.println("\033[1;34mChoose the leaderboard you want to view:\033[0m"); // Blue color
            System.out.println("\033[0;33m1.\033[0m 🪙 Top Players by Coins");
            System.out.println("\033[0;33m2.\033[0m ⏱ Top Players by Total Time Played");
            System.out.println("\033[0;33m3.\033[0m 🌟 Top Players by Total Experience");
            System.out.println("\033[0;33m4.\033[0m 🏆 Top Players by Completed Games");
            System.out.println("\033[0;32m5.\033[0m 🔙 Continue");
            System.out.print("\033[1;35mEnter your choice (1-5):\033[0m ");

            String choice = scanner.nextLine();
            try {
                List<Score> scores; // This will be filled based on the choice
                switch (choice) {
                    case "1":
                        scores = scoreService.fetchTopScores("coins"); // This is a hypothetical method
                        displayTopScores(scores, "Coins");
                        break;
                    case "2":
                        scores = scoreService.fetchTopScores("total_time_played");
                        displayTopScores(scores, "Total Time Played");
                        break;
                    case "3":
                        scores = scoreService.fetchTopScores("total_experience");
                        displayTopScores(scores, "Total Experience");
                        break;
                    case "4":
                        scores = scoreService.fetchTopScores("completed_games");
                        displayTopScores(scores, "Completed Games");
                        break;
                    case "5":
                        continueViewing = false; // User chooses to exit
                        System.out.println("Proceeding to the game...");
                        break;
                    default:
                        System.out.println("\033[1;31mInvalid choice. Please enter a number between 1 and 5.\033[0m");
                        break;
                }
            } catch (ScoreException e) {
                System.out.println("\033[1;31mError displaying leaderboard: " + e.getMessage() + "\033[0m");
            }

            boolean validResponse = false;
            while (continueViewing && !validResponse) {
                System.out.println("\033[1;36mWould you like to view another leaderboard? (yes/no)\033[0m");
                String response = scanner.nextLine().trim().toLowerCase();

                switch (response) {
                    case "yes":
                        validResponse = true; // A valid response; will prompt the user again for which leaderboard to view
                        break;
                    case "no":
                        continueViewing = false; // Exit the loop if the user does not want to view another leaderboard
                        System.out.println("Proceeding to the game...");
                        validResponse = true; // Also a valid response, but exits the viewing loop
                        break;
                    default:
                        System.out.println("\033[1;31mInvalid response. Please answer 'yes' or 'no'.\033[0m"); // Prompt again due to invalid input
                        break;
                }
            }
        }
    }

    public void displayPlayerStats(String username) {
        try {
            Player player = entityManager.createQuery("SELECT p FROM Player p WHERE p.username = :username", Player.class)
                    .setParameter("username", username)
                    .getSingleResult();

            System.out.println("\033[1;36mPlayer:\033[0m " + player.getUsername());
            System.out.println("\033[1;33m🎮 Games Played:\033[0m " + player.getGamesPlayed());
            System.out.println("\033[1;33m⏳ Total Time Played:\033[0m " + player.getTotalTimePlayed() + " seconds");
            System.out.println("\033[1;33m⭐ Total Experience:\033[0m " + player.getExperience());
            System.out.println("\033[1;33m💰 Coins:\033[0m " + player.getCoins());
            System.out.println("\033[1;33m💡 Hints:\033[0m " + player.getHints());
            System.out.println("\033[1;33m🏅 Completed Games:\033[0m " + player.getCompletedGames());
        } catch (Exception e) {
            System.out.println("\033[1;31mError fetching player stats.\033[0m");
            e.printStackTrace();
        }
    }
    public void displayTopScores(List<Score> scores, String displayName) {
        System.out.println("\033[1;36m" + "Top Players by " + displayName + ":" + "\033[0m");
        int rank = 1;
        for (Score score : scores) {
            int displayValue;
            switch (displayName) {
                case "Coins":
                    displayValue = score.getCoins();
                    break;
                case "Total Time Played":
                    displayValue = (int) score.getPlayTime();
                    break;
                case "Total Experience":
                    displayValue = score.getExperience();
                    break;
                case "Completed Games":
                    displayValue = score.getGameCompleted();  // Отримання кількості завершених ігор напряму
                    break;
                default:
                    displayValue = 0;  // За замовчуванням, якщо категорія не визначена
            }
            System.out.printf("\033[1;33m%d. %s: %d\n\033[0m", rank++, score.getUsername(), displayValue);
        }
    }

}
