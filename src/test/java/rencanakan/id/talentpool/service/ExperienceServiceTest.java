package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.repository.ExperienceRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private ExperienceServiceImpl experienceService;

    private Experience experience1;
    private Experience experience2;
    private List<Experience> experienceList;

    @BeforeEach
    void setUp() {
        // Initialize test data
        experience1 = Experience.builder()
                .id(1L)
                .title("Software Engineer")
                .company("Tech Company")
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(LocalDate.of(2018, 1, 1))
                .endDate(LocalDate.of(2020, 12, 31))
                .location("Jakarta")
                .locationType(LocationType.ON_SITE)
                .build();

        experience2 = Experience.builder()
                .id(2L)
                .title("Senior Developer")
                .company("Another Tech Company")
                .employmentType(EmploymentType.CONTRACT)
                .startDate(LocalDate.of(2021, 1, 1))
                .endDate(LocalDate.of(2023, 6, 30))
                .location("Surabaya")
                .locationType(LocationType.HYBRID)
                .build();

        experienceList = new ArrayList<>();
        experienceList.add(experience1);
        experienceList.add(experience2);
    }

    @Nested
    class GetByTalentIdTest {

        @Test
        void testGetByTalentId_Success() {
            // Arrange
            String talentId = "talent-123";

            // Configure the mock to return our list when findByUserId is called
            when(experienceRepository.findByUserId(talentId)).thenReturn(experienceList);

            // Create expected response DTOs
            ExperienceResponseDTO responseDTO1 = new ExperienceResponseDTO();
            ExperienceResponseDTO responseDTO2 = new ExperienceResponseDTO();

            // Configure the static DTOMapper to return our response DTOs
            // Using mockStatic would be better but requires additional setup
            // This is a simplified approach for the test
            try (var dtoMapperMock = mockStatic(DTOMapper.class)) {
                dtoMapperMock.when(() -> DTOMapper.map(experience1, ExperienceResponseDTO.class)).thenReturn(responseDTO1);
                dtoMapperMock.when(() -> DTOMapper.map(experience2, ExperienceResponseDTO.class)).thenReturn(responseDTO2);

                // Act
                List<ExperienceResponseDTO> result = experienceService.getByTalentId(talentId);

                // Assert
                assertEquals(2, result.size());
                assertSame(responseDTO1, result.get(0));
                assertSame(responseDTO2, result.get(1));

                // Verify repository was called with correct parameter
                verify(experienceRepository).findByUserId(talentId);
            }
        }

        @Test
        void testGetByTalentId_EmptyList() {
            // Arrange
            String talentId = "non-existent-talent";

            // Configure the mock to return an empty list
            when(experienceRepository.findByUserId(talentId)).thenReturn(Collections.emptyList());

            // Act
            List<ExperienceResponseDTO> result = experienceService.getByTalentId(talentId);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // Verify repository was called with correct parameter
            verify(experienceRepository).findByUserId(talentId);
        }

        @Test
        void testGetByTalentId_NullTalentId() {
            // Arrange
            String talentId = null;

            // Configure the mock for null input (if applicable)
            when(experienceRepository.findByUserId(null)).thenReturn(Collections.emptyList());

            // Act
            List<ExperienceResponseDTO> result = experienceService.getByTalentId(talentId);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // Verify repository was called with null parameter
            verify(experienceRepository).findByUserId(null);
        }

        @Test
        void testGetByTalentId_RepositoryThrowsException() {
            // Arrange
            String talentId = "talent-123";

            // Configure the mock to throw an exception
            when(experienceRepository.findByUserId(talentId)).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () -> {
                experienceService.getByTalentId(talentId);
            });

            assertEquals("Database error", exception.getMessage());

            // Verify repository was called
            verify(experienceRepository).findByUserId(talentId);
        }
    }
}