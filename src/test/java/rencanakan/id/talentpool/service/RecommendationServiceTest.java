package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.repository.RecommendationRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private Recommendation recommendation;
    private RecommendationRequestDTO request;
    private RecommendationResponseDTO response;

    @BeforeEach
    void setUp() {
        request = createRecommendationRequestDTO();
        response= createRecommendationResponseDTO();
        recommendation = createRecommendation();
    }

    Recommendation createRecommendation() {
        return Recommendation.builder()
                .message("bawa pesan ini, ke perseketuanmu")
                .build();
    }

    RecommendationRequestDTO createRecommendationRequestDTO() {
        return RecommendationRequestDTO.builder()
                .message("hehe")
                .build();
    }

    RecommendationResponseDTO createRecommendationResponseDTO() {
        return RecommendationResponseDTO.builder()
                .message("keren")
                .build();
    }

    @Nested
    class EditExperienceTest {
        @Test
        void testEditById_Success() {
            Long recommendationId = 1L;

            Recommendation newRecommendation = createRecommendation();
            newRecommendation.setMessage("ini pesan baru");

            when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(recommendation));
            when(recommendationRepository.save(any(Recommendation.class))).thenReturn(newRecommendation);

            RecommendationResponseDTO response = recommendationService.editById(recommendationId, request);

            assertNotNull(response);
            assertEquals("ini pesan baru", response.getMessage());

            verify(recommendationRepository, times(1)).findById(recommendationId);
            verify(recommendationRepository, times(1)).save(any(Recommendation.class));
        }

        @Test
        void testEditById_EntityNotFound() {

            Long nonExistentId = 999L;
            when(recommendationRepository.findById(nonExistentId)).thenReturn(Optional.empty());


            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                recommendationService.editById(nonExistentId, request);
            });

            assertEquals("Experience with ID 999 not found", exception.getMessage());

            verify(recommendationRepository, times(1)).findById(nonExistentId);
            verify(recommendationRepository, never()).save(any(Recommendation.class));
        }
    }
}
