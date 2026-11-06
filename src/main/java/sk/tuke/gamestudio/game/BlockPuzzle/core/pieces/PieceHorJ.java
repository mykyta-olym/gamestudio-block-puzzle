package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;

import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;

import java.awt.*;

public class PieceHorJ extends Piece {
    public PieceHorJ() {
        squares = new Point[4];
        // Оновлення координат для форми "J"
        squares[0] = new Point(0, 1);  // Верхня частина "J"
        squares[1] = new Point(1, 1);  // Середина "J"
        squares[2] = new Point(2, 1);  // Нижня частина "J"
        squares[3] = new Point(2, 0);  // "Хвостик" "J" розташований зліва від нижньої частини
    }
}
