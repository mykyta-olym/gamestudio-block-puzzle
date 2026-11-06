package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;

import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;


import java.awt.Point;

public class Piece1x5 extends Piece {
    public Piece1x5() {
        squares = new Point[5];
        squares[0] = new Point(0, 0); // Початкова точка
        squares[1] = new Point(0, 1);
        squares[2] = new Point(0, 2);
        squares[3] = new Point(0, 3);
        squares[4] = new Point(0, 4);
    }
}


