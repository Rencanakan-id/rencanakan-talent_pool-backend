package rencanakan.id.talentpool.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.RecommendationRepository;
import rencanakan.id.talentpool.repository.UserProfileRepository;
import rencanakan.id.talentpool.dto.RecommendationRequestDTO;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecommendationServiceImplTest {

    private static Validator validator;
    private Recommendation recommendation;
    private User mockTalent;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private UserProfileRepository userRepository;

    @Mock
    private RecommendationRequestDTO requestDTO;

    private RecommendationServiceImpl recommendationService;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockTalent = User.builder()
                .id("user123")
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
                .build();

        requestDTO = new RecommendationRequestDTO();
        requestDTO.setContractorId(1L);
        requestDTO.setContractorName("Test Contractor");
        requestDTO.setMessage("Test recommendation message");
        requestDTO.setStatus(StatusType.PENDING);

    }

    // POSITIVE TEST CASES

    @Test
    void testCreateRecommendationSuccess() {

        when(recommendationRepository.save(any())).thenReturn(recommendation);

        RecommendationResponseDTO result = recommendationService.createRecommendation(requestDTO);

        assertNotNull(result);
        assertEquals(StatusType.PENDING, result.getStatus());
        assertEquals(requestDTO.getContractorId(), result.getContractorId());
        assertEquals(requestDTO.getContractorName(), result.getContractorName());
        assertEquals(requestDTO.getMessage(), result.getMessage());

        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithMinimumMessageLength() {

        requestDTO.setMessage("A");

        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        RecommendationResponseDTO result = recommendationService.createRecommendation(requestDTO);

        assertNotNull(result);
        assertEquals("A", result.getMessage());
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithMaxMessageLength() {
        String maxLengthMessage = "A".repeat(4000);
        requestDTO.setMessage(maxLengthMessage);

        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        RecommendationResponseDTO result = recommendationService.createRecommendation(requestDTO);

        assertNotNull(result);
        assertEquals(4000, result.getMessage().length());
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }

    // NEGATIVE TEST CASES

    @Test
    void testCreateRecommendationWithNullRequest() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(null));

        assertEquals("Recommendation request cannot be null", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNoContractorId() {

        requestDTO.setContractorId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(requestDTO));

        assertEquals("Contractor ID is required", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNoContractorName() {

        requestDTO.setContractorName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(requestDTO));

        assertEquals("Contractor name is required", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithEmptyContractorName() {

        requestDTO.setContractorName("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(requestDTO));

        assertEquals("Contractor name cannot be empty", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNoMessage() {

        requestDTO.setMessage("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(requestDTO));

        assertEquals("Recommendation message is required", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithEmptyMessage() {

        requestDTO.setMessage("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(requestDTO));

        assertEquals("Recommendation message cannot be empty", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    // EDGE CASES

    @Test
    void testCreateRecommendationWithMessageExceedingMaxLength() {

        String tooLongMessage = "A".repeat(4001); // Exceeds max length by one character
        requestDTO.setMessage(tooLongMessage);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(requestDTO));

        assertEquals("Message cannot exceed 4000 characters", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithZeroContractorId() {

        requestDTO.setContractorId(0L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(requestDTO));

        assertEquals("Contractor ID must be greater than 0", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNegativeContractorId() {

        requestDTO.setContractorId(-1L);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(requestDTO));

        assertEquals("Contractor ID must be greater than 0", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithExactlyMaxMessageLength() {
        // Arrange
        String maxLengthMessage = "A".repeat(4000);
        requestDTO.setMessage(maxLengthMessage);

        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        RecommendationResponseDTO result = recommendationService.createRecommendation(requestDTO);

        assertNotNull(result);
        assertEquals(4000, result.getMessage().length());
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }

    @Test
    void testValidateEmptyMessage() {
        recommendation.setMessage("");
        Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Message is required")));
    }

    @Test
    void testValidateNullMessage() {
        recommendation.setMessage(null);
        Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Message is required")));
    }

    @Test
    void testValidateMessageExceedingMaxLength() {
        String tooLongMessage = "A".repeat(4001);
        recommendation.setMessage(tooLongMessage);
        Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Message cannot exceed 4000 characters")));
    }

    @Test
    void testValidateNullStatus() {
        recommendation.setStatus(null);
        Set<ConstraintViolation<Recommendation>> violations = validator.validate(recommendation);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Status is required")));
    }
}