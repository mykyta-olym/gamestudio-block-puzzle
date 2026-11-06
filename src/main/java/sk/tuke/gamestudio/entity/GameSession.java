package sk.tuke.gamestudio.entity;

import javax.persistence.*;

@Entity
@Table(name = "game_sessions")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "username")
    private String username;

    @Column(name = "grid_state")
    private String gridState;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "total_coins")
    private Integer totalCoins;

    @Column(name = "game_time")
    private Integer gameTime;

    @Column(name = "hints_remaining")
    private Integer hintsRemaining;

    @Column(name = "current_combo_count")
    private Integer currentComboCount;

    @Column(name = "hard_mode")
    private Boolean hardMode;

    @Column(name = "next_piece_type")
    private String nextPieceType;

    @Column(name = "available_pieces")
    private String availablePieces;

    @Column(name = "rows_cleared")
    private Integer rowsCleared;

    @Column(name = "cols_cleared")
    private Integer colsCleared;

    @Column(name = "speed_bonus")
    private Integer speedBonus;

    public GameSession() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGridState() {
        return gridState;
    }

    public void setGridState(String gridState) {
        this.gridState = gridState;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(Integer totalCoins) {
        this.totalCoins = totalCoins;
    }

    public Integer getGameTime() {
        return gameTime;
    }

    public void setGameTime(Integer gameTime) {
        this.gameTime = gameTime;
    }


    public Integer getCurrentComboCount() {
        return currentComboCount;
    }

    public void setCurrentComboCount(Integer currentComboCount) {
        this.currentComboCount = currentComboCount;
    }

    public Boolean getHardMode() {
        return hardMode;
    }

    public void setHardMode(Boolean hardMode) {
        this.hardMode = hardMode;
    }

    public String getNextPieceType() {
        return nextPieceType;
    }

    public void setNextPieceType(String nextPieceType) {
        this.nextPieceType = nextPieceType;
    }

    public String getAvailablePieces() {
        return availablePieces;
    }

    public void setAvailablePieces(String availablePieces) {
        this.availablePieces = availablePieces;
    }

    public Integer getRowsCleared() {
        return rowsCleared;
    }

    public void setRowsCleared(Integer rowsCleared) {
        this.rowsCleared = rowsCleared;
    }

    public Integer getColsCleared() {
        return colsCleared;
    }

    public void setColsCleared(Integer colsCleared) {
        this.colsCleared = colsCleared;
    }

    public Integer getSpeedBonus() {
        return speedBonus;
    }

    public void setSpeedBonus(Integer speedBonus) {
        this.speedBonus = speedBonus;
    }
}

