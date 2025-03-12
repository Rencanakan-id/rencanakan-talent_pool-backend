package rencanakan.id.talentpool.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rencanakan.id.talentpool.dto.EditExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.repository.ExperienceRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExperienceServiceImplTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private ExperienceServiceImpl experienceService;

    private Experience experience;
    private EditExperienceRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        experience = new Experience();
        experience.setId(1L);
        experience.setTitle("Software Engineer");
        experience.setCompany("Tech Corp");
        experience.setEmploymentType(EmploymentType.FULL_TIME);
        experience.setStartDate(LocalDate.of(2023, 1, 1));
        experience.setEndDate(LocalDate.of(2023, 12, 31));
        experience.setLocation("Jakarta");
        experience.setLocationType(LocationType.HYBRID);
        experience.setTalentId(100L);

        requestDTO = new EditExperienceRequestDTO();
        requestDTO.setTitle("Senior Software Engineer");
        requestDTO.setCompany("Tech Innovators");
        requestDTO.setEmploymentType(EmploymentType.FULL_TIME);
        requestDTO.setStartDate(LocalDate.of(2024, 1, 1));
        requestDTO.setEndDate(LocalDate.of(2024, 12, 31));
        requestDTO.setLocation("Bali");
        requestDTO.setLocationType(LocationType.HYBRID);
        requestDTO.setTalentId(101L);
    }

    @Test
    void testEditById_Success() {

        when(experienceRepository.findById(1L)).thenReturn(Optional.of(experience));
        when(experienceRepository.save(any(Experience.class))).thenAnswer(invocation -> invocation.getArgument(0));


        ExperienceResponseDTO responseDTO = experienceService.editById(1L, requestDTO);


        assertNotNull(responseDTO);
        assertEquals(requestDTO.getTitle(), responseDTO.getTitle());
        assertEquals(requestDTO.getCompany(), responseDTO.getCompany());
        assertEquals(requestDTO.getStartDate(), responseDTO.getStartDate());
        assertEquals(requestDTO.getEndDate(), responseDTO.getEndDate());
        assertEquals(requestDTO.getLocation(), responseDTO.getLocation());
        assertEquals(requestDTO.getLocationType(), responseDTO.getLocationType());
        assertEquals(requestDTO.getTalentId(), responseDTO.getTalentId());

        verify(experienceRepository, times(1)).findById(1L);
        verify(experienceRepository, times(1)).save(any(Experience.class));
    }

    @Test
    void testEditById_EntityNotFoundException() {

        when(experienceRepository.findById(2L)).thenReturn(Optional.empty());


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            experienceService.editById(2L, requestDTO);
        });

        assertEquals("Experience not found", exception.getMessage());


        verify(experienceRepository, times(1)).findById(2L);
        verify(experienceRepository, never()).save(any(Experience.class));
    }
}
