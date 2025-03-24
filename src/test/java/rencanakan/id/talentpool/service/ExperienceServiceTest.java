//package rencanakan.id.talentpool.service;
//
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.validation.ConstraintViolationException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
//import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
//import rencanakan.id.talentpool.enums.EmploymentType;
//import rencanakan.id.talentpool.enums.LocationType;
//import rencanakan.id.talentpool.mapper.DTOMapper;
//import rencanakan.id.talentpool.model.Experience;
//import rencanakan.id.talentpool.repository.ExperienceRepository;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ExperienceServiceTest {
//
//   @Mock
//   private ExperienceRepository experienceRepository;
//
//   @InjectMocks
//   private ExperienceServiceImpl experienceService;
//   private ExperienceResponseDTO response;
//   private  ExperienceRequestDTO request;
//    private Experience experience;
//
//
//    @BeforeEach
//   void setUp() {
//       MockitoAnnotations.openMocks(this);
//       request = createExperienceRequestDTO();
//       response= createExperienceResponseDTO();
//       experience = createExperience();
//   }
//    private ExperienceRequestDTO createExperienceRequestDTO() {
//        return ExperienceRequestDTO.builder()
//                .title("Lead Construction Project Manager")
//                .company("Aman")
//                .employmentType(EmploymentType.FULL_TIME)
//                .startDate(LocalDate.now())
//                .endDate(LocalDate.now().plusDays(1))
//                .location("Depok")
//                .locationType(LocationType.ON_SITE)
//                .build();
//    }
//
//    private ExperienceResponseDTO createExperienceResponseDTO() {
//        return ExperienceResponseDTO.builder()
//                .id(1L)
//                .title("Lead Construction Project Manager")
//                .company("Aman")
//                .employmentType(EmploymentType.FULL_TIME)
//                .startDate(LocalDate.now())
//                .endDate(LocalDate.now().plusDays(1))
//                .location("Depok")
//                .locationType(LocationType.ON_SITE)
//                .build();
//    }
//    private Experience createExperience() {
//        return Experience.builder()
//                .id(1L)
//                .title("Old Title")
//                .company("Old Company")
//                .employmentType(EmploymentType.PART_TIME)
//                .startDate(LocalDate.of(2018, 1, 1))
//                .endDate(LocalDate.of(2020, 12, 31))
//                .location("Old Location")
//                .locationType(LocationType.HYBRID)
//                .build();
//    }
//   @Test
//   void testCreateExperience_Success() {
//       // Arrange
//       ExperienceRequestDTO request = ExperienceRequestDTO.builder()
//               .title("Software Engineer")
//               .company("Tech Corp")
//               .employmentType(EmploymentType.FULL_TIME)
//               .startDate(LocalDate.of(2020, 1, 1))
//               .endDate(LocalDate.of(2023, 12, 31))
//               .location("New York")
//               .locationType(LocationType.ON_SITE)
//               .build();
//
//       Experience savedExperience = Experience.builder()
//               .id(1L)
//               .title("Software Engineer")
//               .company("Tech Corp")
//               .employmentType(EmploymentType.FULL_TIME)
//               .startDate(LocalDate.of(2020, 1, 1))
//               .endDate(LocalDate.of(2023, 12, 31))
//               .location("New York")
//               .locationType(LocationType.ON_SITE)
//               .build();
//
//       when(experienceRepository.save(any(Experience.class))).thenReturn(savedExperience);
//
//       // Act
//       ExperienceResponseDTO response = experienceService.createExperience(request);
//
//       // Assert
//       assertNotNull(response);
//       assertEquals("Software Engineer", response.getTitle());
//       assertEquals("Tech Corp", response.getCompany());
//       assertEquals(EmploymentType.FULL_TIME, response.getEmploymentType());
//       assertEquals(LocalDate.of(2020, 1, 1), response.getStartDate());
//       assertEquals(LocalDate.of(2023, 12, 31), response.getEndDate());
//       assertEquals("New York", response.getLocation());
//       assertEquals(LocationType.ON_SITE, response.getLocationType());
//
//       verify(experienceRepository, times(1)).save(any(Experience.class));
//   }
//
//   @Test
//   void testEditById_Success() {
//       // Arrange
//       Long experienceId = 1L;
//
//       when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
//
//       Experience updatedExperience = Experience.builder()
//               .id(experienceId)
//               .title("Updated Title")
//               .company("Updated Company")
//               .employmentType(EmploymentType.FULL_TIME)
//               .startDate(LocalDate.of(2021, 1, 1))
//               .endDate(LocalDate.of(2023, 12, 31))
//               .location("Updated Location")
//               .locationType(LocationType.ON_SITE)
//               .build();
//
//       when(experienceRepository.save(any(Experience.class))).thenReturn(updatedExperience);
//
//
//       ExperienceResponseDTO response = experienceService.editById(experienceId, request);
//
//
//       assertNotNull(response);
//       assertEquals("Updated Title", response.getTitle());
//       assertEquals("Updated Company", response.getCompany());
//       assertEquals(EmploymentType.FULL_TIME, response.getEmploymentType());
//       assertEquals(LocalDate.of(2021, 1, 1), response.getStartDate());
//       assertEquals(LocalDate.of(2023, 12, 31), response.getEndDate());
//       assertEquals("Updated Location", response.getLocation());
//       assertEquals(LocationType.ON_SITE, response.getLocationType());
//
//       verify(experienceRepository, times(1)).findById(experienceId);
//       verify(experienceRepository, times(1)).save(any(Experience.class));
//   }
//
//   @Test
//   void testEditById_EntityNotFound() {
//
//       Long nonExistentId = 999L;
//       when(experienceRepository.findById(nonExistentId)).thenReturn(Optional.empty());
//
//
//       EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
//           experienceService.editById(nonExistentId, request);
//       });
//
//       assertEquals("Experience with ID 999 not found", exception.getMessage());
//
//       verify(experienceRepository, times(1)).findById(nonExistentId);
//       verify(experienceRepository, never()).save(any(Experience.class));
//   }
//
////    @Test
////    void testValidationFailure_CreateExperience() {
////        // Arrange
////        ExperienceRequestDTO invalidRequest = ExperienceRequestDTO.builder()
////                .title("") // Blank title (violates @NotBlank constraint)
////                .company("Tech Corp")
////                .employmentType(EmploymentType.FULL_TIME)
////                .startDate(LocalDate.of(2020, 1, 1))
////                .endDate(LocalDate.of(2023, 12, 31))
////                .location("New York")
////                .locationType(LocationType.ON_SITE)
////                .build();
////
////        // Act & Assert
////        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
////            experienceService.createExperience(invalidRequest);
////        });
////
////        assertTrue(exception.getMessage().contains("Title is required"));
////
////        verify(experienceRepository, never()).save(any(Experience.class));
////    }
//}