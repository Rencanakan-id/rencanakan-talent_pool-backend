package rencanakan.id.talentpool.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
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
import java.util.Arrays;

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

    private Experience experience;
    private ExperienceRequestDTO requestDTO;
    private User user;
    private UserResponseDTO userResponseDTO;
    private ExperienceResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        user = createUser();
        userResponseDTO = createUserResponseDTO();
        experience = createExperience();
        requestDTO = createExperienceRequestDTO();
        responseDTO = createExperienceResponseDTO();
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
            when(userService.getById("1")).thenReturn(userResponseDTO);
            mockedStatic.when(() -> DTOMapper.map(userResponseDTO, User.class)).thenReturn(user);
            mockedStatic.when(() -> DTOMapper.map(requestDTO, Experience.class)).thenReturn(experience);
            mockedStatic.when(() -> DTOMapper.map(experience, ExperienceResponseDTO.class)).thenReturn(responseDTO);
            when(experienceRepository.save(any(Experience.class))).thenReturn(experience);

            ExperienceResponseDTO result = experienceService.createExperience(requestDTO);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Software Engineer", result.getTitle());
            assertEquals("TechCorp", result.getCompany());
            assertEquals(EmploymentType.FULL_TIME, result.getEmploymentType());

            verify(userService).getById("1");
            verify(experienceRepository).save(any(Experience.class));
        }
    }

    @Test
    void testCreateExperience_WithNullFields() {
        try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
            requestDTO.setEndDate(null);

            Experience modifiedExperience = createModifiedExperienceWithNullEndDate();
            ExperienceResponseDTO modifiedResponse = createModifiedResponseWithNullEndDate();

            when(userService.getById("1")).thenReturn(userResponseDTO);
            mockedStatic.when(() -> DTOMapper.map(userResponseDTO, User.class)).thenReturn(user);
            mockedStatic.when(() -> DTOMapper.map(requestDTO, Experience.class)).thenReturn(modifiedExperience);
            mockedStatic.when(() -> DTOMapper.map(modifiedExperience, ExperienceResponseDTO.class)).thenReturn(modifiedResponse);
            when(experienceRepository.save(any(Experience.class))).thenReturn(modifiedExperience);

            ExperienceResponseDTO result = experienceService.createExperience(requestDTO);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Software Engineer", result.getTitle());
            assertNull(result.getEndDate());

            verify(userService).getById("1");
            verify(experienceRepository).save(any(Experience.class));
        }
    }

    private Experience createModifiedExperienceWithNullEndDate() {
        return Experience.builder()
                .id(experience.getId())
                .title(experience.getTitle())
                .company(experience.getCompany())
                .employmentType(experience.getEmploymentType())
                .startDate(experience.getStartDate())
                .endDate(null)
                .location(experience.getLocation())
                .locationType(experience.getLocationType())
                .user(experience.getUser())
                .build();
    }

    private ExperienceResponseDTO createModifiedResponseWithNullEndDate() {
        return ExperienceResponseDTO.builder()
                .id(responseDTO.getId())
                .title(responseDTO.getTitle())
                .company(responseDTO.getCompany())
                .employmentType(responseDTO.getEmploymentType())
                .startDate(responseDTO.getStartDate())
                .endDate(null)
                .location(responseDTO.getLocation())
                .locationType(responseDTO.getLocationType())
                .build();
    }

    @Test
    void testCreateExperience_WithMaxLengthFields() {
        try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
            String maxLengthString = "A".repeat(100);
            requestDTO.setTitle(maxLengthString);
            requestDTO.setCompany(maxLengthString);
            requestDTO.setLocation(maxLengthString);

            Experience modifiedExperience = createModifiedExperienceWithMaxLengthFields(maxLengthString);
            ExperienceResponseDTO modifiedResponse = createModifiedResponseWithMaxLengthFields(maxLengthString);

            when(userService.getById("1")).thenReturn(userResponseDTO);
            mockedStatic.when(() -> DTOMapper.map(userResponseDTO, User.class)).thenReturn(user);
            mockedStatic.when(() -> DTOMapper.map(requestDTO, Experience.class)).thenReturn(modifiedExperience);
            mockedStatic.when(() -> DTOMapper.map(modifiedExperience, ExperienceResponseDTO.class)).thenReturn(modifiedResponse);
            when(experienceRepository.save(any(Experience.class))).thenReturn(modifiedExperience);

            ExperienceResponseDTO result = experienceService.createExperience(requestDTO);

            assertNotNull(result);
            assertEquals(maxLengthString, result.getTitle());
            assertEquals(maxLengthString, result.getCompany());
            assertEquals(maxLengthString, result.getLocation());

            verify(userService).getById("1");
            verify(experienceRepository).save(any(Experience.class));
        }
    }

    private Experience createModifiedExperienceWithMaxLengthFields(String maxLengthString) {
        return Experience.builder()
                .id(experience.getId())
                .title(maxLengthString)
                .company(maxLengthString)
                .employmentType(experience.getEmploymentType())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .location(maxLengthString)
                .locationType(experience.getLocationType())
                .user(experience.getUser())
                .build();
    }

    private ExperienceResponseDTO createModifiedResponseWithMaxLengthFields(String maxLengthString) {
        return ExperienceResponseDTO.builder()
                .id(responseDTO.getId())
                .title(maxLengthString)
                .company(maxLengthString)
                .employmentType(responseDTO.getEmploymentType())
                .startDate(responseDTO.getStartDate())
                .endDate(responseDTO.getEndDate())
                .location(maxLengthString)
                .locationType(responseDTO.getLocationType())
                .build();
    }

    @Test
    void testCreateExperience_WithDifferentLocationTypes() {
        try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
            for (LocationType locationType : LocationType.values()) {
                requestDTO.setLocationType(locationType);

                Experience modifiedExperience = createModifiedExperienceWithLocationType(locationType);
                ExperienceResponseDTO modifiedResponse = createModifiedResponseWithLocationType(locationType);

                when(userService.getById("1")).thenReturn(userResponseDTO);
                mockedStatic.when(() -> DTOMapper.map(userResponseDTO, User.class)).thenReturn(user);
                mockedStatic.when(() -> DTOMapper.map(requestDTO, Experience.class)).thenReturn(modifiedExperience);
                mockedStatic.when(() -> DTOMapper.map(modifiedExperience, ExperienceResponseDTO.class)).thenReturn(modifiedResponse);
                when(experienceRepository.save(any(Experience.class))).thenReturn(modifiedExperience);

                ExperienceResponseDTO result = experienceService.createExperience(requestDTO);

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
                .id(responseDTO.getId())
                .title(responseDTO.getTitle())
                .company(responseDTO.getCompany())
                .employmentType(responseDTO.getEmploymentType())
                .startDate(responseDTO.getStartDate())
                .endDate(responseDTO.getEndDate())
                .location(responseDTO.getLocation())
                .locationType(locationType)
                .build();
    }

    @Test
    void testCreateExperience_WithDifferentEmploymentTypes() {
        try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
            for (EmploymentType employmentType : EmploymentType.values()) {
                requestDTO.setEmploymentType(employmentType);

                Experience modifiedExperience = createModifiedExperienceWithEmploymentType(employmentType);
                ExperienceResponseDTO modifiedResponse = createModifiedResponseWithEmploymentType(employmentType);

                when(userService.getById("1")).thenReturn(userResponseDTO);
                mockedStatic.when(() -> DTOMapper.map(userResponseDTO, User.class)).thenReturn(user);
                mockedStatic.when(() -> DTOMapper.map(requestDTO, Experience.class)).thenReturn(modifiedExperience);
                mockedStatic.when(() -> DTOMapper.map(modifiedExperience, ExperienceResponseDTO.class)).thenReturn(modifiedResponse);
                when(experienceRepository.save(any(Experience.class))).thenReturn(modifiedExperience);

                ExperienceResponseDTO result = experienceService.createExperience(requestDTO);

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
                .id(responseDTO.getId())
                .title(responseDTO.getTitle())
                .company(responseDTO.getCompany())
                .employmentType(employmentType)
                .startDate(responseDTO.getStartDate())
                .endDate(responseDTO.getEndDate())
                .location(responseDTO.getLocation())
                .locationType(responseDTO.getLocationType())
                .build();
    }
}