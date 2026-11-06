package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;

import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;


import java.awt.*;

public class Piece3x1 extends Piece {
    public Piece3x1() {
        squares = new Point[3];
        squares[0] = new Point(0, 0); // Початкова точка
        squares[1] = new Point(1, 0); // Нижче початкової
        squares[2] = new Point(2, 0); // Ще нижче
    }
}
