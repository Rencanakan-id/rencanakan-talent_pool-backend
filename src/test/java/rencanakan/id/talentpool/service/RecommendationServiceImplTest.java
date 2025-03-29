package rencanakan.id.talentpool.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.dto.UserProfileResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.RecommendationRepository;
import rencanakan.id.talentpool.dto.RecommendationRequestDTO;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;

class RecommendationServiceImplTest {

    private static Validator validator;
    private Recommendation recommendation;
    private User mockTalent;
    private UserProfileResponseDTO userResponseDTO;

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationRequestDTO requestDTO;

    @InjectMocks
    private RecommendationResponseDTO responseDTO;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    @InjectMocks
    private DTOMapper dtoMapper;

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
                .id("recommendation123")
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

        responseDTO = new RecommendationResponseDTO();
        responseDTO.setId("recommendation123");
        responseDTO.setContractorId(1L);
        responseDTO.setContractorName("Test Contractor");
        responseDTO.setMessage("Test recommendation message");
        responseDTO.setStatus(StatusType.PENDING);

        userResponseDTO = new UserProfileResponseDTO();
        userResponseDTO.setFirstName("Test");
        userResponseDTO.setLastName("Talent");
        userResponseDTO.setEmail("test@example.setcom");
        userResponseDTO.setPhoneNumber("081234567890");
        userResponseDTO.setNik("1234567890123456");

    }

    // POSITIVE TEST CASES

    @Test
    void testCreateRecommendationSuccess() {
        try (MockedStatic<DTOMapper> mockedMapper = mockStatic(DTOMapper.class)) {

            mockedMapper.when(() -> DTOMapper.map(requestDTO, Recommendation.class)).thenReturn(recommendation);
            mockedMapper.when(() -> DTOMapper.map(recommendation, RecommendationResponseDTO.class)).thenReturn(responseDTO);
            when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

            RecommendationResponseDTO result = recommendationService.createRecommendation(mockTalent, requestDTO);

            assertEquals(recommendation.getMessage(), result.getMessage());
            assertEquals(recommendation.getContractorId(), result.getContractorId());
            assertEquals(recommendation.getContractorName(), result.getContractorName());
            assertEquals(recommendation.getStatus(), result.getStatus());
            verify(recommendationRepository, times(1)).save(any(Recommendation.class));
        }
    }

    @Test
    void testCreateRecommendationWithMinimumMessageLength() {
        try (MockedStatic<DTOMapper> mockedMapper = mockStatic(DTOMapper.class)) {

            requestDTO.setMessage("A");

            RecommendationResponseDTO modifiedResponseDTO = new RecommendationResponseDTO();
            modifiedResponseDTO.setId("recommendation123");
            modifiedResponseDTO.setContractorId(1L);
            modifiedResponseDTO.setContractorName("Test Contractor");
            modifiedResponseDTO.setMessage("A");
            modifiedResponseDTO.setStatus(StatusType.PENDING);

            Recommendation modifiedRecommendation = new Recommendation();
            modifiedRecommendation.setId(recommendation.getId());
            modifiedRecommendation.setContractorId(recommendation.getContractorId());
            modifiedRecommendation.setContractorName(recommendation.getContractorName());
            modifiedRecommendation.setMessage("A");
            modifiedRecommendation.setStatus(recommendation.getStatus());
            modifiedRecommendation.setTalent(recommendation.getTalent());


            mockedMapper.when(() -> DTOMapper.map(requestDTO, Recommendation.class)).thenReturn(modifiedRecommendation);
            mockedMapper.when(() -> DTOMapper.map(modifiedRecommendation, RecommendationResponseDTO.class)).thenReturn(modifiedResponseDTO);
            when(recommendationRepository.save(any(Recommendation.class))).thenReturn(modifiedRecommendation);

            RecommendationResponseDTO result = recommendationService.createRecommendation(mockTalent, requestDTO);

            assertEquals("A", result.getMessage());
            verify(recommendationRepository, times(1)).save(any(Recommendation.class));

        }

    }

    @Test
    void testCreateRecommendationWithMaxMessageLength() {
        try (MockedStatic<DTOMapper> mockedMapper = mockStatic(DTOMapper.class)) {

            String maxLengthMessage = "A".repeat(4000);
            requestDTO.setMessage(maxLengthMessage);

            RecommendationResponseDTO modifiedResponseDTO = new RecommendationResponseDTO();
            modifiedResponseDTO.setId("recommendation123");
            modifiedResponseDTO.setContractorId(1L);
            modifiedResponseDTO.setContractorName("Test Contractor");
            modifiedResponseDTO.setMessage(maxLengthMessage);
            modifiedResponseDTO.setStatus(StatusType.PENDING);

            Recommendation modifiedRecommendation = new Recommendation();
            modifiedRecommendation.setId(recommendation.getId());
            modifiedRecommendation.setContractorId(recommendation.getContractorId());
            modifiedRecommendation.setContractorName(recommendation.getContractorName());
            modifiedRecommendation.setMessage(maxLengthMessage);
            modifiedRecommendation.setStatus(recommendation.getStatus());
            modifiedRecommendation.setTalent(recommendation.getTalent());


            mockedMapper.when(() -> DTOMapper.map(requestDTO, Recommendation.class)).thenReturn(modifiedRecommendation);
            mockedMapper.when(() -> DTOMapper.map(modifiedRecommendation, RecommendationResponseDTO.class)).thenReturn(modifiedResponseDTO);
            when(recommendationRepository.save(any(Recommendation.class))).thenReturn(modifiedRecommendation);

            RecommendationResponseDTO result = recommendationService.createRecommendation(mockTalent, requestDTO);

            assertEquals(4000, result.getMessage().length());
            verify(recommendationRepository, times(1)).save(any(Recommendation.class));

        }

    }

    // NEGATIVE TEST CASES

    @Test
    void testCreateRecommendationWithNullRequest() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(null, null));

        assertEquals("Recommendation request cannot be null", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNoContractorId() {

        requestDTO.setContractorId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(mockTalent, requestDTO));

        assertEquals("Contractor ID is required", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNoContractorName() {

        requestDTO.setContractorName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(mockTalent, requestDTO));

        assertEquals("Contractor name is required", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithEmptyContractorName() {

        requestDTO.setContractorName("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(mockTalent, requestDTO));

        assertEquals("Contractor name cannot be empty", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNoMessage() {

        requestDTO.setMessage(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(mockTalent, requestDTO));

        assertEquals("Recommendation message is required", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithEmptyMessage() {

        requestDTO.setMessage("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(mockTalent, requestDTO));

        assertEquals("Recommendation message cannot be empty", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    // EDGE CASES

    @Test
    void testCreateRecommendationWithMessageExceedingMaxLength() {

        String tooLongMessage = "A".repeat(4001); // Exceeds max length by one character
        requestDTO.setMessage(tooLongMessage);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(mockTalent, requestDTO));

        assertEquals("Message cannot exceed 4000 characters", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithZeroContractorId() {

        requestDTO.setContractorId(0L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(mockTalent, requestDTO));

        assertEquals("Contractor ID must be greater than 0", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithNegativeContractorId() {

        requestDTO.setContractorId(-1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.createRecommendation(mockTalent, requestDTO));

        assertEquals("Contractor ID must be greater than 0", exception.getMessage());
        verify(recommendationRepository, never()).save(any(Recommendation.class));
    }

    @Test
    void testCreateRecommendationWithExactlyMaxMessageLength() {
        try (MockedStatic<DTOMapper> mockedMapper = mockStatic(DTOMapper.class)) {

            String maxLengthMessage = "A".repeat(4000);
            requestDTO.setMessage(maxLengthMessage);

            RecommendationResponseDTO modifiedResponseDTO = new RecommendationResponseDTO();
            modifiedResponseDTO.setId("recommendation123");
            modifiedResponseDTO.setContractorId(1L);
            modifiedResponseDTO.setContractorName("Test Contractor");
            modifiedResponseDTO.setMessage(maxLengthMessage);
            modifiedResponseDTO.setStatus(StatusType.PENDING);

            Recommendation modifiedRecommendation = new Recommendation();
            modifiedRecommendation.setId(recommendation.getId());
            modifiedRecommendation.setContractorId(recommendation.getContractorId());
            modifiedRecommendation.setContractorName(recommendation.getContractorName());
            modifiedRecommendation.setMessage(maxLengthMessage);
            modifiedRecommendation.setStatus(recommendation.getStatus());
            modifiedRecommendation.setTalent(recommendation.getTalent());


            mockedMapper.when(() -> DTOMapper.map(requestDTO, Recommendation.class)).thenReturn(modifiedRecommendation);
            mockedMapper.when(() -> DTOMapper.map(modifiedRecommendation, RecommendationResponseDTO.class)).thenReturn(modifiedResponseDTO);
            when(recommendationRepository.save(any(Recommendation.class))).thenReturn(modifiedRecommendation);

            RecommendationResponseDTO result = recommendationService.createRecommendation(mockTalent, requestDTO);

            assertEquals(4000, result.getMessage().length());
            verify(recommendationRepository, times(1)).save(any(Recommendation.class));

        }

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