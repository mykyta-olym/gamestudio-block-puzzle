package sk.tuke.gamestudio.game.BlockPuzzle.core;

public class Tile {
    protected int row;
    protected int col;
    protected boolean used;
    protected boolean markForDelete;
    private String pieceType;

    // Оновлений конструктор для встановлення координат та стану використання
    public Tile(int row, int col, boolean used) {
        this.row = row;
        this.col = col;
        this.used = used;
        this.markForDelete = false;
    }

    // Додатковий конструктор без параметрів для зворотної сумісності
    public Tile() {
        this(-1, -1, false); // Встановлюємо недійсні координати та не використаний стан
    }

    // Інші методи класу Tile залишаються незмінними
    public boolean isUsed() {
        return this.used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getPieceType() {
        return pieceType;
    }

    public void setPieceType(String pieceType) {
        this.pieceType = pieceType;
    }
}
