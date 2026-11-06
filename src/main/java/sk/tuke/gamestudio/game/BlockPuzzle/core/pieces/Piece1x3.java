package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;

import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;


import java.awt.*;

public class Piece1x3 extends Piece {
    public Piece1x3() {
        squares = new Point[3];
        squares[0] = new Point(0, 0); // Початкова точка
        squares[1] = new Point(0, 1); // Друга клітинка праворуч
        squares[2] = new Point(0, 2); // Третя клітинка праворуч
    }
}
