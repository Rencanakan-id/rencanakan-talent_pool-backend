package rencanakan.id.talentpool.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import jakarta.validation.*;
import rencanakan.id.talentpool.dto.EditExperienceRequestDTO;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;

import java.time.LocalDate;
import java.util.Set;

public class EditExperienceRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    public void testValidDTO() {
        EditExperienceRequestDTO dto = createValidDTO();
        Set<ConstraintViolation<EditExperienceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
    @Test
    void companyBlank_ShouldFailValidation() {
        EditExperienceRequestDTO dto = createValidDTO();
        dto.setCompany("");
        Set<ConstraintViolation<EditExperienceRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Please provide the company name.")));
    }


    @Test
    public void testInvalidTitle() {
        EditExperienceRequestDTO dto = createValidDTO();
        dto.setTitle("");
        Set<ConstraintViolation<EditExperienceRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Please provide a job title.", violations.iterator().next().getMessage());
    }


    @Test
    public void testExceedingTitleLength() {
        EditExperienceRequestDTO dto = createValidDTO();
        dto.setTitle("A very long job title that exceeds the maximum length allowed for this field.");
        Set<ConstraintViolation<EditExperienceRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Title can't exceed 50 characters.", violations.iterator().next().getMessage());
    }


    @Test
    public void testNullEmploymentType() {
        EditExperienceRequestDTO dto = createValidDTO();
        dto.setEmploymentType(null);
        Set<ConstraintViolation<EditExperienceRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Employment type is required.", violations.iterator().next().getMessage());
    }


    @Test
    public void testNullLocationType() {
        EditExperienceRequestDTO dto = createValidDTO();
        dto.setLocationType(null);
        Set<ConstraintViolation<EditExperienceRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Location type is required.", violations.iterator().next().getMessage());
    }


    @Test
    void endDateBeforeStartDate_ShouldFailValidation() {
        EditExperienceRequestDTO dto = createValidDTO();
        dto.setEndDate(dto.getStartDate().minusDays(1));
        Set<ConstraintViolation<EditExperienceRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("End date must not be earlier than start date")));
    }

    @Test
    void endDateNull_ShouldPassValidation() {
        EditExperienceRequestDTO dto = createValidDTO();
        dto.setEndDate(null);
        Set<ConstraintViolation<EditExperienceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "End date can be null, should not trigger validation error.");
    }


    @Test
    public void testTitleExactly50Characters() {
        EditExperienceRequestDTO dto = createValidDTO();
        dto.setTitle("A title that is exactly fifty characters long!!");
        Set<ConstraintViolation<EditExperienceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }



    @Test
    public void testNegativeTalentId() {
        EditExperienceRequestDTO dto = createValidDTO();
        dto.setTalentId(-1L);  // Invalid talent ID
        Set<ConstraintViolation<EditExperienceRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Talent ID must be a positive number.", violations.iterator().next().getMessage());
    }

    @Test
    public void testLocationExactly50Characters() {
        EditExperienceRequestDTO dto = createValidDTO();
        dto.setLocation("A location name that is exactly fifty chars long");
        Set<ConstraintViolation<EditExperienceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Location with exactly 50 characters should be valid.");
    }

    private EditExperienceRequestDTO createValidDTO() {
        EditExperienceRequestDTO dto = new EditExperienceRequestDTO();
        dto.setTitle("Software Engineer");
        dto.setCompany("TechCorp");
        dto.setEmploymentType(EmploymentType.FULL_TIME);
        dto.setStartDate(LocalDate.of(2020, 1, 1));
        dto.setEndDate(LocalDate.of(2023, 12, 31));
        dto.setLocation("New York");
        dto.setLocationType(LocationType.HYBRID);
        dto.setTalentId(123L);
        return dto;
    }
}

