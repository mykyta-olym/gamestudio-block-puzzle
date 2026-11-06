package sk.tuke.gamestudio.game.BlockPuzzle.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.tuke.gamestudio.game.BlockPuzzle.core.GameModes.ClassicMode;
import sk.tuke.gamestudio.game.BlockPuzzle.core.pieces.Piece1x1;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {
    private ClassicMode gameMode;

    @BeforeEach
    void setUp() {
        gameMode.initializeGrid();
    }

    @Test
    void testGridInitialization() throws NoSuchFieldException, IllegalAccessException {
        gameMode.initializeGrid();

        Field gridField = ClassicMode.class.getDeclaredField("grid");
        gridField.setAccessible(true);

        Tile[][] grid = (Tile[][]) gridField.get(gameMode);

        assertNotNull(grid, "Grid should be initialized.");

    }

    @Test
    void testPlacePieceOnEmptyGrid() throws NoSuchFieldException, IllegalAccessException {
        Piece piece = new Piece1x1();
        assertTrue(gameMode.placePiece(0, 0, piece));

        Tile[][] grid = getGridUsingReflection();
        assertTrue(grid[0][0].isUsed());
    }

    @Test
    void testPlacePieceOnOccupiedTile() {
        Piece piece1 = new Piece1x1();
        Piece piece2 = new Piece1x1();
        gameMode.placePiece(0, 0, piece1);
        assertFalse(gameMode.placePiece(0, 0, piece2));
    }

    @Test
    void testIsGridFull() {
        for (int i = 0; i < ClassicMode.GRID_SIZE; i++) {
            for (int j = 0; j < ClassicMode.GRID_SIZE; j++) {
                Piece piece = new Piece1x1();
                gameMode.placePiece(i, j, piece);
            }
        }
        assertTrue(gameMode.isGridFull());
    }

    @Test
    void testRemovePieceFromGrid() throws NoSuchFieldException, IllegalAccessException {
        Piece piece = new Piece1x1();
        gameMode.placePiece(0, 0, piece);
        gameMode.removePieceFromGrid(piece);

        // Reflection to check if the tile is no longer used
        Tile[][] grid = getGridUsingReflection();
        assertFalse(grid[0][0].isUsed());
    }

    // Utility method to access the grid using reflection
    private Tile[][] getGridUsingReflection() throws NoSuchFieldException, IllegalAccessException {
        Field gridField = ClassicMode.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        return (Tile[][]) gridField.get(gameMode);
    }

    @Test
    void testCannotPlacePieceOutsideGrid() {
        Piece piece = new Piece1x1();
        assertFalse(gameMode.placePiece(ClassicMode.GRID_SIZE, ClassicMode.GRID_SIZE, piece), "Piece cannot be placed outside the grid.");
    }
    @Test
    void testPlacePieceNearGridEdge() {
        Piece piece = new Piece1x1();
        assertTrue(gameMode.placePiece(ClassicMode.GRID_SIZE - 1, ClassicMode.GRID_SIZE - 1, piece), "Piece should be placed near the grid edge.");
    }
    @Test
    void testGridFullCondition() {
        // Fill up the entire grid except for one tile
        for (int i = 0; i < ClassicMode.GRID_SIZE; i++) {
            for (int j = 0; j < ClassicMode.GRID_SIZE; j++) {
                if (!(i == ClassicMode.GRID_SIZE - 1 && j == ClassicMode.GRID_SIZE - 1)) { // Leave last tile empty
                    Piece piece = new Piece1x1();
                    assertTrue(gameMode.placePiece(i, j, piece), "Should be able to place piece at " + i + ", " + j);
                }
            }
        }
        assertFalse(gameMode.isGridFull(), "Grid should not be considered full yet with one tile empty.");

        // Place the last piece to fill the grid completely
        Piece lastPiece = new Piece1x1();
        assertTrue(gameMode.placePiece(ClassicMode.GRID_SIZE - 1, ClassicMode.GRID_SIZE - 1, lastPiece), "Should be able to place the last piece.");
        assertTrue(gameMode.isGridFull(), "Grid should be considered full now.");
    }

    @Test
    void testValidPiecePlacement() {
        Piece piece = new Piece1x1();
        boolean placedSuccessfully = gameMode.placePiece(0, 0, piece);
        assertTrue(placedSuccessfully, "Should be able to place a piece at the beginning of the grid.");
    }

    @Test
    void testInvalidPiecePlacementOutsideGrid() {
        Piece piece = new Piece1x1();
        boolean placedSuccessfully = gameMode.placePiece(ClassicMode.GRID_SIZE, ClassicMode.GRID_SIZE, piece);
        assertFalse(placedSuccessfully, "Should not be able to place a piece outside of the grid.");
    }}
