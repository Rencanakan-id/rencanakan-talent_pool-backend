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
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.dto.RecommendationStatusRequestDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.Recommendation;
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
        recommendation.setStatus(rencanakan.id.talentpool.enums.StatusType.PENDING);
    }

    @Test
    void testEditStatusById_Success() {
        RecommendationStatusRequestDTO requestDTO = new RecommendationStatusRequestDTO(StatusType.ACCEPTED);

        when(recommendationRepository.findById("1")).thenReturn(java.util.Optional.of(recommendation));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        RecommendationResponseDTO response = recommendationService.editStatusById("1", requestDTO);

        assertNotNull(response);
        assertEquals(rencanakan.id.talentpool.enums.StatusType.ACCEPTED, response.getStatus());
        verify(recommendationRepository, times(1)).save(recommendation);
    }

    @Test
    void testEditStatusById_RecommendationNotFound() {
        RecommendationStatusRequestDTO requestDTO = new RecommendationStatusRequestDTO(StatusType.ACCEPTED);


        when(recommendationRepository.findById("1")).thenReturn(java.util.Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            recommendationService.editStatusById("1", requestDTO);
        });
        assertEquals("Recommendation with ID 1 not found", exception.getMessage());
    }
    @Test
    void deleteById_Success() {
        when(recommendationRepository.findById("1")).thenReturn(Optional.of(recommendation));

        RecommendationResponseDTO response = recommendationService.deleteById("1");

        verify(recommendationRepository, times(1)).deleteById("1");

        assertNotNull(response);
        assertEquals("1", response.getId());
        assertEquals(StatusType.PENDING, response.getStatus());
    }
    @Test
    void deleteById_NotFound_ThrowsException() {

        when(recommendationRepository.findById("999")).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            recommendationService.deleteById("999");
        });

        assertEquals("Recommendation with id 999 not found.", exception.getMessage());

        verify(recommendationRepository, never()).deleteById("999");
    }
}
