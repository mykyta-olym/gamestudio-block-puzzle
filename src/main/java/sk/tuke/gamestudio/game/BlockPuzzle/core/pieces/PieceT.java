package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;
import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;


import java.awt.*;

public class PieceT extends Piece {
    public PieceT() {
        squares = new Point[4];
        squares[0] = new Point(0, 0); // Ліва клітинка
        squares[1] = new Point(0, 1); // Центральна верхня клітинка
        squares[2] = new Point(0, 2); // Права клітинка
        squares[3] = new Point(1, 1); // Клітинка нижче центру
    }
}

