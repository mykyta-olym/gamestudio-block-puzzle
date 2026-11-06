package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;

import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;
import java.awt.*;

public class PieceSmallL extends Piece {
    public PieceSmallL() {
        squares = new Point[3]; // Зменшено до 3, оскільки потрібно три точки для форми "L"
        squares[0] = new Point(0, 0); // Перший блок у верхньому лівому куті
        squares[1] = new Point(1, 0); // Другий блок нижче першого
        squares[2] = new Point(1, 1); // Третій блок праворуч від другого, формуючи "L"
    }
}
