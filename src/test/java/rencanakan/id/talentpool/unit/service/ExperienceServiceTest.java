package rencanakan.id.talentpool.unit.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.ExperienceRepository;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;
import rencanakan.id.talentpool.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {
    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExperienceServiceImpl experienceService;
    private ExperienceResponseDTO response;
    private  ExperienceRequestDTO request;
    private Experience experience;
    private User user;
    private ExperienceRequestDTO experienceRequestDTO;
    private UserResponseDTO userResponseDTO;
    private ExperienceResponseDTO experienceResponseDTO;
    private static Validator validator;

    @BeforeEach
    void setUp() {
        user = createUser();
        userResponseDTO = createUserResponseDTO();
        experience = createExperience();
        experienceRequestDTO = createExperienceRequestDTO();
        experienceResponseDTO = createExperienceResponseDTO();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = createExperienceRequestDTO();
        response= createExperienceResponseDTO();

    }

    private User createUser() {
        return User.builder()
                .id("1")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
    }

    private UserResponseDTO createUserResponseDTO() {
        return UserResponseDTO.builder()
                .id("1")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .build();
    }

    private Experience createExperience() {
        return Experience.builder()
                .id(1L)
                .title("Software Engineer")
                .company("TechCorp")
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(LocalDate.of(2020, 5, 1))
                .endDate(LocalDate.of(2022, 5, 1))
                .location("San Francisco")
                .locationType(LocationType.ON_SITE)
                .user(user)
                .build();
    }

    private ExperienceRequestDTO createExperienceRequestDTO() {
        return ExperienceRequestDTO.builder()
                .title("Software Engineer")
                .company("TechCorp")
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(LocalDate.of(2020, 5, 1))
                .endDate(LocalDate.of(2022, 5, 1))
                .location("San Francisco")
                .locationType(LocationType.ON_SITE)
                .userId("1")
                .build();
    }

    private ExperienceResponseDTO createExperienceResponseDTO() {
        return ExperienceResponseDTO.builder()
                .id(1L)
                .title("Software Engineer")
                .company("TechCorp")
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(LocalDate.of(2020, 5, 1))
                .endDate(LocalDate.of(2022, 5, 1))
                .location("San Francisco")
                .locationType(LocationType.ON_SITE)
                .build();
    }

    @Test
    void testCreateExperience_Success() {
        try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
            mockedStatic.when(() -> DTOMapper.map(userResponseDTO, User.class)).thenReturn(user);
            mockedStatic.when(() -> DTOMapper.map(experienceRequestDTO, Experience.class)).thenReturn(experience);
            mockedStatic.when(() -> DTOMapper.map(experience, ExperienceResponseDTO.class)).thenReturn(experienceResponseDTO);
            when(experienceRepository.save(any(Experience.class))).thenReturn(experience);

            ExperienceResponseDTO result = experienceService.createExperience(user.getId(), experienceRequestDTO);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Software Engineer", result.getTitle());
            assertEquals("TechCorp", result.getCompany());
            assertEquals(EmploymentType.FULL_TIME, result.getEmploymentType());
        }
    }

    @Test
    void testCreateExperience_WithNullEndDate_Success() {
        try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
            experienceRequestDTO.setEndDate(null);
            experience.setEndDate(null);
            experienceResponseDTO.setEndDate(null);

            mockedStatic.when(() -> DTOMapper.map(userResponseDTO, User.class)).thenReturn(user);
            mockedStatic.when(() -> DTOMapper.map(experienceRequestDTO, Experience.class)).thenReturn(experience);
            mockedStatic.when(() -> DTOMapper.map(experience, ExperienceResponseDTO.class)).thenReturn(experienceResponseDTO);
            when(experienceRepository.save(any(Experience.class))).thenReturn(experience);

            ExperienceResponseDTO result = experienceService.createExperience(user.getId(), experienceRequestDTO);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Software Engineer", result.getTitle());
            assertNull(result.getEndDate());
        }
    }

    @Test
    void testCreateExperience_WithMaxLengthFields() {
        try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
            String maxLengthString = "A".repeat(100);
            experienceRequestDTO.setTitle(maxLengthString);
            experienceRequestDTO.setCompany(maxLengthString);
            experienceRequestDTO.setLocation(maxLengthString);

            Set<ConstraintViolation<ExperienceRequestDTO>> violations = validator.validate(experienceRequestDTO);
            assertFalse(violations.isEmpty());
        }
    }

    @Test
    void testCreateExperience_WithDifferentLocationTypes() {
        try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
            for (LocationType locationType : LocationType.values()) {
                experienceRequestDTO.setLocationType(locationType);

                Experience modifiedExperience = createModifiedExperienceWithLocationType(locationType);
                ExperienceResponseDTO modifiedResponse = createModifiedResponseWithLocationType(locationType);

                when(userService.getById("1")).thenReturn(userResponseDTO);
                mockedStatic.when(() -> DTOMapper.map(userResponseDTO, User.class)).thenReturn(user);
                mockedStatic.when(() -> DTOMapper.map(experienceRequestDTO, Experience.class)).thenReturn(modifiedExperience);
                mockedStatic.when(() -> DTOMapper.map(modifiedExperience, ExperienceResponseDTO.class)).thenReturn(modifiedResponse);
                when(experienceRepository.save(any(Experience.class))).thenReturn(modifiedExperience);

                ExperienceResponseDTO result = experienceService.createExperience(user.getId(), experienceRequestDTO);

                assertNotNull(result);
                assertEquals(locationType, result.getLocationType());

                reset(userService, experienceRepository);
            }
        }
    }

    private Experience createModifiedExperienceWithLocationType(LocationType locationType) {
        return Experience.builder()
                .id(experience.getId())
                .title(experience.getTitle())
                .company(experience.getCompany())
                .employmentType(experience.getEmploymentType())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .location(experience.getLocation())
                .locationType(locationType)
                .user(experience.getUser())
                .build();
    }

    private ExperienceResponseDTO createModifiedResponseWithLocationType(LocationType locationType) {
        return ExperienceResponseDTO.builder()
                .id(experienceResponseDTO.getId())
                .title(experienceResponseDTO.getTitle())
                .company(experienceResponseDTO.getCompany())
                .employmentType(experienceResponseDTO.getEmploymentType())
                .startDate(experienceResponseDTO.getStartDate())
                .endDate(experienceResponseDTO.getEndDate())
                .location(experienceResponseDTO.getLocation())
                .locationType(locationType)
                .build();
    }

    @Test
    void testCreateExperience_WithDifferentEmploymentTypes() {
        try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
            for (EmploymentType employmentType : EmploymentType.values()) {
                experienceRequestDTO.setEmploymentType(employmentType);

                Experience modifiedExperience = createModifiedExperienceWithEmploymentType(employmentType);
                ExperienceResponseDTO modifiedResponse = createModifiedResponseWithEmploymentType(employmentType);

                when(userService.getById("1")).thenReturn(userResponseDTO);
                mockedStatic.when(() -> DTOMapper.map(userResponseDTO, User.class)).thenReturn(user);
                mockedStatic.when(() -> DTOMapper.map(experienceRequestDTO, Experience.class)).thenReturn(modifiedExperience);
                mockedStatic.when(() -> DTOMapper.map(modifiedExperience, ExperienceResponseDTO.class)).thenReturn(modifiedResponse);
                when(experienceRepository.save(any(Experience.class))).thenReturn(modifiedExperience);

                ExperienceResponseDTO result = experienceService.createExperience(user.getId(), experienceRequestDTO);

                assertNotNull(result);
                assertEquals(employmentType, result.getEmploymentType());

                reset(userService, experienceRepository);
            }
        }
    }

    private Experience createModifiedExperienceWithEmploymentType(EmploymentType employmentType) {
        return Experience.builder()
                .id(experience.getId())
                .title(experience.getTitle())
                .company(experience.getCompany())
                .employmentType(employmentType)
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .location(experience.getLocation())
                .locationType(experience.getLocationType())
                .user(experience.getUser())
                .build();
    }

    private ExperienceResponseDTO createModifiedResponseWithEmploymentType(EmploymentType employmentType) {
        return ExperienceResponseDTO.builder()
                .id(experienceResponseDTO.getId())
                .title(experienceResponseDTO.getTitle())
                .company(experienceResponseDTO.getCompany())
                .employmentType(employmentType)
                .startDate(experienceResponseDTO.getStartDate())
                .endDate(experienceResponseDTO.getEndDate())
                .location(experienceResponseDTO.getLocation())
                .locationType(experienceResponseDTO.getLocationType())
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

    @Nested
    class GetByTalentIdTest {

        @Test
        void testGetByTalentId_Success() {
            // Arrange
            String userId = "1";
            List<Experience> experienceList = List.of(experience);

            try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
                when(experienceRepository.findByUserId(userId)).thenReturn(experienceList);
                mockedStatic.when(() -> DTOMapper.map(experience, ExperienceResponseDTO.class))
                        .thenReturn(experienceResponseDTO);

                // Act
                List<ExperienceResponseDTO> result = experienceService.getByTalentId(userId);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals(experienceResponseDTO.getId(), result.get(0).getId());
                assertEquals(experienceResponseDTO.getTitle(), result.get(0).getTitle());
                assertEquals(experienceResponseDTO.getCompany(), result.get(0).getCompany());

                // Verify
                verify(experienceRepository).findByUserId(userId);
            }
        }

        @Test
        void testGetByTalentId_MultipleExperiences() {
            // Arrange
            String userId = "1";

            // Create a second experience with different values
            Experience secondExperience = Experience.builder()
                    .id(2L)
                    .title("Senior Developer")
                    .company("Another Corp")
                    .employmentType(EmploymentType.CONTRACT)
                    .startDate(LocalDate.of(2022, 6, 1))
                    .endDate(LocalDate.of(2023, 12, 31))
                    .location("New York")
                    .locationType(LocationType.HYBRID)
                    .user(user)
                    .build();

            ExperienceResponseDTO secondResponseDTO = ExperienceResponseDTO.builder()
                    .id(2L)
                    .title("Senior Developer")
                    .company("Another Corp")
                    .employmentType(EmploymentType.CONTRACT)
                    .startDate(LocalDate.of(2022, 6, 1))
                    .endDate(LocalDate.of(2023, 12, 31))
                    .location("New York")
                    .locationType(LocationType.HYBRID)
                    .build();

            List<Experience> experienceList = List.of(experience, secondExperience);

            try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
                when(experienceRepository.findByUserId(userId)).thenReturn(experienceList);

                // Configure DTOMapper to map each experience to its corresponding DTO
                mockedStatic.when(() -> DTOMapper.map(experience, ExperienceResponseDTO.class))
                        .thenReturn(experienceResponseDTO);
                mockedStatic.when(() -> DTOMapper.map(secondExperience, ExperienceResponseDTO.class))
                        .thenReturn(secondResponseDTO);

                // Act
                List<ExperienceResponseDTO> result = experienceService.getByTalentId(userId);

                // Assert
                assertNotNull(result);
                assertEquals(2, result.size());

                // Verify first result
                assertEquals(experienceResponseDTO.getId(), result.get(0).getId());
                assertEquals(experienceResponseDTO.getTitle(), result.get(0).getTitle());
                assertEquals(experienceResponseDTO.getCompany(), result.get(0).getCompany());

                // Verify second result
                assertEquals(secondResponseDTO.getId(), result.get(1).getId());
                assertEquals(secondResponseDTO.getTitle(), result.get(1).getTitle());
                assertEquals(secondResponseDTO.getCompany(), result.get(1).getCompany());
                assertEquals(secondResponseDTO.getEmploymentType(), result.get(1).getEmploymentType());

                // Verify
                verify(experienceRepository).findByUserId(userId);
            }
        }

        @Test
        void testGetByTalentId_EmptyResult() {
            // Arrange
            String userId = "1";
            List<Experience> emptyList = List.of();

            when(experienceRepository.findByUserId(userId)).thenReturn(emptyList);

            // Act
            List<ExperienceResponseDTO> result = experienceService.getByTalentId(userId);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // Verify
            verify(experienceRepository).findByUserId(userId);
            verifyNoMoreInteractions(experienceRepository);
        }

        @Test
        void testGetByTalentId_WithNullId() {
            // Arrange
            String nullUserId = null;

            // We expect the repository to be called with null and return an empty list
            // This tests the behavior of the service when null is passed
            when(experienceRepository.findByUserId(nullUserId)).thenReturn(List.of());

            // Act
            List<ExperienceResponseDTO> result = experienceService.getByTalentId(nullUserId);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // Verify
            verify(experienceRepository).findByUserId(nullUserId);
        }

        @Test
        void testGetByTalentId_RepositoryThrowsException() {
            // Arrange
            String userId = "1";
            when(experienceRepository.findByUserId(userId)).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () -> {
                experienceService.getByTalentId(userId);
            });

            assertEquals("Database error", exception.getMessage());
            verify(experienceRepository).findByUserId(userId);
        }

        @Test
        void testGetByTalentId_NonExistentUser() {
            // Arrange
            String nonExistentUserId = "999";

            // Even if user doesn't exist, the repository should return an empty list
            when(experienceRepository.findByUserId(nonExistentUserId)).thenReturn(List.of());

            // Act
            List<ExperienceResponseDTO> result = experienceService.getByTalentId(nonExistentUserId);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // Verify
            verify(experienceRepository).findByUserId(nonExistentUserId);
        }
        @Nested
        class EditExperienceTest {
            @Test
            void testEditById_Success() {
                // Arrange
                Long experienceId = 1L;
                String userId = "userId";

                User mockedTalent = mock(User.class);
                when(mockedTalent.getId()).thenReturn("userId");
                experience.setUser(mockedTalent);
                when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));

                Experience updatedExperience = Experience.builder()
                        .id(experienceId)
                        .title("Updated Title")
                        .company("Updated Company")
                        .employmentType(EmploymentType.FULL_TIME)
                        .startDate(LocalDate.of(2021, 1, 1))
                        .endDate(LocalDate.of(2023, 12, 31))
                        .location("Updated Location")
                        .locationType(LocationType.ON_SITE)
                        .build();

                when(experienceRepository.save(any(Experience.class))).thenReturn(updatedExperience);


                ExperienceResponseDTO response = experienceService.editById(userId,experienceId, request);


                assertNotNull(response);
                assertEquals("Updated Title", response.getTitle());
                assertEquals("Updated Company", response.getCompany());
                assertEquals(EmploymentType.FULL_TIME, response.getEmploymentType());
                assertEquals(LocalDate.of(2021, 1, 1), response.getStartDate());
                assertEquals(LocalDate.of(2023, 12, 31), response.getEndDate());
                assertEquals("Updated Location", response.getLocation());
                assertEquals(LocationType.ON_SITE, response.getLocationType());

                verify(experienceRepository, times(1)).findById(experienceId);
                verify(experienceRepository, times(1)).save(any(Experience.class));
            }

            @Test
            void testEditById_EntityNotFound() {

                Long nonExistentId = 999L;
                String userId = "userId";
                when(experienceRepository.findById(nonExistentId)).thenReturn(Optional.empty());


                EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                    experienceService.editById(userId,nonExistentId, request);
                });

                assertEquals("Experience with ID 999 not found", exception.getMessage());

                verify(experienceRepository, times(1)).findById(nonExistentId);
                verify(experienceRepository, never()).save(any(Experience.class));
            }

            @Test
            public void testEditById_UserNotAuthorized_ThrowsAccessDeniedException() {
                // Arrange
                String unauthorizedUserId = "999";
                when(experienceRepository.findById(1L)).thenReturn(java.util.Optional.of(experience));

                // Act & Assert
                AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                    experienceService.editById(unauthorizedUserId, 1L, request);
                });

                assertEquals("You are not allowed to edit this experience.", exception.getMessage());
            }
        }
    }
}