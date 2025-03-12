package rencanakan.id.talentpool.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;

class ExperienceListResponseDTOTest {

    @Test
    void testNoArgsConstructorDefaults() {
        ExperienceListResponseDTO dto = new ExperienceListResponseDTO();
        assertNull(dto.getExperiences());
    }

    @Test
    void testSettersAndGetters() {
        ExperienceListResponseDTO dto = new ExperienceListResponseDTO();
        List<ExperienceResponseDTO> experiences = new ArrayList<>();

        ExperienceResponseDTO experience1 = new ExperienceResponseDTO();
        experience1.setId(1L);
        experience1.setTitle("Software Engineer");
        experience1.setCompany("Acme Inc.");
        experience1.setEmploymentType(EmploymentType.FULL_TIME);
        experience1.setStartDate(LocalDate.of(2020, 1, 1));
        experience1.setEndDate(LocalDate.of(2021, 1, 1));
        experience1.setLocation("Jakarta");
        experience1.setLocationType(LocationType.HYBRID);
        experience1.setTalentId(10L);

        experiences.add(experience1);

        dto.setExperiences(experiences);

        assertNotNull(dto.getExperiences());
        assertEquals(1, dto.getExperiences().size());
        assertEquals(1L, dto.getExperiences().get(0).getId());
        assertEquals("Software Engineer", dto.getExperiences().get(0).getTitle());
        assertEquals("Acme Inc.", dto.getExperiences().get(0).getCompany());
        assertEquals(EmploymentType.FULL_TIME, dto.getExperiences().get(0).getEmploymentType());
        assertEquals(LocalDate.of(2020, 1, 1), dto.getExperiences().get(0).getStartDate());
        assertEquals(LocalDate.of(2021, 1, 1), dto.getExperiences().get(0).getEndDate());
        assertEquals("Jakarta", dto.getExperiences().get(0).getLocation());
        assertEquals(LocationType.HYBRID, dto.getExperiences().get(0).getLocationType());
        assertEquals(10L, dto.getExperiences().get(0).getTalentId());
    }

    @Test
    void testAllArgsConstructor() {
        ExperienceResponseDTO experience1 = new ExperienceResponseDTO(
                1L,
                "Software Engineer",
                "Acme Inc.",
                EmploymentType.FULL_TIME,
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2021, 1, 1),
                "Jakarta",
                LocationType.HYBRID,
                10L
        );

        ExperienceResponseDTO experience2 = new ExperienceResponseDTO(
                2L,
                "Data Scientist",
                "Tech Corp",
                EmploymentType.PART_TIME,
                LocalDate.of(2019, 5, 10),
                LocalDate.of(2020, 5, 10),
                "Bandung",
                LocationType.HYBRID,
                20L
        );

        List<ExperienceResponseDTO> experiences = Arrays.asList(experience1, experience2);

        ExperienceListResponseDTO dto = new ExperienceListResponseDTO(experiences);

        assertNotNull(dto.getExperiences());
        assertEquals(2, dto.getExperiences().size());

        // Verify first experience
        assertEquals(1L, dto.getExperiences().get(0).getId());
        assertEquals("Software Engineer", dto.getExperiences().get(0).getTitle());
        assertEquals("Acme Inc.", dto.getExperiences().get(0).getCompany());
        assertEquals(EmploymentType.FULL_TIME, dto.getExperiences().get(0).getEmploymentType());
        assertEquals(LocalDate.of(2020, 1, 1), dto.getExperiences().get(0).getStartDate());
        assertEquals(LocalDate.of(2021, 1, 1), dto.getExperiences().get(0).getEndDate());
        assertEquals("Jakarta", dto.getExperiences().get(0).getLocation());
        assertEquals(LocationType.HYBRID, dto.getExperiences().get(0).getLocationType());
        assertEquals(10L, dto.getExperiences().get(0).getTalentId());

        // Verify second experience
        assertEquals(2L, dto.getExperiences().get(1).getId());
        assertEquals("Data Scientist", dto.getExperiences().get(1).getTitle());
        assertEquals("Tech Corp", dto.getExperiences().get(1).getCompany());
        assertEquals(EmploymentType.PART_TIME, dto.getExperiences().get(1).getEmploymentType());
        assertEquals(LocalDate.of(2019, 5, 10), dto.getExperiences().get(1).getStartDate());
        assertEquals(LocalDate.of(2020, 5, 10), dto.getExperiences().get(1).getEndDate());
        assertEquals("Bandung", dto.getExperiences().get(1).getLocation());
        assertEquals(LocationType.HYBRID, dto.getExperiences().get(1).getLocationType());
        assertEquals(20L, dto.getExperiences().get(1).getTalentId());
    }

    @Test
    void testEmptyList() {
        List<ExperienceResponseDTO> emptyList = new ArrayList<>();
        ExperienceListResponseDTO dto = new ExperienceListResponseDTO(emptyList);

        assertNotNull(dto.getExperiences());
        assertTrue(dto.getExperiences().isEmpty());
    }
}