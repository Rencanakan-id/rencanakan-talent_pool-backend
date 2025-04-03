package rencanakan.id.talentpool.unit.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.model.Experience;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExperienceTest {

private static Validator validator;
private User mockUser;
private LocalDate defaultStartDate;

@BeforeAll
static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id("user123")
                .email("user123@email.com")
                .password("password")
                .build();
        defaultStartDate = LocalDate.of(2020, 5, 1);
    }

    @Test
    void testEmptyConstructor() {
        Experience experience = new Experience();
        assertNotNull(experience, "Experience object should be created with empty constructor");
    }

    @Test
    void testFullValidParameters() {
        Experience experience = Experience.builder()
                .title("Software Engineer")
                .company("Tech Company")
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(defaultStartDate)
                .endDate(LocalDate.of(2022, 5, 1))
                .location("San Francisco")
                .locationType(LocationType.ON_SITE)
                .user(mockUser)
                .build();

        Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
        assertTrue(violations.isEmpty(), "Valid experience should have no violations");
    }

    @Nested
    class TitleAttributeTests {
        @Test
        void testTitleEmpty() {
            Experience experience = Experience.builder()
                    .title("")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title is required")));
        }

        @Test
        void testTitleMaxLength() {
            String maxLengthTitle = "A".repeat(50);
            Experience experience = Experience.builder()
                    .title(maxLengthTitle)
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertTrue(violations.isEmpty(), "Title at max length is considered valid");
        }

        @Test
        void testTitleExceedingMaxLength() {
            String exceedingTitle = "A".repeat(51);
            Experience experience = Experience.builder()
                    .title(exceedingTitle)
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must not exceed 50 characters")));
        }
    }

    @Nested
    class CompanyAttributeTests {
        @Test
        void testCompanyEmpty() {
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Company is required")));
        }

        @Test
        void testCompanyAtMaxLength() {
            String maxLengthCompany = "A".repeat(50);
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company(maxLengthCompany)
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertTrue(violations.isEmpty(), "Company at max length is considered valid");
        }

        @Test
        void testCompanyExceedingMaxLength() {
            String exceedingCompany = "A".repeat(51);
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company(exceedingCompany)
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must not exceed 50 characters")));
        }
    }

    @Nested
    class EmploymentTypeTests {
        @Test
        void testEmploymentTypeNull() {
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(null)
                    .startDate(defaultStartDate)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Employment type is required")));
        }
    }

    @Nested
    class StartDateAttributeTests {
        @Test
        void testStartDateNull() {
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(null)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Start date is required")));
        }
    }

    @Nested
    class EndDateAttributeTests {
        @Test
        void testEndDateNull() {
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .endDate(null)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertTrue(violations.isEmpty(), "End date can be null (for current jobs)");
        }
    }

    @Nested
    class LocationAttributeTests {
        @Test
        void testLocationEmpty() {
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .location("")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Location is required")));
        }

        @Test
        void testLocationAtMaxLength() {
            String maxLengthLocation = "A".repeat(50);
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .location(maxLengthLocation)
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertTrue(violations.isEmpty(), "Location at max length is considered valid");
        }

        @Test
        void testLocationExceedingMaxLength() {
            String exceedingLocation = "A".repeat(51);
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .location(exceedingLocation)
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must not exceed 50 characters")));
        }
    }

    @Nested
    class LocationTypeAttributeTests {
        @Test
        void testLocationTypeNull() {
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .location("San Francisco")
                    .locationType(null)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Location type is required")));
        }
    }

    @Nested
    class UserAttributeTests {
        @Test
        void testUserNull() {
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(null)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Talent (User) is required")));
        }
    }

    @Nested
    class CustomLogicTests {
        @Test
        void testEndDateBeforeStartDate() {
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .endDate(LocalDate.of(2020, 4, 30))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            // Check the custom validation method directly
            assertTrue(experience.isEndDateBeforeStartDate(), "Should return true when endDate is before startDate");

            // Also validate with the validator to ensure the constraint annotation works
            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("End date must not be earlier than start date")));
        }

        @Test
        void testEndDateAfterStartDate() {
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .endDate(LocalDate.of(2020, 5, 2))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertTrue(violations.isEmpty(), "End date after start date should be valid");
        }

        @Test
        void testEndDateSameAsStartDate() {
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(defaultStartDate)
                    .endDate(defaultStartDate)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(mockUser)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertTrue(violations.isEmpty(), "End date same as start date should be valid");
        }
    }
}