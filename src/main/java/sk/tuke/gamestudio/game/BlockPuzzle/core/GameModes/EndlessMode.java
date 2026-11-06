package sk.tuke.gamestudio.game.BlockPuzzle.core.GameModes;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.database.DatabaseException;
import sk.tuke.gamestudio.database.PlayerDAO_JPA;
import sk.tuke.gamestudio.entity.GameSession;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;
import sk.tuke.gamestudio.game.BlockPuzzle.core.Tile;
import sk.tuke.gamestudio.game.BlockPuzzle.core.pieces.*;
import sk.tuke.gamestudio.game.BlockPuzzle.ui.BlockPuzzleGame;
import sk.tuke.gamestudio.game.BlockPuzzle.ui.CommentAndRatingUI;
import sk.tuke.gamestudio.game.BlockPuzzle.ui.ConsoleBorder;
import sk.tuke.gamestudio.game.BlockPuzzle.ui.ScoreUI;
import sk.tuke.gamestudio.services.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;




@Service
@Transactional
public class EndlessMode {
    private  String nextPieceType = null;
    private  int GRID_SIZE = 5;
    private  Tile[][] grid = new Tile[GRID_SIZE][GRID_SIZE];
    private  Map<String, Integer> availablePiecesCount;
    private  Scanner scanner = new Scanner(System.in);
    private  Map<Integer, String> selectionIndexToPieceType = new HashMap<>();

    private int hints = 0;
    private long startTime;
    private  List<Piece> placedPieces = new ArrayList<>();

    private  int totalScore = 0;
    private int totalCoins = 0;

    private long lastClearTime = System.currentTimeMillis();

    private int currentComboCount = 0;
    private final int maxComboBonus = 2;

    private boolean hardMode = false;
    private int rowsCleared = 0;
    private int colsCleared = 0;
    private int speedBonus = 0;

    private String username;

    private  int lastClearedRow = -1;
    private  int lastClearedCol = -1;
    static boolean gameLoaded;
    String currentGameMode = "Endless";
    private final CommentService commentService;
    private final RatingService ratingService;
    private final ScoreService scoreService;
    private final UserAuthenticationServiceJPA authService;

    private final ScoreUI scoreUI;

    private final GameSessionService  gameSessionService;

    private PlayerDAO_JPA playerDAO;

    @Autowired
    private BlockPuzzleGame blockPuzzleGame;

    @Autowired
    public EndlessMode(CommentService commentService, RatingService ratingService, ScoreService scoreService, UserAuthenticationServiceJPA authService, ScoreUI scoreUI, GameSessionService gameSessionService, PlayerDAO_JPA playerDAO) {
        this.commentService = commentService;
        this.ratingService = ratingService;
        this.scoreService = scoreService;
        this.authService = authService;
        this.scoreUI = scoreUI;
        this.gameSessionService = gameSessionService;
        this.playerDAO = playerDAO;
    }

    private static final Logger logger = LoggerFactory.getLogger(EndlessMode.class);

    @PersistenceContext
    private EntityManager entityManager;

