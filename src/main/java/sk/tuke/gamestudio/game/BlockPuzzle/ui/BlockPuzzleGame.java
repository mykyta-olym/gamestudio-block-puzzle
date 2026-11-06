package sk.tuke.gamestudio.game.BlockPuzzle.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.tuke.gamestudio.database.DatabaseException;
import sk.tuke.gamestudio.game.BlockPuzzle.core.GameModes.ClassicMode;
import sk.tuke.gamestudio.game.BlockPuzzle.core.GameModes.EndlessMode;

import java.util.Scanner;
@Component
public class BlockPuzzleGame {
    @Autowired
    private ClassicMode classicMode;
    @Autowired
    private EndlessMode endlessMode;
    public void start() throws InterruptedException, DatabaseException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("🎮 Welcome to Block Puzzle Game! 🎮");

        boolean askingForHelp = true;
        while (askingForHelp) {
            System.out.println("Would you like to view the game help before starting? (yes/no)");
            String helpResponse = scanner.nextLine().trim().toLowerCase();

            switch (helpResponse) {
                case "yes":
                    GameHelp.showHelp(); // Припускаючи, що метод showHelp() розміщений у класі GameHelp
                    System.out.println("\n"); // Додаємо порожній рядок для кращого відокремлення тексту
                    askingForHelp = false; // Вихід з циклу
                    break;
                case "no":
                    askingForHelp = false; // Вихід з циклу
                    break;
                default:
                    System.out.println("Invalid input. Please type 'yes' or 'no'.");
                    // Цикл продовжується
                    break;
            }
        }

        boolean validSelection = false;
        while (!validSelection) {
            System.out.println("Choose game mode: \n1. Classic Mode \n2. Endless Mode");
            String modeSelection = scanner.nextLine();

            switch (modeSelection) {
                case "1":
                    classicMode.startGame();
                    validSelection = true;
                    break;
                case "2":
                    endlessMode.startGame();
                    validSelection = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}
