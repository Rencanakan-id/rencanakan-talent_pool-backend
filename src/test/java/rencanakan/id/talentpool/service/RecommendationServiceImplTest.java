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
    }

    // POSITIVE TEST CASES

    @Test
    void testCreateRecommendationSuccess() {
        // Arrange
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setContractorId(1L);
        request.setContractorName("Test Contractor");
        request.setMessage("Test recommendation message");

        when(userRepository.findById("user123")).thenReturn(Optional.of(mockTalent));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        // Act
        Recommendation result = recommendationService.createRecommendation(request);

        // Assert
        assertNotNull(result);
        assertEquals(mockTalent, result.getTalent());
        assertEquals(request.getContractorId(), result.getContractorId());
        assertEquals(request.getContractorName(), result.getContractorName());
        assertEquals(request.getMessage(), result.getMessage());
        assertEquals(StatusType.PENDING, result.getStatus());
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithMinimumMessageLength() {
        // Arrange
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(1L);
        request.setContractorId(1L);
        request.setContractorName("Test Contractor");
        request.setMessage("A"); // Minimum valid message

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockTalent));

        Recommendation savedRecommendation = Recommendation.builder()
                .id(UUID.randomUUID().toString())
                .talent(mockTalent)
                .contractorId(request.getContractorId())
                .contractorName(request.getContractorName())
                .message(request.getMessage())
                .status(StatusType.PENDING)
                .build();

        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(savedRecommendation);

        // Act
        Recommendation result = recommendationService.createRecommendation(request);

        // Assert
        assertNotNull(result);
        assertEquals("A", result.getMessage());
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithMaxMessageLength() {
        // Arrange
        String maxLengthMessage = "A".repeat(4000);
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(1L);
        request.setContractorId(1L);
        request.setContractorName("Test Contractor");
        request.setMessage(maxLengthMessage);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockTalent));

        Recommendation savedRecommendation = Recommendation.builder()
                .id(UUID.randomUUID().toString())
                .talent(mockTalent)
                .contractorId(request.getContractorId())
                .contractorName(request.getContractorName())
                .message(request.getMessage())
                .status(StatusType.PENDING)
                .build();

        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(savedRecommendation);

        // Act
        Recommendation result = recommendationService.createRecommendation(request);

        // Assert
        assertNotNull(result);
        assertEquals(4000, result.getMessage().length());
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }

    // NEGATIVE TEST CASES

    @Test
    void testCreateRecommendationTalentNotFound() {
        // Arrange
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(999L);
        request.setContractorId(1L);
        request.setContractorName("Test Contractor");
        request.setMessage("Test recommendation message");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> recommendationService.createRecommendation(request));

        assertEquals("Talent with ID 999 not found", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNullRequest() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(null));

        assertEquals("Recommendation request cannot be null", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNoTalentId() {
        // Arrange
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setContractorId(1L);
        request.setContractorName("Test Contractor");
        request.setMessage("Test recommendation message");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(request));

        assertEquals("Talent ID is required", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNoContractorId() {
        // Arrange
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(1L);
        request.setContractorName("Test Contractor");
        request.setMessage("Test recommendation message");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(request));

        assertEquals("Contractor ID is required", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNoContractorName() {
        // Arrange
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(1L);
        request.setContractorId(1L);
        request.setMessage("Test recommendation message");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(request));

        assertEquals("Contractor name is required", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithEmptyContractorName() {
        // Arrange
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(1L);
        request.setContractorId(1L);
        request.setContractorName("");
        request.setMessage("Test recommendation message");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(request));

        assertEquals("Contractor name cannot be empty", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNoMessage() {
        // Arrange
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(1L);
        request.setContractorId(1L);
        request.setContractorName("Test Contractor");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(request));

        assertEquals("Recommendation message is required", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithEmptyMessage() {
        // Arrange
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(1L);
        request.setContractorId(1L);
        request.setContractorName("Test Contractor");
        request.setMessage("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(request));

        assertEquals("Recommendation message cannot be empty", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    // EDGE CASES

    @Test
    void testCreateRecommendationWithMessageExceedingMaxLength() {
        // Arrange
        String tooLongMessage = "A".repeat(4001); // Exceeds max length by one character
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(1L);
        request.setContractorId(1L);
        request.setContractorName("Test Contractor");
        request.setMessage(tooLongMessage);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockTalent));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(request));

        assertEquals("Message cannot exceed 4000 characters", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithZeroContractorId() {
        // Arrange
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(1L);
        request.setContractorId(0L);
        request.setContractorName("Test Contractor");
        request.setMessage("Test recommendation message");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(request));

        assertEquals("Contractor ID must be greater than 0", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNegativeContractorId() {
        // Arrange
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(1L);
        request.setContractorId(-1L);
        request.setContractorName("Test Contractor");
        request.setMessage("Test recommendation message");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(request));

        assertEquals("Contractor ID must be greater than 0", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithSameContractorAndTalentId() {
        // Arrange
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(1L);
        request.setContractorId(1L); // Same as talent ID
        request.setContractorName("Test Contractor");
        request.setMessage("Test recommendation message");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockTalent));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(request));

        assertEquals("Contractor cannot recommend themselves", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithExactlyMaxMessageLength() {
        // Arrange
        String maxLengthMessage = "A".repeat(4000);
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setTalentId(1L);
        request.setContractorId(2L);
        request.setContractorName("Test Contractor");
        request.setMessage(maxLengthMessage);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockTalent));

        Recommendation savedRecommendation = Recommendation.builder()
                .id(UUID.randomUUID().toString())
                .talent(mockTalent)
                .contractorId(request.getContractorId())
                .contractorName(request.getContractorName())
                .message(request.getMessage())
                .status(StatusType.PENDING)
                .build();

        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(savedRecommendation);

        // Act
        Recommendation result = recommendationService.createRecommendation(request);

        // Assert
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