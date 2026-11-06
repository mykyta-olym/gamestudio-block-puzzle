package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;

import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;

import java.awt.*;

public class PieceS extends Piece {
    public PieceS() {
        squares = new Point[4];
        // Змінено розташування для форми "S"
        squares[0] = new Point(0, 1); // Перша клітинка в першому ряду, зміщена вправо
        squares[1] = new Point(0, 2); // Друга клітинка в першому ряду, ще правіше
        squares[2] = new Point(1, 0); // Третя клітинка в другому ряду, зміщена вліво від першої
        squares[3] = new Point(1, 1); // Четверта клітинка поруч з третьою в другому ряду
    }
}