    public void startGame() throws InterruptedException, DatabaseException {

        this.username = authService.authenticateUser();
        gameLoaded = offerGameOptions();
        if (!gameLoaded) {
            int levelChoice = 0;
            rowsCleared = 0;
            colsCleared = 0;
            currentComboCount = 0;
            while (true) {
                System.out.println("Choose difficulty level: 1 - Standard (5x5), 2 - Hard (10x10)");
                try {
                    String input = scanner.nextLine();
                    levelChoice = Integer.parseInt(input);
                    if (levelChoice == 1 || levelChoice == 2) {
                        break;
                    } else {
                        System.out.println("Please enter 1 for Standard or 2 for Hard.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }

            if (levelChoice == 2) {
                GRID_SIZE = 10;
                hardMode = true;
            } else {
                GRID_SIZE = 5;
                hardMode = false;
            }
        }
        if (!gameLoaded) {
            grid = new Tile[GRID_SIZE][GRID_SIZE];
            initializeGrid();
            calculateAvailablePieces();
        }

        scoreUI.displayPlayerStats(username);
        scoreUI.showLeaderboardSelection();


        boolean exitGame = false;
        startTime = System.currentTimeMillis();
        while (canPlaceAnyPiece()) {
            if (!gameLoaded) {
                playerDAO.startNewGame(username);
            }

            displayGrid();
            displayAvailablePieces();


            System.out.println("Press 'E' to exit the game or 'C' to continue playing.");
            String userInput = scanner.nextLine().trim().toUpperCase();
            if ("E".equals(userInput)) {
                exitGame = true;  // Signal that the game should exit.
                break;  // Immediately exit the while loop to handle game exit.
            } else if (!"C".equals(userInput)) {
                System.out.println("Invalid input. Please try again.");
                continue;  // Re-start the loop to correct invalid input without executing further code.
            }


            System.out.println("Choose a piece to place (or type 'reset' to restart): ");
            String choice = scanner.nextLine().trim();
            if ("reset".equalsIgnoreCase(choice)) {
                initializeGrid();
                calculateAvailablePieces();
                continue;
            }

            try {
                int selection = Integer.parseInt(choice);
                if (selectionIndexToPieceType.containsKey(selection)) {
                    String pieceType = selectionIndexToPieceType.get(selection);
                    Piece selectedPiece = createPiece(pieceType);
                    System.out.println("Selected: " + selectedPiece.getClass().getSimpleName());
                    System.out.println("Enter row and column to place piece (e.g., 2 3):");
                    String[] input = scanner.nextLine().split(" ");
                    if (input.length == 2) {
                        int row = Integer.parseInt(input[0]);
                        int col = Integer.parseInt(input[1]);
                        if (placePiece(row, col, selectedPiece)) {
                            decreasePieceCount(selectedPiece);
                            System.out.println("Piece placed.");
                        } else {
                            System.out.println("Invalid move. Please try again.");
                        }
                    } else {
                        System.out.println("Please enter row and column correctly.");
                    }
                } else {
                    System.out.println("Invalid selection. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }

        }

        CommentAndRatingUI commentaryAndRatingUI = new CommentAndRatingUI(commentService, ratingService, username, currentGameMode);

// Припустимо, що username вже визначено
        if (exitGame) {
            System.out.println("Exiting the game. Thank you for playing.");

            GameSession gameSession = new GameSession();
            // Set properties for gameSession based on the current game state
            gameSession.setUsername(username);
            gameSession.setGridState(serializeGrid());
            gameSession.setTotalScore(totalScore);
            gameSession.setTotalCoins(totalCoins);
            gameSession.setGameTime((int) ((System.currentTimeMillis() - startTime) / 1000));
            gameSession.setCurrentComboCount(currentComboCount);
            gameSession.setHardMode(hardMode);
            gameSession.setNextPieceType(nextPieceType);
            gameSession.setAvailablePieces(serializeAvailablePieces());
            gameSession.setRowsCleared(rowsCleared);
            gameSession.setColsCleared(colsCleared);
            gameSession.setSpeedBonus(speedBonus);

            gameSessionService.saveGame(gameSession);
            saveGameData(username, 0); // Pass a flag indicating the game was not completed
        } else if (!canPlaceAnyPiece()) {
            displayGrid();
            System.out.println("Zero move left. Sad!");
            saveGameData(username, 1); // Pass a flag indicating the game was completed
        }
        commentaryAndRatingUI.handleCommentsAndRatings();
        System.out.println("Would you like to play again? (yes/no)");
        String playAgainInput = scanner.nextLine().trim().toLowerCase();
        if ("yes".equals(playAgainInput)) {
            // Очищення стану гри для нового запуску
            grid = new Tile[GRID_SIZE][GRID_SIZE]; // Переініціалізація гріда
            initializeGrid(); // Первинна ініціалізація гріда
            availablePiecesCount.clear(); // Очищення доступних фігур
            placedPieces.clear(); // Очищення розміщених фігур

            // Виклик main знову для рестарту
            blockPuzzleGame.start();
        } else {
            System.out.println("Thank you for playing. Goodbye!");
        }
    }

    private boolean canPlaceAnyPiece() {
        for (String pieceType : availablePiecesCount.keySet()) {
            Piece piece = createPiece(pieceType);
            if (piece == null) continue;
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    if (canPlacePieceAtPosition(piece, row, col)) {
                        return true;
                    }
                }
            }
        }
        return false; // Якщо не знайдено жодної можливої позиції для розміщення блоку
    }

    private boolean canPlacePieceAtPosition(Piece piece, int row, int col) {
        for (Point square : piece.getSquares()) {
            int targetRow = row + square.x;
            int targetCol = col + square.y;
            if (targetRow < 0 || targetRow >= GRID_SIZE || targetCol < 0 || targetCol >= GRID_SIZE || grid[targetRow][targetCol].isUsed()) {
                return false;
            }
        }
        return true;
    }

    public void displayAvailablePieces() {
        Map<String, String> colorCodes = Map.ofEntries(
            Map.entry("PieceBigL", "\u001B[31m"), // Red
            Map.entry("PieceHorJ", "\u001B[32m"), // Green
            Map.entry("PieceS", "\u001B[33m"), // Yellow
            Map.entry("PieceSmallL", "\u001B[34m"), // Blue
            Map.entry("PieceT", "\u001B[35m"), // Purple
            Map.entry("Piece4x1", "\u001B[36m"), // Cyan
            Map.entry("Piece3x3", "\u001B[37m"), // White
            Map.entry("Piece3x1", "\u001B[90m"), // Bright Black (Gray)
            Map.entry("Piece2x1", "\u001B[91m"), // Bright Red
            Map.entry("Piece2x2", "\u001B[92m"), // Bright Green
            Map.entry("Piece1x5", "\u001B[93m"), // Bright Yellow
            Map.entry("Piece1x4", "\u001B[94m"), // Bright Blue
            Map.entry("Piece1x3", "\u001B[95m"), // Bright Purple
            Map.entry("Piece1x2", "\u001B[96m"), // Bright Cyan
            Map.entry("Piece1x1", "\u001B[97m")   // Bright White
        );

        selectionIndexToPieceType.clear();
        int displayIndex = 1;

        System.out.println("Available Pieces:");
        for (Map.Entry<String, Integer> entry : availablePiecesCount.entrySet()) {
            if (entry.getValue() > 0) { // Only display if available
                String colorCode = colorCodes.getOrDefault(entry.getKey(), "\u001B[0m"); // Default to no color
                List<String> shape = pieceShapes.getOrDefault(entry.getKey(), Arrays.asList(" "));

                System.out.println(displayIndex + ". " + entry.getKey() + " - Available: " + entry.getValue());
                for (String row : shape) {
                    System.out.println(colorCode + row + "\u001B[0m"); // Print each row of the shape in color
                }

                selectionIndexToPieceType.put(displayIndex, entry.getKey());
                displayIndex++;
            }
        }

        // Відображення наступного блоку з кольором та формою
        if (nextPieceType != null && colorCodes.containsKey(nextPieceType) && pieceShapes.containsKey(nextPieceType)) {
            String colorCode = colorCodes.getOrDefault(nextPieceType, "\u001B[0m");
            List<String> shape = pieceShapes.getOrDefault(nextPieceType, Arrays.asList(" "));
            System.out.println("Next Piece: " + nextPieceType + " (will be available next)");
            for (String row : shape) {
                System.out.println(colorCode + row + "\u001B[0m"); // Print each row of the next piece shape in color
            }
        }
    }
    private static Map<String, List<String>> pieceShapes = new HashMap<>();
    static {
        pieceShapes.put("PieceBigL", Arrays.asList("█  ", "█  ", "███"));
        pieceShapes.put("PieceHorJ", Arrays.asList("  █", "  █", "███"));
        pieceShapes.put("PieceS", Arrays.asList(" ██ ", "██"));
        pieceShapes.put("PieceSmallL", Arrays.asList("█ ", "██"));
        pieceShapes.put("PieceT", Arrays.asList("███", " █ "));
        // Correcting "Piece4x1" to represent 4 blocks vertically
        pieceShapes.put("Piece4x1", Arrays.asList("█", "█", "█", "█"));
        pieceShapes.put("Piece3x3", Arrays.asList("███", "███", "███"));
        pieceShapes.put("Piece3x1", Arrays.asList("█", "█", "█"));
        pieceShapes.put("Piece2x1", Arrays.asList("█", "█"));
        pieceShapes.put("Piece2x2", Arrays.asList("██", "██"));
        pieceShapes.put("Piece1x5", Arrays.asList("█████"));
        // Correcting "Piece1x4" to represent 4 blocks horizontally
        pieceShapes.put("Piece1x4", Arrays.asList("████"));
        pieceShapes.put("Piece1x3", Arrays.asList("███"));
        pieceShapes.put("Piece1x2", Arrays.asList("██"));
        pieceShapes.put("Piece1x1", Arrays.asList("█"));
    }
     boolean placePiece(int row, int col, Piece piece) {
        boolean canPlace = true;
        // Check if the piece can be placed
        for (Point square : piece.getSquares()) {
            int targetRow = row + square.x;
            int targetCol = col + square.y;

            if (targetRow < 0 || targetRow >= GRID_SIZE || targetCol < 0 || targetCol >= GRID_SIZE || grid[targetRow][targetCol].isUsed()) {
                canPlace = false;
                break;
            }
        }

        if (canPlace) {
            // Place the piece
            for (Point square : piece.getSquares()) {
                int targetRow = row + square.x;
                int targetCol = col + square.y;
                grid[targetRow][targetCol].setUsed(true);
            }

            checkAndClearFullLines(); // Check and clear full lines after placing the piece
            calculateAvailablePieces(); // Update available pieces
            return true;
        } else {
            System.out.println("Cannot place piece at the selected position.");
            return false;
        }
    }




    void calculateAvailablePieces() {

        Random random = new Random();

        if (!gameLoaded && nextPieceType == null) {
            List<String> pieceTypes = Arrays.asList(
                "PieceBigL", "Piece1x4", "Piece3x1", "Piece1x3",
                "Piece2x2", "Piece1x2", "Piece1x1", "Piece3x3",
                "Piece4x1", "Piece2x1", "Piece1x5", "PieceHorJ",
                "PieceS", "PieceSmallL", "PieceT"
            );
            availablePiecesCount = new HashMap<>();
            nextPieceType = pieceTypes.get(random.nextInt(pieceTypes.size()));
            availablePiecesCount.put(nextPieceType, 1); // Set this as currently available
        }

        // Moving the next piece to current available piece
        if (nextPieceType != null) {
            availablePiecesCount.clear(); // Clear previous counts
            availablePiecesCount.put(nextPieceType, 1); // Set next piece as currently available

            // Select a new next piece randomly from the list excluding the current nextPieceType
            List<String> pieceTypes = new ArrayList<>(Arrays.asList(
                "PieceBigL", "Piece1x4", "Piece3x1", "Piece1x3",
                "Piece2x2", "Piece1x2", "Piece1x1", "Piece3x3",
                "Piece4x1", "Piece2x1", "Piece1x5", "PieceHorJ",
                "PieceS", "PieceSmallL", "PieceT"
            ));
            pieceTypes.remove(nextPieceType); // Ensure the next piece is different
            nextPieceType = pieceTypes.get(random.nextInt(pieceTypes.size()));
        } else {
            // Initialize nextPieceType for the first time
            List<String> pieceTypes = Arrays.asList(
                "PieceBigL", "Piece1x4", "Piece3x1", "Piece1x3",
                "Piece2x2", "Piece1x2", "Piece1x1", "Piece3x3",
                "Piece4x1", "Piece2x1", "Piece1x5", "PieceHorJ",
                "PieceS", "PieceSmallL", "PieceT"
            );
            nextPieceType = pieceTypes.get(random.nextInt(pieceTypes.size()));
            availablePiecesCount.put(nextPieceType, 1); // Set this as currently available
        }
    }


    private void checkAndClearFullLines() {
        boolean rowFull, colFull;
        List<Integer> fullRows = new ArrayList<>();
        List<Integer> fullCols = new ArrayList<>();
        boolean cleared = false;

        // Перевірка рядків
        for (int i = 0; i < GRID_SIZE; i++) {
            rowFull = true;
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!grid[i][j].isUsed()) {
                    rowFull = false;
                    break;
                }
            }
            if (rowFull) {
                fullRows.add(i);

            }
        }

        // Перевірка стовпців
        for (int j = 0; j < GRID_SIZE; j++) {
            colFull = true;
            for (int i = 0; i < GRID_SIZE; i++) {
                if (!grid[i][j].isUsed()) {
                    colFull = false;
                    break;
                }
            }
            if (colFull) {
                fullCols.add(j);
            }
        }

        // Очищення повних рядків і стовпців
        if (!fullRows.isEmpty() || !fullCols.isEmpty()) {
            for (Integer row : fullRows) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    grid[row][col] = new Tile(row, col, false); // Оновлення стану кожної клітинки
                }
            }
            for (Integer col : fullCols) {
                for (int row = 0; row < GRID_SIZE; row++) {
                    grid[row][col] = new Tile(row, col, false); // Оновлення стану кожної клітинки
                }
            }
            cleared = true;
        }
        rowsCleared = fullRows.size();
        colsCleared = fullCols.size();
        if (cleared) {
            // Оновлюємо lastClearTime тільки якщо було очищення
            long currentTime = System.currentTimeMillis();
            long timeSinceLastClear = currentTime - lastClearTime;
            lastClearTime = currentTime;
            if (rowsCleared > 0 || colsCleared > 0) {
                currentComboCount++; // Збільшуємо комбо
                int comboBonus = Math.min(currentComboCount, maxComboBonus); // Обмежуємо максимальний бонус

                // Розрахунок швидкісного бонусу
                speedBonus = timeSinceLastClear < 40000 ? 2 : 1;
                if (hardMode) {
                    speedBonus = timeSinceLastClear < 100000 ? 2 : 1;
                }
                int scoreGained = calculateScore(rowsCleared, colsCleared) * comboBonus * speedBonus;
                int coinsGained = calculateCoins(scoreGained);

                System.out.println("Cleared " + rowsCleared + " rows and " + colsCleared + " columns.");
                System.out.println("Combo: " + currentComboCount + ". Speed bonus: " + speedBonus + ".");
                System.out.println("Gained " + scoreGained + " points and " + coinsGained + " coins with bonuses.");
            } else {
                currentComboCount = 0; // Скидаємо комбо, якщо не було очищено жодного рядка
            }

            if (rowsCleared + colsCleared >= 3) {
                generateBomb();
            }

            if (rowsCleared + colsCleared  >= 5) {
                int bonusRow = new Random().nextInt(GRID_SIZE);
                int bonusCol = new Random().nextInt(GRID_SIZE);
                activateLineClearBonus(bonusRow, bonusCol);
            }
            if (rowsCleared + colsCleared  >= 10) {
                activateSuperClearBonus();
            }

        }
    }

    private void activateLineClearBonus(int row, int col) {
        if (row != -1 && col != -1) { // Перевірка, що індекси встановлено
            // Очищення рядка і колонки, де була видалена остання лінія
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[row][j] = new Tile(row, j, false);
            }
            for (int i = 0; i < GRID_SIZE; i++) {
                grid[i][col] = new Tile(i, col, false);
            }
            System.out.println("Line Clear Bonus activated! Cleared row " + row + " and column " + col);
        }
    }

