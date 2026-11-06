package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;

import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;


import java.awt.*;


public class Piece1x4 extends Piece {
    public Piece1x4() {
        squares = new Point[4];
        squares[0] = new Point(0, 0); // Початкова точка
        squares[1] = new Point(0, 1); // і так далі
        squares[2] = new Point(0, 2);
        squares[3] = new Point(0, 3);
    }
}
