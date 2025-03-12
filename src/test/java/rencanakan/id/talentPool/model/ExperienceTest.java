package rencanakan.id.talentpool.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.model.Experience;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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
        System.out.println(violations);
        assertEquals(6, violations.size(), "Expected 7 validation errors for mandatory attributes in Experience");
    }

    @Test
    public void testFullValidParameters() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(1);

        Experience experience = Experience.builder()
                .title("Lead Construction Project Manager")
                .company("Aman")
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(startDate)
                .endDate(endDate)
                .location("Depok")
                .locationType(LocationType.ON_SITE)
                .talentId(1L)
                .build();

        Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
        assertTrue(violations.isEmpty(), "Validation should pass when endDate is a valid Date");
    }

    @Nested
    class TitleAttributeTests {
        @Test
        public void testTitleEmpty() {
            Experience experience = Experience.builder()
                    .title("")
                    .company("Aman")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .location("Depok")
                    .locationType(LocationType.ON_SITE)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            boolean hasTitleViolation = violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Title is required") && v.getPropertyPath().toString().equals("title"));
            assertTrue(hasTitleViolation, "Expected violation for empty title");
        }

        @Test
        public void testTitleMexLength() {
            String validTitle = "A".repeat(50);

            Experience experience = Experience.builder()
                    .title(validTitle)
                    .company("Aman")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .location("Depok")
                    .locationType(LocationType.ON_SITE)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            assertTrue(violations.isEmpty(), "Validation should pass for title with 50 characters");
        }
    }

    @Nested
    class CompanyAttributeTests {
        @Test
        public void testCompanyEmpty() {
            Experience experience = Experience.builder()
                    .title("Lead Construction Project Manager")
                    .company("")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .location("Depok")
                    .locationType(LocationType.ON_SITE)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            boolean hasCompanyViolation = violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Company is required") && v.getPropertyPath().toString().equals("company"));
            assertTrue(hasCompanyViolation, "Expected violation for empty company");
        }

        @Test
        public void testCompanyAtMaxLength() {
            String validCompany = "A".repeat(50);
            Experience experience = Experience.builder()
                    .title("Lead Construction Project Manager")
                    .company(validCompany)
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .location("Depok")
                    .locationType(LocationType.ON_SITE)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            assertTrue(violations.isEmpty(), "Validation should pass for company with 50 characters");
        }

        @Test
        public void testCompanyExceedingMaxLength() {
            String invalidCompany = "A".repeat(51);
            Experience experience = Experience.builder()
                    .title("Lead Construction Project Manager")
                    .company(invalidCompany)
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .location("Depok")
                    .locationType(LocationType.ON_SITE)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            boolean hasCompanyViolation = violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Company must not exceed 50 characters") && v.getPropertyPath().toString().equals("company"));
            assertTrue(hasCompanyViolation, "Expected violation for company exceeding 50 characters");
        }
    }

    @Nested
    class EmploymentTypeTests {
        @Test
        public void testEmploymentTypeNull() {
            Experience experience = Experience.builder()
                    .title("Lead Construction Project Manager")
                    .company("Aman")
                    .employmentType(null)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .location("Depok")
                    .locationType(LocationType.ON_SITE)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            boolean hasEmploymentTypeViolation = violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Employment type is required") && v.getPropertyPath().toString().equals("employmentType"));
            assertTrue(hasEmploymentTypeViolation, "Expected violation for null employment type");
        }
    }

    @Nested
    class StartDateAttributeTests {
        @Test
        public void testStartDateNull() {
            Experience experience = Experience.builder()
                    .title("Lead Construction Project Manager")
                    .company("Aman")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(null)
                    .endDate(LocalDate.now())
                    .location("Depok")
                    .locationType(LocationType.ON_SITE)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            boolean hasStartDateViolation = violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Start date is required") && v.getPropertyPath().toString().equals("startDate"));
            assertTrue(hasStartDateViolation, "Expected violation for null start date");
        }
    }

    @Nested
    class EndDateAttributeTests {
        @Test
        public void testEndDateNull() {
            Experience experience = Experience.builder()
                    .title("Lead Construction Project Manager")
                    .company("Aman")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.now())
                    .location("Depok")
                    .locationType(LocationType.ON_SITE)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            System.out.println(violations);
            assertTrue(violations.isEmpty(), "Validation should pass when endDate is null");
        }
    }

    @Nested
    class LocationAttributeTests {
        @Test
        public void testLocationEmpty() {
            Experience experience = Experience.builder()
                    .title("Lead Construction Project Manager")
                    .company("Aman")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .location("")
                    .locationType(LocationType.ON_SITE)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            boolean hasLocationViolation = violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Location is required") && v.getPropertyPath().toString().equals("location"));
            assertTrue(hasLocationViolation, "Expected violation for empty location");
        }

        @Test
        public void testLocationAtMaxLength() {
            String validLocation = "A".repeat(50);
            Experience experience = Experience.builder()
                    .title("Lead Construction Project Manager")
                    .company("Aman")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .location(validLocation)
                    .locationType(LocationType.ON_SITE)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            assertTrue(violations.isEmpty(), "Validation should pass for location with 50 characters");
        }

        @Test
        public void testLocationExceedingMaxLength() {
            String invalidLocation = "A".repeat(51);
            Experience experience = Experience.builder()
                    .title("Lead Construction Project Manager")
                    .company("Aman")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .location(invalidLocation)
                    .locationType(LocationType.ON_SITE)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            boolean hasCompanyViolation = violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Location must not exceed 50 characters") && v.getPropertyPath().toString().equals("location"));
            assertTrue(hasCompanyViolation, "Expected violation for location exceeding 50 characters");
        }
    }

    @Nested
    class LocationTypeAttributeTests {
        @Test
        public void tesLocationTypeNull() {
            Experience experience = Experience.builder()
                    .title("Lead Construction Project Manager")
                    .company("Aman")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .location("Depok")
                    .locationType(null)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            boolean hasLocationTypeViolation = violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Location type is required") && v.getPropertyPath().toString().equals("locationType"));
            assertTrue(hasLocationTypeViolation, "Expected violation for null location type");
        }
    }

    @Nested
    class TalentIdAttributeTests {
        @Test
        public void testTalentIdNegative() {
            Experience experience = Experience.builder()
                    .title("Lead Construction Project Manager")
                    .company("Aman")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .location("Depok")
                    .locationType(LocationType.ON_SITE)
                    .talentId(-1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            boolean hasTalentIdViolation = violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Talent ID must be a positive number") && v.getPropertyPath().toString().equals("talentId"));
            assertTrue(hasTalentIdViolation, "Expected violation for non-positive talent ID");
        }

        @Test
        public void testTalentIdZero() {
            Experience experience = Experience.builder()
                    .title("Lead Construction Project Manager")
                    .company("Aman")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .location("Depok")
                    .locationType(LocationType.ON_SITE)
                    .talentId(0L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            boolean hasTalentIdViolation = violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Talent ID must be a positive number") && v.getPropertyPath().toString().equals("talentId"));
            assertTrue(hasTalentIdViolation, "Expected violation for non-positive talent ID");
        }
    }

    @Nested
    class CustomLogicTests {
        @Test
        public void testEndDateBeforeStartDate() {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.minusDays(1);

            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Aman")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(startDate)
                    .endDate(endDate)
                    .location("Depok")
                    .locationType(LocationType.ON_SITE)
                    .talentId(1L)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            boolean hasEndDateViolation = violations.stream()
                    .anyMatch(v -> v.getMessage().equals("End date must not be earlier than start date"));
            assertTrue(hasEndDateViolation, "Expected violation for endDate being earlier than startDate");
        }
    }
}