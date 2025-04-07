package rencanakan.id.talentpool.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.RecommendationRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private Recommendation recommendation;

    @BeforeEach
    void setUp() {
        recommendation = new Recommendation();
        recommendation.setId("1");
        recommendation.setStatus(StatusType.PENDING);
    }

    @Test
    void testEditStatusById_Success() {
        // Arrange
        Recommendation updated = new Recommendation();
        updated.setId("1");
        updated.setStatus(StatusType.ACCEPTED);


        User mockedTalent = mock(User.class);
        when(mockedTalent.getId()).thenReturn("idUser");

        recommendation.setTalent(mockedTalent);

        when(recommendationRepository.findById("1")).thenReturn(Optional.of(recommendation));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(updated);

        // Act
        RecommendationResponseDTO response = recommendationService.editStatusById("idUser", "1", StatusType.ACCEPTED);

        // Assert
        assertNotNull(response);
        assertEquals(StatusType.ACCEPTED, response.getStatus());
        verify(recommendationRepository, times(1)).save(recommendation);
    }

    @Test
    void testEditStatusById_RecommendationNotFound() {
        // Arrange
        when(recommendationRepository.findById("1")).thenReturn(Optional.empty());

        // Act + Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                recommendationService.editStatusById( "idUser","1", StatusType.ACCEPTED)
        );

        assertEquals("Recommendation with ID 1 not found", exception.getMessage());
    }


    @Test
    void testEditStatusById_UnAuthorized() {
        // Arrange
        User mockedTalent = mock(User.class);
        when(mockedTalent.getId()).thenReturn("idUser");

        recommendation.setTalent(mockedTalent);

        when(recommendationRepository.findById("1")).thenReturn(Optional.of(recommendation));

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            recommendationService.editStatusById("idUser123", "1", StatusType.ACCEPTED);
        });

        assertEquals("You are not allowed to edit this experience.", exception.getMessage());
        verify(recommendationRepository, never()).save(any());
    }

}
