package sk.tuke.gamestudio.game.BlockPuzzle.core.GameModes;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.database.DatabaseException;
import sk.tuke.gamestudio.database.PlayerDAO;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;
import sk.tuke.gamestudio.game.BlockPuzzle.core.Tile;
import sk.tuke.gamestudio.game.BlockPuzzle.core.pieces.*;
import sk.tuke.gamestudio.game.BlockPuzzle.ui.BlockPuzzleGame;
import sk.tuke.gamestudio.game.BlockPuzzle.ui.CommentAndRatingUI;
import sk.tuke.gamestudio.game.BlockPuzzle.ui.ConsoleBorder;
import sk.tuke.gamestudio.game.BlockPuzzle.ui.ScoreUI;
import sk.tuke.gamestudio.services.CommentService;
import sk.tuke.gamestudio.services.RatingService;
import sk.tuke.gamestudio.services.ScoreService;
import sk.tuke.gamestudio.services.UserAuthenticationServiceJPA;

import java.awt.*;
import java.util.List;
import java.util.*;



@Service
public class ClassicMode {
    public static final int GRID_SIZE = 5;
    private Tile[][] grid = new Tile[GRID_SIZE][GRID_SIZE];
    private Map<String, Integer> availablePiecesCount;
    private List<Piece> availablePiecesList = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);
    private Map<Integer, String> selectionIndexToPieceType = new HashMap<>();

    private int hints;
    private long startTime;
    private List<Piece> placedPieces = new ArrayList<>();

    private String username;

    String currentGameMode = "Classic";

    private final CommentService commentService;
    private final RatingService ratingService;
    private final ScoreService scoreService;
    private final UserAuthenticationServiceJPA authService;
    private final ScoreUI scoreUI;

    private PlayerDAO playerDAO;

    @Autowired
    private BlockPuzzleGame blockPuzzleGame;

    @Autowired
    public ClassicMode(CommentService commentService, RatingService ratingService, ScoreService scoreService, UserAuthenticationServiceJPA authService, ScoreUI scoreUI, PlayerDAO playerDAO) {
        this.commentService = commentService;
        this.ratingService = ratingService;
        this.scoreService = scoreService;
        this.authService = authService;
        this.scoreUI = scoreUI;
        this.playerDAO = playerDAO;
    }


    public void startGame() throws InterruptedException, DatabaseException {

        this.username = authService.authenticateUser();
        hints = playerDAO.getHintsForPlayer(username);
        initializeGrid();
        calculateAvailablePieces();

        scoreUI.displayPlayerStats(username);
        scoreUI.showLeaderboardSelection();


        purchaseHints(username);
        boolean exitGame = false;
        startTime = System.currentTimeMillis();
        while (!isGridFull()) {
            playerDAO.startNewGame(username);

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


            boolean askForHint = true;
            while (askForHint && hints > 0) {
                System.out.println("You have " + hints + " hint(s). Do you want to use a hint? (yes/no)");
                String hintChoice = scanner.nextLine().trim();
                if ("yes".equalsIgnoreCase(hintChoice)) {
                    hints--;

                    showHint();
                    askForHint = false;
                } else if ("no".equalsIgnoreCase(hintChoice)) {
                    askForHint = false;
                } else {
                    System.out.println("Please answer 'yes' or 'no'.");
                }
            }

            boolean actionTaken = false;
            while (!actionTaken && !placedPieces.isEmpty()) {
                System.out.println("Do you want to move an existing piece or return it to the list of available pieces? (move/return/no)");
                String actionChoice = scanner.nextLine().trim();
                switch (actionChoice.toLowerCase()) {
                    case "move":
                        displayPlacedPieces();
                        System.out.println("Select the piece number to move:");
                        try {
                            int pieceIndex = Integer.parseInt(scanner.nextLine()) - 1;
                            if (pieceIndex >= 0 && pieceIndex < placedPieces.size()) {
                                Piece pieceToMove = placedPieces.get(pieceIndex);
                                System.out.println("Moving: " + pieceToMove.getClass().getSimpleName());
                                // Запам'ятовуємо початкові координати для можливості повернення на них
                                int originalRow = pieceToMove.getStartRow();
                                int originalCol = pieceToMove.getStartCol();
                                removePieceFromGrid(pieceToMove); // Видаляємо блок з ігрового поля

                                System.out.println("Enter new row and column to place " + pieceToMove.getClass().getSimpleName() + " (e.g., 2 3):");
                                String[] newPlacement = scanner.nextLine().split(" ");
                                int newRow = Integer.parseInt(newPlacement[0]);
                                int newCol = Integer.parseInt(newPlacement[1]);

                                if (placePiece(newRow, newCol, pieceToMove)) { // Якщо переміщення вдалося
                                    System.out.println(pieceToMove.getClass().getSimpleName() + " moved.");
                                    actionTaken = true;
                                } else {
                                    System.out.println("Invalid move. Piece not moved.");
                                    // Повернення блоку на його початкове місце на полі не потрібно знову додавати до placedPieces,
                                    // оскільки він вже там є
                                    placePiece(originalRow, originalCol, pieceToMove); // Відновлюємо блок на його оригінальні координати без повторного додавання
                                    System.out.println("Piece returned to its original position.");
                                }
                            } else {
                                System.out.println("Invalid selection. Try again.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number.");
                        }
                        break;
                    case "return":
                        displayPlacedPieces();
                        System.out.println("Select the piece number to return:");
                        try {
                            int pieceIndex = Integer.parseInt(scanner.nextLine()) - 1;
                            if (pieceIndex >= 0 && pieceIndex < placedPieces.size()) {
                                Piece pieceToReturn = placedPieces.remove(pieceIndex);
                                removePieceFromGrid(pieceToReturn);
                                returnPieceToAvailable(pieceToReturn);
                                System.out.println(pieceToReturn.getClass().getSimpleName() + " returned to the list of available pieces.");
                                actionTaken = true;
                            } else {
                                System.out.println("Invalid selection. Try again.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number.");
                        }
                        break;
                    case "no":
                        actionTaken = true;
                        break;
                    default:
                        System.out.println("Invalid response. Please answer 'move', 'return', or 'no'.");
                        break;
                }
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

        if (exitGame) {
            System.out.println("Exiting the game. Thank you for playing.");
            saveGameData(username, 0); // Pass 0 indicating the game was not completed
        } else if (isGridFull()) {
            System.out.println("Congratulations! You've completed the game!");
            saveGameData(username, 1); // Pass 1 indicating the game was completed
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
            hints = playerDAO.getHintsForPlayer(username); // Оновлення кількості підказок

            // Виклик main знову для рестарту
            blockPuzzleGame.start();
        } else {
            System.out.println("Thank you for playing. Goodbye!");
        }
    }

    public void purchaseHints(String username) {
        System.out.println("Would you like to buy hints? (yes/no)");
        String response = scanner.nextLine().trim().toLowerCase();
        if (!"yes".equals(response)) {
            System.out.println("No hints purchased.");
            return;
        }

        System.out.println("How many hints would you like to buy?");
        int numberOfHints = 0;
        try {
            numberOfHints = Integer.parseInt(scanner.nextLine()); // Get the number of hints the user wants to buy
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of hints. Please enter a valid number.");
            return; // Exit if the input is not a valid integer
        }

        if (numberOfHints <= 0) {
            System.out.println("The number of hints must be greater than 0.");
            return; // Ensure the user enters a positive number of hints
        }

        System.out.println("Choose the payment method: 1 for Experience, 2 for Coins");
        int paymentMethod;
        try {
            paymentMethod = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid payment method. Please enter a valid number.");
            return; // Exit if the input is not a valid integer
        }

        boolean purchaseSuccessful = false;
        switch (paymentMethod) {
            case 1:
                purchaseSuccessful = playerDAO.useExperienceForHint(username, numberOfHints);
                if (purchaseSuccessful) {
                    hints += numberOfHints;
                    System.out.println("You have successfully purchased " + numberOfHints + " hints using Experience. You now have " + hints + " hints.");
                } else {
                    System.out.println("Not enough Experience to purchase hints.");
                }
                break;
            case 2:
                purchaseSuccessful = playerDAO.useCoinsForHint(username, numberOfHints);
                if (purchaseSuccessful) {
                    hints += numberOfHints;
                    System.out.println("You have successfully purchased " + numberOfHints + " hints using Coins. You now have " + hints + " hints.");
                } else {
                    System.out.println("Not enough Coins to purchase hints.");
                }
                break;
            default:
                System.out.println("Invalid payment method selected.");
                break;
        }
    }

     void showHint() {
        if (availablePiecesList.isEmpty() && placedPieces.isEmpty()) {
            System.out.println("No moves available.");
            return;
        }

        int bestRow = -1;
        int bestCol = -1;
        int bestScore = Integer.MIN_VALUE;
        Piece bestPiece = null;
        boolean isMove = false;
        boolean placementFound = false;

        // Спочатку шукаємо найкраще місце для розміщення нових блоків
        for (String pieceType : availablePiecesCount.keySet()) {
            // Пропускаємо, якщо всі блоки цього типу вже використані
            if (availablePiecesCount.get(pieceType) == 0) continue;

            try {
                Piece piece = createPiece(pieceType); // Створюємо блок даного типу
                for (int row = 0; row < GRID_SIZE; row++) {
                    for (int col = 0; col < GRID_SIZE; col++) {
                        if (isPositionOccupied(row, col, piece)) {
                            continue; // Якщо позиція зайнята, пропускаємо її
                        }
                        int score = simulatePlacement(piece, row, col, false);
                        if (score > bestScore) {
                            bestScore = score;
                            bestRow = row;
                            bestCol = col;
                            bestPiece = piece;
                            isMove = false;
                            placementFound = true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // У випадку помилки при створенні блоку
            }
        }

        // Якщо не знайдено нове розміщення, шукаємо можливість переміщення існуючих блоків
        if (!placementFound) {
            for (Piece placedPiece : placedPieces) {
                for (int row = 0; row < GRID_SIZE; row++) {
                    for (int col = 0; col < GRID_SIZE; col++) {
                        int score = simulatePlacement(placedPiece, row, col, true);
                        if (score > bestScore) {
                            bestScore = score;
                            bestRow = row;
                            bestCol = col;
                            bestPiece = placedPiece;
                            isMove = true;
                            placementFound = true;
                        }
                    }
                }
            }
        }

        if (placementFound && bestPiece != null) {
            String action = isMove ? "Move" : "Place";
            System.out.println("Hint: " + action + " " + bestPiece.getClass().getSimpleName() + " at (" + bestRow + ", " + bestCol + ").");
        } else {
            System.out.println("No valid position found for a hint.");
        }
    }

    // Додаткова метода для перевірки зайнятості позиції
    private boolean isPositionOccupied(int row, int col, Piece piece) {
        for (Point square : piece.getSquares()) {
            int targetRow = row + square.x;
            int targetCol = col + square.y;
            if (!withinGrid(targetRow, targetCol) || grid[targetRow][targetCol].isUsed()) {
                return true; // Позиція зайнята
            }
        }
        return false; // Позиція вільна
    }


    private int simulatePlacement(Piece piece, int row, int col, boolean isMove) {
        int score = 0;

        // Створення копії ігрового поля для симуляції
        boolean[][] gridCopy = new boolean[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                gridCopy[i][j] = grid[i][j].isUsed();
            }
        }

        // Якщо це переміщення, спочатку видаляємо фігуру з поточної позиції
        if (isMove) {
            for (Point square : piece.getSquares()) {
                int currentRow = piece.getStartRow() + square.x;
                int currentCol = piece.getStartCol() + square.y;
                if (withinGrid(currentRow, currentCol)) {
                    gridCopy[currentRow][currentCol] = false;
                }
            }
        }

        // Перевірка можливості розміщення фігури на новій позиції
        for (Point square : piece.getSquares()) {
            int targetRow = row + square.x;
            int targetCol = col + square.y;
            if (!withinGrid(targetRow, targetCol) || gridCopy[targetRow][targetCol]) {
                return -1; // Неможливо розмістити фігуру, якщо місце вже зайняте
            }
        }

        // Розміщення фігури на копії ігрового поля
        for (Point square : piece.getSquares()) {
            int targetRow = row + square.x;
            int targetCol = col + square.y;
            gridCopy[targetRow][targetCol] = true;
        }

        // Аналіз копії ігрового поля після розміщення
        score = analyzeGrid(gridCopy);

        return score;
    }

    private boolean withinGrid(int row, int col) {
        return row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE;
    }

    private static int analyzeGrid(boolean[][] gridCopy) {
        int score = 0;
        int filledLines = 0;
        int holes = 0;

        // Підрахунок заповнених ліній
        for (int i = 0; i < GRID_SIZE; i++) {
            boolean lineFull = true;
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!gridCopy[i][j]) {
                    lineFull = false;
                    break;
                }
            }
            if (lineFull) {
                filledLines++;
            }
        }

        // Підрахунок "дір" під блоками
        for (int j = 0; j < GRID_SIZE; j++) {
            for (int i = GRID_SIZE - 1; i >= 0; i--) {
                if (!gridCopy[i][j] && i > 0 && gridCopy[i - 1][j]) {
                    holes++;
                }
            }
        }

        score += filledLines * 10 - holes * 5;

        return score;
    }

    public void saveGameData(String username, int completedGames) {
        int playTime = (int) ((System.currentTimeMillis() - startTime) / 1000); // Calculate play time
        int experience = calculateScore(); // Calculate experience based on your logic
        int coins = calculateCoins(); // Assuming calculateCoins method exists and returns int

        // Create a Score object
        Score scoreObj = new Score(username, playTime, experience, hints, coins, completedGames);

        // Use the injected ScoreService to add the score
        scoreService.addScore(scoreObj);

        // Log output based on game completion
        if (completedGames > 0) {
            System.out.println("Game data saved. Play time: " + playTime + " sec, Score: " + experience + ", Coins earned: " + coins);
        } else {
            System.out.println("Game data saved. Play time: " + playTime + " sec");
        }

        playerDAO.updateHints(username, hints);
    }

    public int calculateScore() {
        // Припустимо, що score базується на кількості заповнених ліній
        int filledLines = countFilledLines();
        return filledLines * 100; // Наприклад, за кожну заповнену лінію даємо 100 очок
    }

    public int calculateCoins() {
        // Монети розраховуються на основі очок
        int score = calculateScore();
        return 10 + score / 200; // Гарантовано 10 монет за гру + додаткові за очки
    }
    public boolean placePiece(int row, int col, Piece piece) {
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
            for (Point square : piece.getSquares()) {
                int targetRow = row + square.x;
                int targetCol = col + square.y;
                grid[targetRow][targetCol].setUsed(true);
                grid[targetRow][targetCol].setPieceType(piece.getClass().getSimpleName()); // Set the piece type
            }
            // Record the piece's position
            piece.setStartRow(row);
            piece.setStartCol(col);
            placedPieces.add(piece); // Add the piece to the list of placed pieces
        }
        return canPlace;
    }
    public void initializeGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = new Tile();
            }
        }
    }


    void calculateAvailablePieces() {
        availablePiecesCount = new HashMap<>(); // Очищення поточного списку доступних блоків
        boolean fieldFullyPopulated = false; // Прапорець для перевірки, чи повністю заповнене поле

        while (!fieldFullyPopulated) {
            int simulationScore, bestSimulationScore = Integer.MIN_VALUE;
            List<String> bestPieces = new ArrayList<>();

            for (int attempt = 0; attempt < 100; attempt++) {
                List<String> currentAttemptPieces = new ArrayList<>();
                boolean[][] simulationGrid = new boolean[GRID_SIZE][GRID_SIZE];
                initializeSimulationGrid(simulationGrid);

                while (!isGridFullSimulation(simulationGrid)) {
                    String randomPieceType = getRandomPieceType(currentAttemptPieces);
                    Piece piece = createPiece(randomPieceType);
                    if (piece == null) continue;

                    if (placePieceSimulationRandomly(piece, simulationGrid)) {
                        currentAttemptPieces.add(randomPieceType);
                    } else {
                        break;
                    }
                }

                // Оновлено для включення currentAttemptPieces як другого аргумента
                simulationScore = analyzeSimulationGrid(simulationGrid, currentAttemptPieces);
                if (simulationScore > bestSimulationScore) {
                    bestSimulationScore = simulationScore;
                    bestPieces = new ArrayList<>(currentAttemptPieces);
                }
            }
            // Перевіряємо, чи найкраща спроба спричинила повне заповнення поля
            if (!bestPieces.isEmpty() && isGridFullSimulation(createSimulationGridFromPieces(bestPieces))) {
                fieldFullyPopulated = true; // Встановлюємо прапорець, що поле повністю заповнене

                // Оновлюємо список доступних блоків на основі найкращої спроби
                for (String pieceType : bestPieces) {
                    availablePiecesCount.put(pieceType, availablePiecesCount.getOrDefault(pieceType, 0) + 1);
                }
            }
        }

        // Оновлюємо список доступних блоків для гри
        populateAvailablePiecesList();
    }

    private boolean[][] createSimulationGridFromPieces(List<String> pieceTypes) {
        boolean[][] simulationGrid = new boolean[GRID_SIZE][GRID_SIZE];
        initializeSimulationGrid(simulationGrid); // Ініціалізація симуляційного ігрового поля

        for (String pieceType : pieceTypes) {
            Piece piece = createPiece(pieceType);
            if (piece == null) continue; // Якщо тип блоку некоректний, пропускаємо його

            boolean placedSuccessfully = placePieceSimulationRandomly(piece, simulationGrid);
            if (!placedSuccessfully) {
                // Якщо не вдалося розмістити блок, можемо зупинити цикл або спробувати розмістити блок ще раз
                // У цьому прикладі просто пропускаємо невдалий блок
                continue;
            }

            // Перевірка, чи заповнене поле після додавання кожного блоку
            if (isGridFullSimulation(simulationGrid)) {
                break; // Якщо поле повністю заповнене, завершуємо цикл
            }
        }

        return simulationGrid;
    }
    private boolean placePieceSimulationRandomly(Piece piece, boolean[][] simulationGrid) {
        Random random = new Random();

        // Спроби розміщення блоку на полі
        for (int attempt = 0; attempt < 100; attempt++) { // Обмежуємо кількість спроб, щоб уникнути нескінченного циклу
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);

            // Перевірка, чи можна розмістити блок на цій позиції
            if (canPlacePieceSimulation(piece, row, col, simulationGrid)) {
                // Розміщення блоку на полі
                for (Point square : piece.getSquares()) {
                    int targetRow = row + square.x;
                    int targetCol = col + square.y;
                    if (withinGrid(targetRow, targetCol)) {
                        simulationGrid[targetRow][targetCol] = true;
                    }
                }
                return true; // Блок успішно розміщено
            }
        }

        return false; // Не вдалося розмістити блок після заданої кількості спроб
    }
    private boolean canPlacePieceSimulation(Piece piece, int row, int col, boolean[][] simulationGrid) {
        // Перевіряємо кожен квадратик фігури
        for (Point square : piece.getSquares()) {
            int targetRow = row + square.x;
            int targetCol = col + square.y;

            // Перевіряємо, чи фігура виходить за межі симуляційного ігрового поля
            if (targetRow < 0 || targetRow >= GRID_SIZE || targetCol < 0 || targetCol >= GRID_SIZE) {
                return false; // Фігура виходить за межі ігрового поля
            }

            // Перевіряємо, чи клітинка вже зайнята
            if (simulationGrid[targetRow][targetCol]) {
                return false; // Клітинка вже зайнята
            }
        }

        return true; // Фігуру можна розмістити
    }
    private int analyzeSimulationGrid(boolean[][] simulationGrid, List<String> usedTypes) {
        int score = 0;
        Set<String> uniqueBlockTypes = new HashSet<>(usedTypes);
        int diversityBonus = uniqueBlockTypes.size() * 50; // Бонус за різноманітність використаних блоків

        // Параметри для оцінки
        int filledLines = 0;
        int holes = 0;
        int aggregateHeight = 0;
        int bumpiness = 0;
        int[] columnHeights = new int[GRID_SIZE];
        int gapsBelowBlocks = 0; // Кількість "дір" під блоками

        // Розрахунок висоти кожного стовпця і загальної висоти
        for (int j = 0; j < GRID_SIZE; j++) {
            boolean columnStarted = false;
            for (int i = GRID_SIZE - 1; i >= 0; i--) {
                if (simulationGrid[i][j]) {
                    columnHeights[j] = GRID_SIZE - i;
                    columnStarted = true;
                } else if (columnStarted) {
                    gapsBelowBlocks++;
                }
            }
            aggregateHeight += columnHeights[j];
        }

        // Визначення нерівності між стовпцями і кількості заповнених ліній
        for (int i = 0; i < GRID_SIZE - 1; i++) {
            bumpiness += Math.abs(columnHeights[i] - columnHeights[i + 1]);
        }

        for (int i = 0; i < GRID_SIZE; i++) {
            boolean isLineFull = true;
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!simulationGrid[i][j]) {
                    isLineFull = false;
                    break;
                }
            }
            if (isLineFull) filledLines++;
        }

        // Розрахунок фінальної оцінки з урахуванням різноманітності та інших параметрів
        score += filledLines * 100 - holes * 50 - gapsBelowBlocks * 25 - bumpiness * 10 - aggregateHeight + diversityBonus;

        return score;
    }

    private static void initializeSimulationGrid(boolean[][] simulationGrid) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                simulationGrid[i][j] = false; // Початково всі клітинки не заповнені
            }
        }
    }

    // Перевірка, чи заповнене симуляційне ігрове поле
    private static boolean isGridFullSimulation(boolean[][] simulationGrid) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!simulationGrid[i][j]) {
                    return false; // Знайдена не заповнена клітинка
                }
            }
        }
        return true; // Всі клітинки заповнені
    }

    // Вибір випадкового типу блоку
    private String getRandomPieceType(List<String> usedTypes) {
        List<String> pieceTypes = Arrays.asList(
            "PieceBigL", "Piece1x4", "Piece3x1", "Piece1x3",
            "Piece2x2", "Piece1x2", "Piece1x1", "Piece3x3",
            "Piece4x1", "Piece2x1", "Piece1x5", "PieceHorJ",
            "PieceS", "PieceSmallL", "PieceT"
        );

        Random random = new Random();
        List<String> availableTypes = new ArrayList<>(pieceTypes);

        // Видалення вже використаних типів для збільшення рознообразя
        availableTypes.removeAll(usedTypes);

        // Якщо всі типи використані, скидаємо список використаних типів
        if (availableTypes.isEmpty()) {
            availableTypes = new ArrayList<>(pieceTypes); // або можна використати іншу логіку
        }

        return availableTypes.get(random.nextInt(availableTypes.size()));
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
                String cellRepresentation = grid[row][col].isUsed() ? " ##" : "  . "; // Fixed width by adding spaces
                sb.append(String.format("%-5s", cellRepresentation)); // Ensure each cell block has fixed width
            }
            sb.append("\n");
        }

        ConsoleBorder.printTextInBorder(sb.toString());
    }

     String getString(Tile tile) {
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


    void displayAvailablePieces() {
        // ANSI escape codes for colors
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
                List<String> shape = getPieceShape(entry.getKey());

                System.out.println(displayIndex + ". " + entry.getKey() + " - Available: " + entry.getValue());
                for (String row : shape) {
                    System.out.println(colorCode + row + "\u001B[0m"); // Print each row of the shape in color
                }

                // Map the display index to the piece type
                selectionIndexToPieceType.put(displayIndex, entry.getKey());

                displayIndex++;
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

    private static List<String> getPieceShape(String pieceType) {
        return pieceShapes.getOrDefault(pieceType, Arrays.asList(" "));
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

    public int countFilledLines() {
        // Метод для підрахунку кількості заповнених ліній у грі
        int filledLines = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            boolean lineFull = true;
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!grid[i][j].isUsed()) {
                    lineFull = false;
                    break;
                }
            }
            if (lineFull) {
                filledLines++;
            }
        }
        return filledLines;
    }

    void displayPlacedPieces() {
        if (placedPieces.isEmpty()) {
            System.out.println("No pieces placed yet.");
            return;
        }
        System.out.println("Placed Pieces:");
        int index = 1;
        for (Piece piece : placedPieces) {
            System.out.println(index++ + ". " + piece.getClass().getSimpleName() + " at (" + piece.getStartRow() + ", " + piece.getStartCol() + ")");
        }
    }

    public void removePieceFromGrid(Piece piece) {
        for (Point square : piece.getSquares()) {
            int targetRow = piece.getStartRow() + square.x;
            int targetCol = piece.getStartCol() + square.y;
            if (targetRow >= 0 && targetRow < GRID_SIZE && targetCol >= 0 && targetCol < GRID_SIZE) {
                grid[targetRow][targetCol].setUsed(false);
            }
        }
        piece.setStartRow(-1); // Reset the piece's position
        piece.setStartCol(-1);
    }



    private void populateAvailablePiecesList() {
        availablePiecesList.clear(); // Очищуємо список доступних блоків
        for (Map.Entry<String, Integer> entry : availablePiecesCount.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                availablePiecesList.add(createPiece(entry.getKey())); // Створюємо блоки та додаємо їх до списку
            }
        }
    }


    public boolean isGridFull() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!grid[i][j].isUsed()) {
                    return false;
                }
            }
        }
        return true;
    }


    void returnPieceToAvailable(Piece piece) {
        // Перевіряємо, чи вже існує цей тип блоку в списку доступних
        boolean pieceExists = false;
        for (Piece availablePiece : availablePiecesList) {
            if (availablePiece.getClass().equals(piece.getClass())) {
                pieceExists = true;
                break;
            }
        }

        // Якщо блоку цього типу немає у списку, додаємо його
        if (!pieceExists) {
            availablePiecesList.add(piece);
        }

        // Збільшуємо кількість доступних блоків цього типу
        String pieceName = piece.getClass().getSimpleName();
        availablePiecesCount.put(pieceName, availablePiecesCount.getOrDefault(pieceName, 0) + 1);
    }
}
