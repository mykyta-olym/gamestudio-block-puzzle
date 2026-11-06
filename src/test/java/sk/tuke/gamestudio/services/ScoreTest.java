package sk.tuke.gamestudio.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sk.tuke.gamestudio.entity.Score;

import javax.persistence.EntityManager;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ScoreTest {

    @InjectMocks
    private ScoreServiceJPA service;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Setup mock behavior if necessary
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());
    }

    @Test
    void testReset() {
        Score score = new Score("player1", 500, 100, 2, 10, 1);
        service.addScore(score);
        service.reset();

        // After reset, we expect to find no data
        assertTrue(service.getTopScores("total_experience").isEmpty());
    }

    @Mock
    private javax.persistence.Query query;
}
