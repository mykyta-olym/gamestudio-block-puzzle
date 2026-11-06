package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;

import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;


import java.awt.*;

public class PieceBigL extends Piece {
    public PieceBigL() {
        squares = new Point[5];
        // Зміна координат для врахування розміщення від верхнього лівого кута
        squares[0] = new Point(0, 0);
        squares[1] = new Point(1, 0);
        squares[2] = new Point(2, 0);
        squares[3] = new Point(2, 1);
        squares[4] = new Point(2, 2);
    }
}
