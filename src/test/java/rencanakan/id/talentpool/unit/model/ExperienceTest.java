package rencanakan.id.talentpool.unit.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExperienceTest {

    private static Validator validator;

    private User createUser() {
        return User.builder()
                .firstName("first")
                .lastName("Smith")
                .email("william@gmial.com")
                .phoneNumber("1234567890")
                .photo("profile-photo.jpg")
                .aboutMe("I am a software developer.")
                .nik("1234567890123456")
                .npwp("987654321098765")
                .photoKtp("ktp-photo.jpg")
                .photoNpwp("npwp-photo.jpg")
                .photoIjazah("ijazah-photo.jpg")
                .experienceYears(5)
                .skkLevel("Level 3")
                .currentLocation("Jakarta")
                .preferredLocations(Arrays.asList("Bandung", "Surabaya"))
                .skill("Java, Spring Boot, Microservices")
                .password("password")
                .price(1000000)
                .build();
    }

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testEmptyConstructor() {
        Experience experience = new Experience();
        assertNotNull(experience, "Experience object should be created with empty constructor");
    }

    @Test
    void testFullValidParameters() {
        User user = createUser();

        Experience experience = Experience.builder()
                .title("Software Engineer")
                .company("Tech Company")
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(LocalDate.of(2020, 5, 1))
                .endDate(LocalDate.of(2022, 5, 1))
                .location("San Francisco")
                .locationType(LocationType.ON_SITE)
                .user(user)
                .build();

        Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
        assertTrue(violations.isEmpty(), "Valid experience should have no violations");
    }

    @Nested
    class TitleAttributeTests {
        @Test
        void testTitleEmpty() {
            User user = createUser();

            Experience experience = Experience.builder()
                    .title("")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title is required")));
        }

        @Test
        void testTitleMaxLength() {
            User user = createUser();

            String maxLengthTitle = "A".repeat(50);
            Experience experience = Experience.builder()
                    .title(maxLengthTitle)
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertTrue(violations.isEmpty(), "Title at max length is considered valid");
        }

        @Test
        void testTitleExceedingMaxLength() {
            User user = createUser();

            String exceedingTitle = "A".repeat(51);
            Experience experience = Experience.builder()
                    .title(exceedingTitle)
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
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
            User user = createUser();

            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Company is required")));
        }

        @Test
        void testCompanyAtMaxLength() {
            User user = createUser();

            String maxLengthCompany = "A".repeat(50);
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company(maxLengthCompany)
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertTrue(violations.isEmpty(), "Company at max length is considered valid");
        }

        @Test
        void testCompanyExceedingMaxLength() {
            User user = createUser();

            String exceedingCompany = "A".repeat(51);
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company(exceedingCompany)
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
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
            User user = createUser();

            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(null)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
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
            User user = createUser();

            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(null)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
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
            User user = createUser();

            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .endDate(null)
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertTrue(violations.isEmpty(), "End date can be null (for current jobs)");
        }
    }

    @Nested
    class LocationAttributeTests {
        @Test
        void testLocationEmpty() {
            User user = createUser();

            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .location("")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Location is required")));
        }

        @Test
        void testLocationAtMaxLength() {
            User user = createUser();

            String maxLengthLocation = "A".repeat(50);
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .location(maxLengthLocation)
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertTrue(violations.isEmpty(), "Location at max length is considered valid");
        }

        @Test
        void testLocationExceedingMaxLength() {
            User user = createUser();

            String exceedingLocation = "A".repeat(51);
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .location(exceedingLocation)
                    .locationType(LocationType.ON_SITE)
                    .user(user)
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
            User user = createUser();

            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .location("San Francisco")
                    .locationType(null)
                    .user(user)
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
                    .startDate(LocalDate.of(2020, 5, 1))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(null) // Explicitly set user to null
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
            User user = createUser();

            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .endDate(LocalDate.of(2020, 4, 30))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
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
            User user = createUser();

            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .endDate(LocalDate.of(2020, 5, 2))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertTrue(violations.isEmpty(), "End date after start date should be valid");
        }

        @Test
        void testEndDateSameAsStartDate() {
            User user = createUser();

            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .endDate(LocalDate.of(2020, 5, 1))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();

            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertTrue(violations.isEmpty(), "End date same as start date should be valid");
        }
    }

    @Nested
    class LastModifiedDateTests {
        @Test
        void testLastModifiedDateNotNull() {
            User user = createUser();
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .endDate(LocalDate.of(2020, 5, 2))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();

            assertNotNull(experience.getLastModifiedDate(),
                    "Last modified date should not be null");
        }

        @Test
        void testLastModifiedDateSetter() {
            User user = createUser();
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .endDate(LocalDate.of(2020, 5, 2))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();
            LocalDateTime testDate = LocalDateTime.of(2023, 1, 1, 12, 0);
            experience.setLastModifiedDate(testDate);
            assertEquals(testDate, experience.getLastModifiedDate(),
                    "Should be able to set the last modified date");
        }

        @Test
        void testLastModifiedDateInThePast() {
            User user = createUser();
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .endDate(LocalDate.of(2020, 5, 2))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();
            LocalDateTime pastDate = LocalDateTime.now().minusDays(5);
            experience.setLastModifiedDate(pastDate);
            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            assertTrue(violations.isEmpty(), "Past date should be valid for last modified date");
        }

        @Test
        void testLastModifiedDateInPresent() {
            User user = createUser();
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .endDate(LocalDate.of(2020, 5, 2))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();
            LocalDateTime presentDate = LocalDateTime.now();
            experience.setLastModifiedDate(presentDate);
            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
            assertTrue(violations.isEmpty(), "Present date should be valid for last modified date");
        }

        @Test
        void testLastModifiedDateInFuture() {
            User user = createUser();
            Experience experience = Experience.builder()
                    .title("Software Engineer")
                    .company("Tech Company")
                    .employmentType(EmploymentType.FULL_TIME)
                    .startDate(LocalDate.of(2020, 5, 1))
                    .endDate(LocalDate.of(2020, 5, 2))
                    .location("San Francisco")
                    .locationType(LocationType.ON_SITE)
                    .user(user)
                    .build();
            LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
            experience.setLastModifiedDate(futureDate);
            Set<ConstraintViolation<Experience>> violations = validator.validate(experience);

            assertFalse(violations.isEmpty(), "Future date should not be valid for last modified date");
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Last modified date cannot be in the future")),
                    "Should show appropriate error message for future date");
        }
    }
}