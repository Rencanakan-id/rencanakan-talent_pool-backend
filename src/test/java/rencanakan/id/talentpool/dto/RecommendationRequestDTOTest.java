package rencanakan.id.talentpool.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rencanakan.id.talentpool.enums.StatusType;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructorDefaults() {
        RecommendationRequestDTO dto = new RecommendationRequestDTO();
        assertNull(dto.getContractorId());
        assertNull(dto.getContractorName());
        assertNull(dto.getMessage());
        assertNull(dto.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        RecommendationRequestDTO dto = new RecommendationRequestDTO(
                1L,
                "Contractor Name",
                "Test message",
                StatusType.PENDING);

        assertEquals(1L, dto.getContractorId());
        assertEquals("Contractor Name", dto.getContractorName());
        assertEquals("Test message", dto.getMessage());
        assertEquals(StatusType.PENDING, dto.getStatus());
    }

    @Test
    void testBuilder() {
        RecommendationRequestDTO dto = RecommendationRequestDTO.builder()
                .contractorId(2L)
                .contractorName("Builder Contractor")
                .message("Test builder message")
                .status(StatusType.ACCEPTED)
                .build();

        assertEquals(2L, dto.getContractorId());
        assertEquals("Builder Contractor", dto.getContractorName());
        assertEquals("Test builder message", dto.getMessage());
        assertEquals(StatusType.ACCEPTED, dto.getStatus());
    }

    @Test
    void testSettersAndGetters() {
        RecommendationRequestDTO dto = new RecommendationRequestDTO();

        dto.setContractorId(3L);
        dto.setContractorName("Setter Contractor");
        dto.setMessage("Test setter message");
        dto.setStatus(StatusType.DECLINED);

        assertEquals(3L, dto.getContractorId());
        assertEquals("Setter Contractor", dto.getContractorName());
        assertEquals("Test setter message", dto.getMessage());
        assertEquals(StatusType.DECLINED, dto.getStatus());
    }

    @Test
    void testValidDTO() {
        RecommendationRequestDTO dto = RecommendationRequestDTO.builder()
                .contractorId(1L)
                .contractorName("Valid Contractor")
                .message("Valid message")
                .status(StatusType.PENDING)
                .build();

        Set<ConstraintViolation<RecommendationRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid DTO should not have violations");
    }

    @Test
    void testNullContractorId() {
        RecommendationRequestDTO dto = RecommendationRequestDTO.builder()
                .contractorName("Valid Contractor")
                .message("Valid message")
                .status(StatusType.PENDING)
                .build();

        Set<ConstraintViolation<RecommendationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Contractor ID is required")));
    }

    @Test
    void testEmptyContractorName() {
        RecommendationRequestDTO dto = RecommendationRequestDTO.builder()
                .contractorId(1L)
                .contractorName("")
                .message("Valid message")
                .status(StatusType.PENDING)
                .build();

        Set<ConstraintViolation<RecommendationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Contractor name is required")));
    }

    @Test
    void testNullContractorName() {
        RecommendationRequestDTO dto = RecommendationRequestDTO.builder()
                .contractorId(1L)
                .message("Valid message")
                .status(StatusType.PENDING)
                .build();

        Set<ConstraintViolation<RecommendationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Contractor name is required")));
    }

    @Test
    void testEmptyMessage() {
        RecommendationRequestDTO dto = RecommendationRequestDTO.builder()
                .contractorId(1L)
                .contractorName("Valid Contractor")
                .message("")
                .status(StatusType.PENDING)
                .build();

        Set<ConstraintViolation<RecommendationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Message is required")));
    }

    @Test
    void testNullMessage() {
        RecommendationRequestDTO dto = RecommendationRequestDTO.builder()
                .contractorId(1L)
                .contractorName("Valid Contractor")
                .status(StatusType.PENDING)
                .build();

        Set<ConstraintViolation<RecommendationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Message is required")));
    }

    @Test
    void testMessageMaxLength() {
        String maxLengthMessage = "A".repeat(4000);
        RecommendationRequestDTO dto = RecommendationRequestDTO.builder()
                .contractorId(1L)
                .contractorName("Valid Contractor")
                .message(maxLengthMessage)
                .status(StatusType.PENDING)
                .build();

        Set<ConstraintViolation<RecommendationRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Message at max length should be valid");
    }

    @Test
    void testMessageExactlyBelowMaxLength() {
        String maxLengthMessage = "A".repeat(3999);
        RecommendationRequestDTO dto = RecommendationRequestDTO.builder()
                .contractorId(1L)
                .contractorName("Valid Contractor")
                .message(maxLengthMessage)
                .status(StatusType.PENDING)
                .build();

        Set<ConstraintViolation<RecommendationRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Message at max length should be valid");
    }

    @Test
    void testMessageExceedingMaxLength() {
        String tooLongMessage = "A".repeat(4001);
        RecommendationRequestDTO dto = RecommendationRequestDTO.builder()
                .contractorId(1L)
                .contractorName("Valid Contractor")
                .message(tooLongMessage)
                .status(StatusType.PENDING)
                .build();

        Set<ConstraintViolation<RecommendationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Message cannot exceed 4000 characters")));
    }

    @Test
    void testNullStatus() {
        RecommendationRequestDTO dto = RecommendationRequestDTO.builder()
                .contractorId(1L)
                .contractorName("Valid Contractor")
                .message("Valid message")
                .build();

        Set<ConstraintViolation<RecommendationRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Status is required")));
    }
}