    private void activateSuperClearBonus() {
        lastClearedRow = GRID_SIZE / 2;
        lastClearedCol = GRID_SIZE / 2;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = new Tile(i, j, false); // Оновлено
            }
        }
        System.out.println("Super Clear Bonus activated! Cleared the whole board.");
    }


    private void generateBomb() {
        Random rand = new Random();
        int bombRow = lastClearedRow != -1 ? Math.max(0, Math.min(GRID_SIZE - 2, lastClearedRow - 1)) : rand.nextInt(GRID_SIZE - 1);
        int bombCol = lastClearedCol != -1 ? Math.max(0, Math.min(GRID_SIZE - 2, lastClearedCol - 1)) : rand.nextInt(GRID_SIZE - 1);
        for (int i = bombRow; i <= Math.min(GRID_SIZE - 1, bombRow + 1); i++) {
            for (int j = bombCol; j <= Math.min(GRID_SIZE - 1, bombCol + 1); j++) {
                grid[i][j] = new Tile(i, j, false); // Оновлено
            }
        }
        System.out.println("Bomb activated! Cleared 2x2 area starting from (" + bombRow + ", " + bombCol + ")");
    }




     void decreasePieceCount(Piece selectedPiece) {
        // Отримуємо назву класу вибраного блоку
        String selectedPieceName = selectedPiece.getClass().getSimpleName();

        // Зменшуємо кількість доступних блоків лише для вибраного типу
        if (availablePiecesCount.containsKey(selectedPieceName)) {
            int currentCount = availablePiecesCount.get(selectedPieceName);
            if (currentCount > 0) {
                // Зменшуємо кількість на одиницю
                availablePiecesCount.put(selectedPieceName, currentCount - 1);
            }
        }
    }

     void displayGrid() {
        StringBuilder sb = new StringBuilder();

        // Add column headers for clarity, starting from index 0, with proper spacing
        for (int col = 0; col < GRID_SIZE; col++) {
            sb.append(String.format(" %3d ", col));
        }
        sb.append("\n");

        for (int row = 0; row < GRID_SIZE; row++) {
            // Add row headers, starting from index 0
            sb.append(String.format("%d", row)); // Left align the row numbers, with a space for separation
            for (int col = 0; col < GRID_SIZE; col++) {
                String colorCode = getColorCodeForPiece(grid[row][col]);
                String cellRepresentation = grid[row][col].isUsed() ? colorCode + " ##" : "  . "; // Fixed width by adding spaces
                sb.append(String.format("%-5s", cellRepresentation)); // Ensure each cell block has fixed width
            }
            sb.append("\n");
        }

        ConsoleBorder.printTextInBorder(sb.toString());
    }


    private String getColorCodeForPiece(Tile tile) {
        return getString(tile);
    }
    static String getString(Tile tile) {
        if (!tile.isUsed() || tile.getPieceType() == null) {
            return " ";
        }


        String pieceType = tile.getPieceType(); // This method needs to exist in your Tile class.
        switch (pieceType) {
            case "PieceBigL":
                return "\u001B[31m"; // Red for "PieceBigL"
            case "PieceHorJ":
                return "\u001B[32m"; // Green for "PieceHorJ"
            case "PieceS":
                return "\u001B[33m"; // Yellow for "PieceS"
            case "PieceSmallL":
                return "\u001B[34m"; // Blue for "PieceSmallL"
            case "PieceT":
                return "\u001B[35m"; // Purple for "PieceT"
            case "Piece4x1":
                return "\u001B[36m"; // Cyan for "Piece4x1"
            case "Piece3x3":
                return "\u001B[37m"; // White for "Piece3x3"
            case "Piece3x1":
                return "\u001B[90m"; // Bright Black (Gray) for "Piece3x1"
            case "Piece2x1":
                return "\u001B[91m"; // Bright Red for "Piece2x1"
            case "Piece2x2":
                return "\u001B[92m"; // Bright Green for "Piece2x2"
            case "Piece1x5":
                return "\u001B[93m"; // Bright Yellow for "Piece1x5"
            case "Piece1x4":
                return "\u001B[94m"; // Bright Blue for "Piece1x4"
            case "Piece1x3":
                return "\u001B[95m"; // Bright Purple for "Piece1x3"
            case "Piece1x2":
                return "\u001B[96m"; // Bright Cyan for "Piece1x2"
            case "Piece1x1":
                return "\u001B[97m"; // Bright White for "Piece1x1"
            default:
                return "\u001B[0m"; // Reset to default terminal color for any unrecognized piece type.
        }
    }



    static Piece createPiece(String pieceType) {
        switch (pieceType) {
            case "PieceBigL":
                return new PieceBigL();
            case "PieceHorJ":
                return new PieceHorJ();
            case "PieceS":
                return new PieceS();
            case "PieceSmallL":
                return new PieceSmallL();
            case "PieceT":
                return new PieceT();
            case "Piece4x1":
                return new Piece4x1();
            case "Piece3x3":
                return new Piece3x3();
            case "Piece3x1":
                return new Piece3x1();
            case "Piece2x1":
                return new Piece2x1();
            case "Piece2x2":
                return new Piece2x2();
            case "Piece1x5":
                return new Piece1x5();
            case "Piece1x4":
                return new Piece1x4();
            case "Piece1x3":
                return new Piece1x3();
            case "Piece1x2":
                return new Piece1x2();
            case "Piece1x1":
                return new Piece1x1();
            default:
                return null;
        }
    }

    public void saveGameData(String username, int completedGames) {
        long playTime = (System.currentTimeMillis() - startTime) / 1000;

        int scoreValue = calculateScore(rowsCleared, colsCleared);
        int coins = completedGames > 0 ? calculateCoins(scoreValue) : 0;

        Score score = new Score(username, playTime, scoreValue, hints, coins, completedGames);


            scoreService.addScore(score);

            if (completedGames > 0) {
                System.out.println("Game data saved. Play time: " + playTime + " sec, Score: " + scoreValue + ", Coins earned: " + coins);
            } else {
                System.out.println("Game data saved. Play time: " + playTime + " sec");
            }


        totalScore = 0;
        totalCoins = 0;
    }



    int calculateScore(int rowsCleared, int colsCleared) {
        int baseScore = (rowsCleared + colsCleared) * 100; // Базовий обрахунок очок
        // Збільшення очок за лінію в важчому режимі
        if (hardMode) {
            baseScore *= (int) 1.5; // Наприклад, збільшуємо винагороду на 50%
        }
        return baseScore;
    }


    public  int calculateCoins(int scoreGained) {
        // Монети розраховуються на основі очок, з додатковим бонусом для хард-моду
        int baseCoins = 20 + scoreGained / 200; // Гарантовано 10 монет за гру + додаткові за очки
        int hardModeBonus = hardMode ? 5 : 0; // Додаткові монети у хард-моді
        return baseCoins + hardModeBonus;
    }


     void initializeGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = new Tile();
            }
        }
    }



    private String serializeAvailablePieces() {
        if (availablePiecesCount == null || availablePiecesCount.isEmpty()) {
            return "";
        }

        return availablePiecesCount.entrySet().stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.joining(";"));
    }

    public Tile[][] deserializeGrid(String gridState, int gridSize) {
        Tile[][] grid = new Tile[gridSize][gridSize];
        if (gridState == null || gridState.isEmpty()) {
            fillDefaultGrid(grid, gridSize);
            return grid;
        }

        String[] rows = gridState.split(";");
        for (int i = 0; i < rows.length && i < gridSize; i++) {
            String[] cols = rows[i].split(",");
            for (int j = 0; j < cols.length && j < gridSize; j++) {
                grid[i][j] = new Tile(i, j, "1".equals(cols[j]));
            }
            fillRowWithDefaultTiles(grid, i, cols.length, gridSize);
        }
        fillRemainingRowsWithDefaultTiles(grid, rows.length, gridSize);

        return grid;
    }

    private void fillDefaultGrid(Tile[][] grid, int gridSize) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j] = new Tile(i, j, false);
            }
        }
    }

    private void fillRowWithDefaultTiles(Tile[][] grid, int rowIndex, int startCol, int gridSize) {
        for (int j = startCol; j < gridSize; j++) {
            grid[rowIndex][j] = new Tile(rowIndex, j, false);
        }
    }

    private void fillRemainingRowsWithDefaultTiles(Tile[][] grid, int startRow, int gridSize) {
        for (int i = startRow; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j] = new Tile(i, j, false);
            }
        }
    }


    public Map<String, Integer> deserializeAvailablePieces(String serializedData) {
        Map<String, Integer> availablePiecesCount = new HashMap<>();
        if (serializedData == null || serializedData.trim().isEmpty()) {
            return availablePiecesCount;
        }

        String[] pieces = serializedData.split(";");
        for (String piece : pieces) {
            if (piece.isEmpty()) continue; // Skip empty pieces to prevent unnecessary processing

            String[] parts = piece.split(":");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                try {
                    availablePiecesCount.put(key, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing integer from serialized data: '" + value + "' in piece: '" + piece + "'");
                }
            }
        }
        return availablePiecesCount;
    }

    private String serializeGrid() {
        if (grid == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                sb.append(grid[i][j].isUsed() ? "1" : "0");
                if (j < GRID_SIZE - 1) sb.append(",");
            }
            if (i < GRID_SIZE - 1) sb.append(";");
        }
        return sb.toString();
    }
    private boolean offerGameOptions() {
        // Attempt to load the last game
        GameSession gameSession = gameSessionService.loadGame(this.username);
        boolean gameLoaded = gameSession != null;

        if (gameLoaded) {
            // Deserialize and initialize the game state from the loaded game session
            initializeGameState(gameSession);
            System.out.println("Do you want to continue your last game or start a new one? (continue/new)");
            String choice = scanner.nextLine().trim().toLowerCase();
            if ("continue".equals(choice)) {
                // Continue with the loaded game
                return true;
            } else if ("new".equals(choice)) {
                // Start a new game session
                playerDAO.startNewGame(this.username);
                return false;
            } else {
                System.out.println("Invalid choice. Please choose 'continue' or 'new'.");
                return offerGameOptions(); // Recursive call for retrying the choice
            }
        } else {
            System.out.println("No previous game found. Would you like to start a new game? (yes/no)");
            String choice = scanner.nextLine().trim().toLowerCase();
            if ("yes".equals(choice)) {
                playerDAO.startNewGame(this.username);
                return false; // Starting a new game
            } else {
                System.out.println("No game will be started.");
                return false; // Not starting a new game, could exit or show main menu
            }
        }
    }

    private void initializeGameState(GameSession gameSession) {
        this.hardMode = gameSession.getHardMode();
        this.GRID_SIZE = hardMode ? 10 : 5;
        this.grid = deserializeGrid(gameSession.getGridState(), GRID_SIZE);

        this.totalScore = gameSession.getTotalScore();
        this.totalCoins = gameSession.getTotalCoins();
        this.startTime = System.currentTimeMillis() - gameSession.getGameTime() * 1000L;
        this.currentComboCount = gameSession.getCurrentComboCount();

        // Use the return value of deserializeAvailablePieces to set availablePiecesCount
        this.availablePiecesCount = deserializeAvailablePieces(gameSession.getAvailablePieces());

        this.nextPieceType = gameSession.getNextPieceType();
        this.rowsCleared = gameSession.getRowsCleared();
        this.colsCleared = gameSession.getColsCleared();
        this.speedBonus = gameSession.getSpeedBonus();
    }

}
