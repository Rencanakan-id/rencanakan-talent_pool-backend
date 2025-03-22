package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.repository.ExperienceRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private ExperienceServiceImpl experienceService;
    
    private Experience experience;

    @BeforeEach
    void setUp() {
        experience = createValidExperienceModel();
    }

    private Experience createValidExperienceModel() {
        return Experience.builder()
            .id(1L)
            .title("Old Title")
            .company("Old Company")
            .employmentType(EmploymentType.PART_TIME)
            .startDate(LocalDate.of(2018, 1, 1))
            .endDate(LocalDate.of(2020, 12, 31))
            .location("Old Location")
            .locationType(LocationType.HYBRID)
            .build();
    }
    
    @Nested
    class DeleteExperienceTest {
        
        @Test
        void testDeleteById_Success() {
            // Arrange
            Long experienceId = 1L;
            // Explicitly set up the mock behavior for this specific test
            when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
            doNothing().when(experienceRepository).delete(any(Experience.class));

            // Act
            experienceService.deleteById(experienceId);

            // Assert
            verify(experienceRepository).findById(experienceId);
            verify(experienceRepository).delete(experience); // Use the exact object for better verification
        }

        @Test
        void testDeleteById_EntityNotFound() {
            // Arrange
            Long nonExistentId = 999L;
            
            doReturn(Optional.empty()).when(experienceRepository).findById(nonExistentId);

            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                experienceService.deleteById(nonExistentId);
            });

            // Verify the findById method was called with the correct argument
            verify(experienceRepository, times(1)).findById(nonExistentId);
            
            assertEquals("Experience with ID 999 not found", exception.getMessage());
            assertTrue(exception.getMessage().contains("not found"));
            
            verifyNoMoreInteractions(experienceRepository);
        }

        @Test
        void testDeleteById_WithNullId() {
            // Arrange
            
            // Act & Assert
            assertThrows(EntityNotFoundException.class, () -> {
                experienceService.deleteById(null);
            });
        }

        @Test
        void testDeleteById_ThrowsRepositoryException() {
            // Arrange
            Long experienceId = 1L;
            when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
            doThrow(new RuntimeException("Database error")).when(experienceRepository).delete(any(Experience.class));

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () -> {
                experienceService.deleteById(experienceId);
            });

            assertEquals("Database error", exception.getMessage());
            verify(experienceRepository).findById(experienceId);
            verify(experienceRepository).delete(any(Experience.class));
        }
    }
}