package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;

import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;

import java.awt.*;

public class Piece1x2 extends Piece {
    // Форма фігури (X позначає початкову точку)
    //
    // [X][ ]

    public Piece1x2() {
        squares = new Point[2];
        squares[0] = new Point(0, 0);
        squares[1] = new Point(0, 1);
    }
}
