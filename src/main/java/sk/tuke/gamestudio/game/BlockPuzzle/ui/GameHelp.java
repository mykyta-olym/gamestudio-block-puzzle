package sk.tuke.gamestudio.game.BlockPuzzle.ui;

public class GameHelp {

    // ANSI color codes for styling text
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    public static void showHelp() {
        System.out.println(ANSI_GREEN + "🎮 Welcome to Block Puzzle Game Help! 🎮\n" + ANSI_RESET);

        System.out.println(ANSI_YELLOW + "Game Modes:" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "1. Classic Mode:" + ANSI_RESET);
        System.out.println("   - Objective: Fill the grid with pieces to clear lines and gain points. 🧩");
        System.out.println("   - Pieces can be rotated and placed in any valid position on the grid. 🔁");
        System.out.println("   - The game ends when no more pieces can be placed. ❌");

        System.out.println(ANSI_CYAN + "2. Endless Mode:" + ANSI_RESET);
        System.out.println("   - Objective: Place pieces to clear lines, with the game progressively getting faster. ⏩");
        System.out.println("   - Pieces are provided in sets of three. Once placed, new pieces are provided. 🔄");
        System.out.println("   - Lines are cleared immediately when filled, and points are awarded based on the number of lines cleared. 💥");
        System.out.println("   - The game ends when there is no space to place any of the available pieces. ❌");

        System.out.println(ANSI_YELLOW + "Game Mechanics:" + ANSI_RESET);
        System.out.println(" - Placing Pieces: Select a piece and a position on the grid. If the piece fits, it will be placed. ✅");
        System.out.println(" - Clearing Lines: When a line is fully filled, it will be cleared from the board, and points will be awarded. 💯");
        System.out.println(" - Game Over: The game ends when there is no space left to place new pieces. 🏁");

        System.out.println(ANSI_YELLOW + "Strategies:" + ANSI_RESET);
        System.out.println(" - Try to keep the grid as clear as possible to have space for placing new pieces. 🧹");
        System.out.println(" - Use hints wisely to get suggestions on where to place pieces for maximum efficiency. 💡");
        System.out.println(" - Plan ahead and anticipate the next pieces to maximize your score. 🤔");

        System.out.println(ANSI_YELLOW + "Tips:" + ANSI_RESET);
        System.out.println(" - Take your time in Classic Mode to think about each move. 🕰");
        System.out.println(" - In Endless Mode, speed is key. The faster you clear lines, the more points you'll earn. ⚡️");
        System.out.println(" - Practice different piece placements to become more familiar with how they fit together. 🧠");

        System.out.println(ANSI_YELLOW + "Store & Statistics:" + ANSI_RESET);
        System.out.println(" - The game features a store where players can use coins earned from playing to buy hints. 🏪");
        System.out.println(" - Coins are earned by clearing lines, completing games, and achieving high scores. 🪙");
        System.out.println(" - Statistics for each player are tracked, including total games played, total time played, total experience, and coins earned. 📊");

        System.out.println(ANSI_YELLOW + "Leaderboard:" + ANSI_RESET);
        System.out.println(" - The game maintains a leaderboard showcasing top players based on various criteria such as coins earned, total time played, total experience, and completed games. 📈");
        System.out.println(" - Players can view the leaderboard to see how they rank against others and aim to improve their standings by playing more and earning higher scores. 👑");

        System.out.println(ANSI_YELLOW + "Hints:" + ANSI_RESET);
        System.out.println(" - Hints are available for purchase in the game's store. They can help players decide where to place pieces for optimal gameplay. 🛒");
        System.out.println(" - Players can use coins or experience points to purchase hints. Each hint provides a suggestion for placing a piece on the grid. 🤖");

        System.out.println(ANSI_PURPLE + "Quick Commands:" + ANSI_RESET);
        System.out.println(" - 🔄 Move: 'Block' + Cordinates to move + Enter");
        System.out.println(" - ⬇️ Place: Select grid position (e.g., '1 0') + Enter");
        System.out.println(" - 💡 Hint: 'Yes or No' + Enter ");
        System.out.println(" - 🚪 Quit: 'E' + Enter");

        System.out.println(ANSI_CYAN + "Support & Feedback:" + ANSI_RESET);
        System.out.println("Your experience is important to us! If you have any suggestions or need support,");
        System.out.println("please contact us at: support@blockpuzzlegame.com 📧");

        System.out.println(ANSI_YELLOW + "Remember:" + ANSI_RESET);
        System.out.println("The key to mastering Block Puzzle Game is practice, patience, and strategy. 🗝");
        System.out.println("Challenge yourself, improve your skills, and climb the leaderboard. 🌟");

        System.out.println(ANSI_GREEN + "Thank you for playing Block Puzzle Game. Enjoy and keep puzzling! 🎉" + ANSI_RESET);
    }
}
