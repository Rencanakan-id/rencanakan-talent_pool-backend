package rencanakan.id.talentPool.service;

import jakarta.validation.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import rencanakan.id.talentPool.dto.ExperienceRequestDTO;
import rencanakan.id.talentPool.enums.EmploymentType;
import rencanakan.id.talentPool.enums.LocationType;
import rencanakan.id.talentPool.model.Experience;
import rencanakan.id.talentPool.repository.ExperienceRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {
    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private Validator mockValidator;

    @InjectMocks
    private ExperienceServiceImpl experienceService;

    @Captor
    private ArgumentCaptor<Experience> experienceCaptor;

    private static Validator validator;

    private ExperienceRequestDTO createValidRequestDTO() {
        ExperienceRequestDTO dto = new ExperienceRequestDTO();
        dto.setTitle("Lead Construction Project Manager");
        dto.setCompany("Aman");
        dto.setEmploymentType(EmploymentType.FULL_TIME);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));
        dto.setLocation("Depok");
        dto.setLocationType(LocationType.ON_SITE);
        dto.setTalentId(1L);
        return dto;
    }

    private Experience createMockExperience() {
        return Experience.builder()
                .title("Lead Construction Project Manager")
                .company("Aman")
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .location("Depok")
                .locationType(LocationType.ON_SITE)
                .talentId(1L)
                .build();
    }

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Create Experience Tests")
    class CreateExperienceTests {

        @Test
        @DisplayName("Should create and save experience with valid data")
        void createExperience_AllValid() {
            ExperienceRequestDTO request = createValidRequestDTO();
            Experience mockSavedExperience = createMockExperience();

            when(experienceRepository.save(any(Experience.class))).thenReturn(mockSavedExperience);

            Experience result = experienceService.createExperience(request);

            verify(experienceRepository).save(experienceCaptor.capture());
            Experience capturedExperience = experienceCaptor.getValue();

            assertEquals(request.getTitle(), capturedExperience.getTitle());
            assertEquals(request.getCompany(), capturedExperience.getCompany());
            assertEquals(request.getEmploymentType(), capturedExperience.getEmploymentType());
            assertEquals(request.getStartDate(), capturedExperience.getStartDate());
            assertEquals(request.getEndDate(), capturedExperience.getEndDate());
            assertEquals(request.getLocation(), capturedExperience.getLocation());
            assertEquals(request.getLocationType(), capturedExperience.getLocationType());
            assertEquals(request.getTalentId(), capturedExperience.getTalentId());

            assertEquals(result, mockSavedExperience);
        }

        @Test
        @DisplayName("Should create experience with null end date")
        void createExperience_NullEndDate() {
            ExperienceRequestDTO request = createValidRequestDTO();
            request.setEndDate(null);
            Experience mockSavedExperience = createMockExperience();
            mockSavedExperience.setEndDate(null);

            when(experienceRepository.save(any(Experience.class))).thenReturn(mockSavedExperience);

            Experience result = experienceService.createExperience(request);

            verify(experienceRepository).save(experienceCaptor.capture());
            Experience capturedExperience = experienceCaptor.getValue();

            assertNull(capturedExperience.getEndDate());
            assertNull(result.getEndDate());
        }
        @Test
        @DisplayName("Should create experience with maximum character limits")
        void createExperience_MaxCharLimit() {
            ExperienceRequestDTO request = createValidRequestDTO();
            String maxAllowedChars = "A".repeat(50);
            request.setTitle(maxAllowedChars);
            request.setCompany(maxAllowedChars);
            request.setLocation(maxAllowedChars);

            Experience mockSavedExperience = createMockExperience();
            mockSavedExperience.setTitle(maxAllowedChars);
            mockSavedExperience.setCompany(maxAllowedChars);
            mockSavedExperience.setLocation(maxAllowedChars);
            when(experienceRepository.save(any(Experience.class))).thenReturn(mockSavedExperience);

            Experience result = experienceService.createExperience(request);

            verify(experienceRepository).save(experienceCaptor.capture());
            Experience capturedExperience = experienceCaptor.getValue();

            assertEquals(maxAllowedChars, capturedExperience.getTitle(), "Title should match the maximum allowed characters");
            assertEquals(maxAllowedChars, capturedExperience.getCompany(), "Company should match the maximum allowed characters");
            assertEquals(maxAllowedChars, capturedExperience.getLocation(), "Location should match the maximum allowed characters");
            assertEquals(mockSavedExperience, result, "The returned experience should match the mocked saved experience");
        }

        @Test
        @DisplayName("Should reject experience with invalid/missing data")
        void createExperience_MissingValue() {
            ExperienceRequestDTO request = new ExperienceRequestDTO();

            when(mockValidator.validate(request)).thenReturn(validator.validate(request));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                experienceService.createExperience(request);
            });

            assertEquals("Validation failed", exception.getMessage());
            verify(experienceRepository, never()).save(any(Experience.class));
        }

        @Test
        @DisplayName("Should reject experience with exceeding characters limit")
        void createExperience_ExceedingCharLimit() {
            ExperienceRequestDTO request = createValidRequestDTO();
            request.setTitle("A".repeat(51));
            request.setCompany("A".repeat(51));
            request.setLocation("A".repeat(51));

            when(mockValidator.validate(request)).thenReturn(validator.validate(request));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                experienceService.createExperience(request);
            });

            assertEquals("Validation failed", exception.getMessage());
            verify(experienceRepository, never()).save(any(Experience.class));
        }
    }
}
