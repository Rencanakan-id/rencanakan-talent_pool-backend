package rencanakan.id.talentpool.unit.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationTest {

    private static Validator validator;
    private Recommendation recommendation;
    private User mockTalent;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        mockTalent = User.builder()
                .firstName("Test")
                .lastName("Talent")
                .email("test@example.com")
                .password("Password123")
                .phoneNumber("081234567890")
                .nik("1234567890123456")
                .build();

        recommendation = Recommendation.builder()
                .id(UUID.randomUUID().toString())
                .talent(mockTalent)
                .contractorId(1L)
                .contractorName("Test Contractor")
                .message("Test recommendation message")
                .status(StatusType.PENDING)
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    @Test
    void testValidRecommendation() {
        Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
        assertTrue(violations.isEmpty(), "Valid recommendation should have no violations");
    }

    @Nested
    class TalentTests {
        @Test
        void testNullTalent() {
            recommendation.setTalent(null);
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Talent (User) is required")));
        }
    }

    @Nested
    class ContractorTests {
        @Test
        void testNullContractorId() {
            recommendation.setContractorId(null);
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Contractor ID is required")));
        }

        @Test
        void testEmptyContractorName() {
            recommendation.setContractorName("");
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Contractor name is required")));
        }

        @Test
        void testNullContractorName() {
            recommendation.setContractorName(null);
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Contractor name is required")));
        }
    }

    @Nested
    class MessageTests {
        @Test
        void testEmptyMessage() {
            recommendation.setMessage("");
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Message is required")));
        }

        @Test
        void testNullMessage() {
            recommendation.setMessage(null);
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Message is required")));
        }

        @Test
        void testMessageMaxLength() {
            String maxLengthMessage = "A".repeat(4000);
            recommendation.setMessage(maxLengthMessage);
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
            assertTrue(violations.isEmpty(), "Message at max length should be valid");
        }

        @Test
        void testMessageExactlyBelowMaxLength() {
            String validLengthMessage = "A".repeat(3999);
            recommendation.setMessage(validLengthMessage);
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
            assertTrue(violations.isEmpty(), "Message below max length should be valid");
        }

        @Test
        void testMessageExceedingMaxLength() {
            String tooLongMessage = "A".repeat(4001);
            recommendation.setMessage(tooLongMessage);
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Message cannot exceed 4000 characters")));
        }
    }

    @Nested
    class StatusTests {
        @Test
        void testNullStatus() {
            recommendation.setStatus(null);
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Status is required")));
        }

        @Test
        void testAllStatusTypes() {
            for (StatusType status : StatusType.values()) {
                recommendation.setStatus(status);
                Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
                assertTrue(violations.isEmpty(),
                        String.format("Status %s should be valid", status));
            }
        }
    }

    @Nested
    class LastModifiedDateTests {
        @Test
        void testLastModifiedDateNotNull() {
            assertNotNull(recommendation.getLastModifiedDate(),
                    "Last modified date should not be null");
        }

        @Test
        void testLastModifiedDateSetter() {
            LocalDateTime testDate = LocalDateTime.of(2023, 1, 1, 12, 0);
            recommendation.setLastModifiedDate(testDate);
            assertEquals(testDate, recommendation.getLastModifiedDate(),
                    "Should be able to set the last modified date");
        }

        @Test
        void testLastModifiedDateInThePast() {
            LocalDateTime pastDate = LocalDateTime.now().minusDays(5);
            recommendation.setLastModifiedDate(pastDate);
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
            assertTrue(violations.isEmpty(), "Past date should be valid for last modified date");
        }

        @Test
        void testLastModifiedDateInPresent() {
            LocalDateTime presentDate = LocalDateTime.now();
            recommendation.setLastModifiedDate(presentDate);
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
            assertTrue(violations.isEmpty(), "Present date should be valid for last modified date");
        }

        @Test
        void testLastModifiedDateInFuture() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
            recommendation.setLastModifiedDate(futureDate);
            Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);

            assertFalse(violations.isEmpty(), "Future date should not be valid for last modified date");
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Last modified date cannot be in the future")),
                    "Should show appropriate error message for future date");
        }
    }
}
