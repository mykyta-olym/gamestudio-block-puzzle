package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;
import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;


import java.awt.Point;
public class Piece2x1 extends Piece {
    public Piece2x1() {
        squares = new Point[2];
        squares[0] = new Point(0, 0); // Початкова точка зверху
        squares[1] = new Point(1, 0); // Друга клітинка нижче
    }
}
