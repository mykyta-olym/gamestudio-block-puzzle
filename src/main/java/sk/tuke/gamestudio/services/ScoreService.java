package sk.tuke.gamestudio.services;

import sk.tuke.gamestudio.entity.Score;

import java.util.List;

public interface ScoreService {
    List<Score> getTopScores(String columnName) throws ScoreException;

    void addScore(Score score) throws ScoreException;
    List<Score> fetchTopScores(String criterion) throws ScoreException;
    void reset() throws ScoreException;
}
