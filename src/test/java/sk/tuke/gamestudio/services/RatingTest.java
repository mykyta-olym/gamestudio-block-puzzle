package sk.tuke.gamestudio.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RatingTest {
    @InjectMocks
    private RatingServiceJPA ratingService;

    @Mock
    private EntityManager entityManagerMock;

    @BeforeEach
    void setUp() {
        entityManagerMock = mock(EntityManager.class);
    }

    @Test
    void getNonExistingRating() {
        String game = "BlockPuzzle";
        String nonExistingUser = "nonExistingUser";

        int expectedDefaultRating = 0;

        TypedQuery<Integer> queryMock = mock(TypedQuery.class);
        when(entityManagerMock.createQuery(anyString(), eq(Integer.class))).thenReturn(queryMock);
        when(queryMock.setParameter(anyString(), any())).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(null); // simulate no result found

        int actualRating = ratingService.getRating(game, nonExistingUser);
        assertEquals(expectedDefaultRating, actualRating, "The retrieved rating for a non-existing user should match the default value.");
    }

    @Test
    void getAverageRatingForGame() {
        String game = "BlockPuzzle";
        TypedQuery<Double> queryMock = mock(TypedQuery.class);
        when(entityManagerMock.createQuery(anyString(), eq(Double.class))).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(5.0); // simulate an average rating

        int averageRating = ratingService.getAverageRating(game);
        assertTrue(averageRating > 0, "Average rating should be positive.");
    }

    @Test
    void getRatingForNonExistentGameReturnsZero() {
        String nonExistentGame = "NonExistentGame";
        String username = "testUser";

        TypedQuery<Integer> queryMock = mock(TypedQuery.class);
        when(entityManagerMock.createQuery(anyString(), eq(Integer.class))).thenReturn(queryMock);
        when(queryMock.setParameter(anyString(), any())).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(null); // simulate no result found

        int rating = ratingService.getRating(nonExistentGame, username);
        assertEquals(0, rating, "Rating for a non-existent game should be zero.");
    }
}
