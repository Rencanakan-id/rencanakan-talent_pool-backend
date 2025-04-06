package rencanakan.id.talentpool.unit.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.repository.RecommendationRepository;
import rencanakan.id.talentpool.service.RecommendationServiceImpl;

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
        Recommendation updated = new Recommendation();
        updated.setId("1");
        updated.setStatus(StatusType.ACCEPTED);
        when(recommendationRepository.findById("1")).thenReturn(java.util.Optional.of(recommendation));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(updated);

        RecommendationResponseDTO response = recommendationService.editStatusById("1", StatusType.ACCEPTED  );

        assertNotNull(response);
        assertEquals(rencanakan.id.talentpool.enums.StatusType.ACCEPTED, response.getStatus());
        verify(recommendationRepository, times(1)).save(recommendation);
    }

    @Test
    void testEditStatusById_RecommendationNotFound() {
        when(recommendationRepository.findById("1")).thenReturn(java.util.Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            recommendationService.editStatusById("1", StatusType.ACCEPTED);
        });
        assertEquals("Recommendation with ID 1 not found", exception.getMessage());
    }
}
