package rencanakan.id.talentpool.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;

public class ExperienceResponseDTOTest {

    @Test
    public void testNoArgsConstructorDefaults() {
        ExperienceResponseDTO dto = new ExperienceResponseDTO();
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getCompany());
        assertNull(dto.getEmploymentType());
        assertNull(dto.getStartDate());
        assertNull(dto.getEndDate());
        assertNull(dto.getLocation());
        assertNull(dto.getLocationType());
        assertNull(dto.getTalentId());
    }

    @Test
    public void testSettersAndGetters() {
        ExperienceResponseDTO dto = new ExperienceResponseDTO();
        dto.setId(1L);
        dto.setTitle("Software Engineer");
        dto.setCompany("Acme Inc.");
        dto.setEmploymentType(EmploymentType.FULL_TIME);
        dto.setStartDate(LocalDate.of(2020, 1, 1));
        dto.setEndDate(LocalDate.of(2021, 1, 1));
        dto.setLocation("Jakarta");
        dto.setLocationType(LocationType.HYBRID);
        dto.setTalentId(10L);

        assertEquals(1L, dto.getId());
        assertEquals("Software Engineer", dto.getTitle());
        assertEquals("Acme Inc.", dto.getCompany());
        assertEquals(EmploymentType.FULL_TIME, dto.getEmploymentType());
        assertEquals(LocalDate.of(2020, 1, 1), dto.getStartDate());
        assertEquals(LocalDate.of(2021, 1, 1), dto.getEndDate());
        assertEquals("Jakarta", dto.getLocation());
        assertEquals(LocationType.HYBRID, dto.getLocationType());
        assertEquals(10L, dto.getTalentId());
    }

    @Test
    public void testAllArgsConstructor() {
        ExperienceResponseDTO dto = new ExperienceResponseDTO(
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

        assertEquals(2L, dto.getId());
        assertEquals("Data Scientist", dto.getTitle());
        assertEquals("Tech Corp", dto.getCompany());
        assertEquals(EmploymentType.PART_TIME, dto.getEmploymentType());
        assertEquals(LocalDate.of(2019, 5, 10), dto.getStartDate());
        assertEquals(LocalDate.of(2020, 5, 10), dto.getEndDate());
        assertEquals("Bandung", dto.getLocation());
        assertEquals(LocationType.HYBRID, dto.getLocationType());
        assertEquals(20L, dto.getTalentId());
    }
}
