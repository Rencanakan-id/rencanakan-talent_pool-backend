package rencanakan.id.talentPool.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rencanakan.id.talentPool.enums.EmploymentType;
import rencanakan.id.talentPool.enums.LocationType;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ExperienceTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testEmptyConstructor() {
        Experience experience = Experience.builder()
                .build();

        Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
        assertEquals(7, violations.size(), "Expected 7 validation errors for mandatory attributes in Experience");
    }

    @Test
    public void testTitleEmpty() {
        Experience experience = Experience.builder()
                .setTitle("")
                .setCompany("Aman")
                .setEmploymentType(EmploymentType.FULL_TIME)
                .setStartDate(new Date())
                .setLocation("Depok")
                .setLocationType(LocationType.ON_SITE)
                .setTalentId("1")
                .build();

        Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
        boolean hasTitleViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Title is required") && v.getPropertyPath().toString().equals("title"));
        assertTrue(hasTitleViolation, "Expected violation for empty title");
    }

    @Test
    public void testTitleMexLength() {
        String validTitle = "A".repeat(30);

        Experience experience = Experience.builder()
                .setTitle(validTitle)
                .setCompany("Aman")
                .setEmploymentType(EmploymentType.FULL_TIME)
                .setStartDate(new Date())
                .setLocation("Depok")
                .setLocationType(LocationType.ON_SITE)
                .setTalentId("1")
                .build();

        Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
        assertTrue(violations.isEmpty(), "Validation should pass for title with 30 characters");
    }

    @Test
    public void testLocationExceedingMaxLength() {
        String invalidLocation = "A".repeat(31);
        Experience experience = Experience.builder()
                .setTitle(invalidLocation)
                .setCompany("Aman")
                .setEmploymentType(EmploymentType.FULL_TIME)
                .setStartDate(new Date())
                .setLocation(invalidLocation)
                .setLocationType(LocationType.ON_SITE)
                .setTalentId("1")
                .build();

        Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
        boolean hasLocationViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Title must not exceed 30 characters") && v.getPropertyPath().toString().equals("title"));
        assertTrue(hasLocationViolation, "Expected violation for title exceeding 30 characters");
    }
}