package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;

import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;

import java.awt.*;

public class Piece1x1 extends Piece {
    // Форма фігури (X позначає початкову точку)
    //
    // [X]

    public Piece1x1() {
        squares = new Point[1];
        squares[0] = new Point(0, 0);
    }
}
