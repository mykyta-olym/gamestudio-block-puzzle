package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;

import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;


import java.awt.*;

public class Piece2x2 extends Piece {
    public Piece2x2() {
        squares = new Point[4];
        squares[0] = new Point(0, 0);
        squares[1] = new Point(0, 1); // Праворуч від початкової
        squares[2] = new Point(1, 0); // Нижче початкової
        squares[3] = new Point(1, 1); // Діагонально від початкової
    }
}
