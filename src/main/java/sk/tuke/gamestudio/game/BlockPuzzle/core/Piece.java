package sk.tuke.gamestudio.game.BlockPuzzle.core;

import java.awt.*;

public class Piece {
    protected Point[] squares;
    // Added fields for the piece's position on the grid
    protected int startRow = -1;
    protected int startCol = -1;

    // Getters and setters for startRow and startCol
    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public Point[] getSquares() {
        return squares;
    }

}
