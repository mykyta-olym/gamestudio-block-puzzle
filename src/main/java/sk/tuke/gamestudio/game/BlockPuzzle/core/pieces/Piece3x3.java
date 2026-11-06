package sk.tuke.gamestudio.game.BlockPuzzle.core.pieces;

import sk.tuke.gamestudio.game.BlockPuzzle.core.Piece;


import java.awt.*;

public class Piece3x3 extends Piece {
    public Piece3x3() {
        squares = new Point[9];
        int index = 0;
        // Змінено координати для відношення від верхнього лівого кута
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                squares[index++] = new Point(i, j);
            }
        }
    }
}
