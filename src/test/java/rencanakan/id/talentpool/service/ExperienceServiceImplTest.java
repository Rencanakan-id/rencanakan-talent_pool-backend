//package rencanakan.id.talentpool.service;
//
//
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import rencanakan.id.talentpool.dto.EditExperienceRequestDTO;
//import rencanakan.id.talentpool.dto.ExperienceListResponseDTO;
//import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
//import rencanakan.id.talentpool.enums.EmploymentType;
//import rencanakan.id.talentpool.enums.LocationType;
//import rencanakan.id.talentpool.model.Experience;
//import rencanakan.id.talentpool.repository.ExperienceRepository;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class ExperienceServiceImplTest {
//
//   @Mock
//   private ExperienceRepository experienceRepository;
//
//   @InjectMocks
//   private ExperienceServiceImpl experienceService;
//
//   private Experience experience;
//   private EditExperienceRequestDTO requestDTO;
//   private List<Experience> experienceList;
//
//   @BeforeEach
//   void setUp() {
//       experience = new Experience();
//       experience.setId(1L);
//       experience.setTitle("Software Engineer");
//       experience.setCompany("Tech Corp");
//       experience.setEmploymentType(EmploymentType.FULL_TIME);
//       experience.setStartDate(LocalDate.of(2023, 1, 1));
//       experience.setEndDate(LocalDate.of(2023, 12, 31));
//       experience.setLocation("Jakarta");
//       experience.setLocationType(LocationType.HYBRID);
//       experience.setTalentId(100L);
//
//       requestDTO = new EditExperienceRequestDTO();
//       requestDTO.setTitle("Senior Software Engineer");
//       requestDTO.setCompany("Tech Innovators");
//       requestDTO.setEmploymentType(EmploymentType.FULL_TIME);
//       requestDTO.setStartDate(LocalDate.of(2024, 1, 1));
//       requestDTO.setEndDate(LocalDate.of(2024, 12, 31));
//       requestDTO.setLocation("Bali");
//       requestDTO.setLocationType(LocationType.HYBRID);
//       requestDTO.setTalentId(101L);
//
//       // Create a second experience for testing lists
//       Experience experience2 = new Experience();
//       experience2.setId(2L);
//       experience2.setTitle("Product Manager");
//       experience2.setCompany("Digital Solutions");
//       experience2.setEmploymentType(EmploymentType.PART_TIME);
//       experience2.setStartDate(LocalDate.of(2022, 6, 1));
//       experience2.setEndDate(LocalDate.of(2023, 5, 31));
//       experience2.setLocation("Bandung");
//       experience2.setLocationType(LocationType.HYBRID);
//       experience2.setTalentId(100L);
//
//       experienceList = Arrays.asList(experience, experience2);
//   }
//
//   @Test
//   void testEditById_Success() {
//
//       when(experienceRepository.findById(1L)).thenReturn(Optional.of(experience));
//       when(experienceRepository.save(any(Experience.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//
//       ExperienceResponseDTO responseDTO = experienceService.editById(1L, requestDTO);
//
//
//       assertNotNull(responseDTO);
//       assertEquals(requestDTO.getTitle(), responseDTO.getTitle());
//       assertEquals(requestDTO.getCompany(), responseDTO.getCompany());
//       assertEquals(requestDTO.getStartDate(), responseDTO.getStartDate());
//       assertEquals(requestDTO.getEndDate(), responseDTO.getEndDate());
//       assertEquals(requestDTO.getLocation(), responseDTO.getLocation());
//       assertEquals(requestDTO.getLocationType(), responseDTO.getLocationType());
//       assertEquals(requestDTO.getTalentId(), responseDTO.getTalentId());
//
//       verify(experienceRepository, times(1)).findById(1L);
//       verify(experienceRepository, times(1)).save(any(Experience.class));
//   }
//
//   @Test
//   void testEditById_EntityNotFoundException() {
//
//       when(experienceRepository.findById(2L)).thenReturn(Optional.empty());
//
//
//       EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
//           experienceService.editById(2L, requestDTO);
//       });
//
//       assertEquals("Experience not found", exception.getMessage());
//
//
//       verify(experienceRepository, times(1)).findById(2L);
//       verify(experienceRepository, never()).save(any(Experience.class));
//   }
//
//   @Test
//   void testGetByTalentId_Success() {
//       // Arrange
//       Long talentId = 100L;
//       when(experienceRepository.findByTalentId(talentId)).thenReturn(experienceList);
//
//       // Act
//       ExperienceListResponseDTO responseDTO = experienceService.getByTalentId(talentId);
//
//       // Assert
//       assertNotNull(responseDTO);
//       assertEquals(2, responseDTO.getExperiences().size());
//
//       // Verify first experience
//       ExperienceResponseDTO firstExp = responseDTO.getExperiences().get(0);
//       assertEquals(1L, firstExp.getId());
//       assertEquals("Software Engineer", firstExp.getTitle());
//       assertEquals("Tech Corp", firstExp.getCompany());
//       assertEquals(EmploymentType.FULL_TIME, firstExp.getEmploymentType());
//       assertEquals(LocalDate.of(2023, 1, 1), firstExp.getStartDate());
//       assertEquals(LocalDate.of(2023, 12, 31), firstExp.getEndDate());
//       assertEquals("Jakarta", firstExp.getLocation());
//       assertEquals(LocationType.HYBRID, firstExp.getLocationType());
//       assertEquals(100L, firstExp.getTalentId());
//
//       // Verify second experience
//       ExperienceResponseDTO secondExp = responseDTO.getExperiences().get(1);
//       assertEquals(2L, secondExp.getId());
//       assertEquals("Product Manager", secondExp.getTitle());
//       assertEquals("Digital Solutions", secondExp.getCompany());
//
//       // Verify repository was called with correct parameters
//       verify(experienceRepository, times(1)).findByTalentId(talentId);
//   }
//
//   @Test
//   void testGetByTalentId_EmptyList_ThrowsEntityNotFoundException() {
//       // Arrange
//       Long talentId = 999L;
//       when(experienceRepository.findByTalentId(talentId)).thenReturn(Collections.emptyList());
//
//       // Act & Assert
//       EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
//           experienceService.getByTalentId(talentId);
//       });
//
//       assertEquals("Experience is empty", exception.getMessage());
//       verify(experienceRepository, times(1)).findByTalentId(talentId);
//   }
//
//   @Test
//   void testDeleteById_Success() {
//       // Arrange
//       when(experienceRepository.findById(1L)).thenReturn(Optional.of(experience));
//
//       // Act
//       experienceService.deleteById(1L);
//
//       // Assert
//       verify(experienceRepository, times(1)).findById(1L);
//       verify(experienceRepository, times(1)).delete(experience);
//   }
//
//   @Test
//   void testDeleteById_EntityNotFoundException() {
//       // Arrange
//       when(experienceRepository.findById(2L)).thenReturn(Optional.empty());
//
//       // Act & Assert
//       EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
//           experienceService.deleteById(2L);
//       });
//
//       assertEquals("Experience by id " + 2L + " not found", exception.getMessage());
//       verify(experienceRepository, times(1)).findById(2L);
//       verify(experienceRepository, never()).delete(any(Experience.class));
//   }
//}
