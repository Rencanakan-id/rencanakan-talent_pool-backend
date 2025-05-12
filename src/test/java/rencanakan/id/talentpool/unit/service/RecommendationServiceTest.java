package rencanakan.id.talentpool.unit.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;

import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.RecommendationRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import rencanakan.id.talentpool.repository.UserRepository;
import rencanakan.id.talentpool.service.RecommendationServiceImpl;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;


    @Mock
    private UserRepository userRepository;


    private User talent1;
    private User talent2;
    private User mockTalent;
    private RecommendationRequestDTO requestDTO;
    private RecommendationResponseDTO responseDTO;
    private UserResponseDTO userResponseDTO;
    private Recommendation recommendation;
    private Recommendation recommendation1;
    private Recommendation recommendation2;
    private Recommendation recommendation3;
    private List<Recommendation> talent1Recommendations;
    private List<Recommendation> talent1PendingRecommendations;

    @BeforeEach
    void setUp() {
        setUpUsers();
        setUpRecommendations();
        setUpRecommendationLists();

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

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setFirstName("Test");
        userResponseDTO.setLastName("Talent");
        userResponseDTO.setEmail("test@example.setcom");
        userResponseDTO.setPhoneNumber("081234567890");
        userResponseDTO.setNik("1234567890123456");
    }

    private void setUpUsers() {
        mockTalent = User.builder()
                .id("user123")
                .firstName("Test")
                .lastName("Talent")
                .email("test@example.com")
                .password("Password123")
                .phoneNumber("081234567890")
                .nik("1234567890123456")
                .build();

        talent1 = User.builder()
                .firstName("Talent")
                .lastName("One")
                .email("talent1@example.com")
                .password("password123")
                .phoneNumber("1234567890")
                .photo("profile.jpg")
                .aboutMe("About me text")
                .nik("1234567890123456")
                .npwp("12.345.678.9-012.345")
                .photoKtp("ktp.jpg")
                .photoNpwp("npwp.jpg")
                .photoIjazah("ijazah.jpg")
                .experienceYears(5)
                .skkLevel("Intermediate")
                .currentLocation("Jakarta")
                .preferredLocations(Arrays.asList("Jakarta", "Bandung"))
                .skill("Java, Spring Boot")
                .build();

        talent2 = User.builder()
                .firstName("Talent")
                .lastName("Two")
                .email("talent2@example.com")
                .password("password123")
                .phoneNumber("1234567891")
                .photo("profile.jpg")
                .aboutMe("About me text")
                .nik("1234567890123457")
                .npwp("12.345.678.9-012.346")
                .photoKtp("ktp.jpg")
                .photoNpwp("npwp.jpg")
                .photoIjazah("ijazah.jpg")
                .experienceYears(5)
                .skkLevel("Intermediate")
                .currentLocation("Jakarta")
                .preferredLocations(Arrays.asList("Jakarta", "Bandung"))
                .skill("Java, Spring Boot")
                .build();
    }

    private void setUpRecommendations() {
        recommendation = Recommendation.builder()
                .id("recommendation123")
                .talent(mockTalent)
                .contractorId(1L)
                .contractorName("Test Contractor")
                .message("Test recommendation message")
                .status(StatusType.PENDING)
                .build();

        recommendation1 = new Recommendation();
        recommendation1.setId("rec-id-1");
        recommendation1.setTalent(talent1);
        recommendation1.setContractorId(101L);
        recommendation1.setContractorName("Contractor A");
        recommendation1.setMessage("Excellent performance on project X");
        recommendation1.setStatus(StatusType.PENDING);

        recommendation2 = new Recommendation();
        recommendation2.setId("rec-id-2");
        recommendation2.setTalent(talent1);
        recommendation2.setContractorId(102L);
        recommendation2.setContractorName("Contractor B");
        recommendation2.setMessage("Great collaboration on project Y");
        recommendation2.setStatus(StatusType.ACCEPTED);

        recommendation3 = new Recommendation();
        recommendation3.setId("rec-id-3");
        recommendation3.setTalent(talent2);
        recommendation3.setContractorId(103L);
        recommendation3.setContractorName("Contractor C");
        recommendation3.setMessage("Good technical skills");
        recommendation3.setStatus(StatusType.DECLINED);
    }

    void setUpRecommendationLists() {
        talent1Recommendations = Arrays.asList(recommendation1, recommendation2);
        talent1PendingRecommendations = Collections.singletonList(recommendation1);
    }

    @Nested
    class CreateRecommendation {
        private RecommendationResponseDTO testCreateRecommendationWithMessage(String message) {
            try (MockedStatic<DTOMapper> mockedMapper = mockStatic(DTOMapper.class)) {
                // Setup modified DTOs with the custom message
                requestDTO.setMessage(message);

                Recommendation modifiedRecommendation = new Recommendation();
                modifiedRecommendation.setId(recommendation.getId());
                modifiedRecommendation.setContractorId(recommendation.getContractorId());
                modifiedRecommendation.setContractorName(recommendation.getContractorName());
                modifiedRecommendation.setMessage(message);
                modifiedRecommendation.setStatus(recommendation.getStatus());
                modifiedRecommendation.setTalent(recommendation.getTalent());

                RecommendationResponseDTO modifiedResponseDTO = new RecommendationResponseDTO();
                modifiedResponseDTO.setId("recommendation123");
                modifiedResponseDTO.setTalentId("user123");
                modifiedResponseDTO.setContractorId(1L);
                modifiedResponseDTO.setContractorName("Test Contractor");
                modifiedResponseDTO.setMessage(message);
                modifiedResponseDTO.setStatus(StatusType.PENDING);

                // Setup mocks
                mockedMapper.when(() -> DTOMapper.map(requestDTO, Recommendation.class)).thenReturn(modifiedRecommendation);
                mockedMapper.when(() -> DTOMapper.map(modifiedRecommendation, RecommendationResponseDTO.class)).thenReturn(modifiedResponseDTO);
                when(recommendationRepository.save(any(Recommendation.class))).thenReturn(modifiedRecommendation);

                // Execute the method
                RecommendationResponseDTO result = recommendationService.createRecommendation(mockTalent.getId(), requestDTO);

                // Verify repository was called
                verify(recommendationRepository, times(1)).save(any(Recommendation.class));

                return result;
            }
        }

        // POSITIVE TEST CASES

        @Test
        void testCreateRecommendationSuccess() {
            try (MockedStatic<DTOMapper> mockedMapper = mockStatic(DTOMapper.class)) {
                mockedMapper.when(() -> DTOMapper.map(userResponseDTO, User.class)).thenReturn(mockTalent);
                mockedMapper.when(() -> DTOMapper.map(requestDTO, Recommendation.class)).thenReturn(recommendation);
                mockedMapper.when(() -> DTOMapper.map(recommendation, RecommendationResponseDTO.class)).thenReturn(responseDTO);
                when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

                RecommendationResponseDTO result = recommendationService.createRecommendation(mockTalent.getId(), requestDTO);

                assertNotNull(result);
                assertEquals("recommendation123", result.getId());
                assertEquals("Test Contractor", result.getContractorName());
                assertEquals("Test recommendation message", result.getMessage());
                assertEquals(StatusType.PENDING, result.getStatus());
                verify(recommendationRepository, times(1)).save(any(Recommendation.class));

            }
        }

        @Test
        void testCreateRecommendationWithMinimumMessageLength() {
            RecommendationResponseDTO result = testCreateRecommendationWithMessage("A");
            assertEquals("A", result.getMessage());
        }

        @Test
        void testCreateRecommendationWithMaxMessageLength() {
            String maxLengthMessage = "A".repeat(4000);
            RecommendationResponseDTO result = testCreateRecommendationWithMessage(maxLengthMessage);
            assertEquals(4000, result.getMessage().length());
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
        void testCreateRecommendationWithNullTalent() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> recommendationService.createRecommendation(null, requestDTO));

            assertEquals("Recommendation request cannot be null", exception.getMessage());
            verify(recommendationRepository, never()).save(any(Recommendation.class));
        }

        @Test
        void testCreateRecommendationWithNullRecommendation() {
            String talentId = mockTalent.getId();
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> recommendationService.createRecommendation(talentId, null));

            assertEquals("Recommendation request cannot be null", exception.getMessage());
            verify(recommendationRepository, never()).save(any(Recommendation.class));
        }
    }

    @Nested
    class DeleteRecommendation{
        @Test
        void deleteById_Success() {
            when(recommendationRepository.findById(recommendation1.getId())).thenReturn(Optional.of(recommendation1));
            User mockedTalent = mock(User.class);
            recommendation1.setTalent(mockedTalent);
            when(mockedTalent.getId()).thenReturn("idUser");
            RecommendationResponseDTO response = recommendationService.deleteById( "idUser", recommendation1.getId());

            verify(recommendationRepository, times(1)).deleteById(recommendation1.getId());

            assertNotNull(response);
            assertEquals(recommendation1.getId(), response.getId());
            assertEquals(StatusType.PENDING, response.getStatus());
        }
        @Test
        void deleteById_NotFound_ThrowsException() {

            when(recommendationRepository.findById("999")).thenReturn(Optional.empty());
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                recommendationService.deleteById("userId","999");
            });

            assertEquals("Recommendation with id 999 not found.", exception.getMessage());

            verify(recommendationRepository, never()).deleteById("999");
        }

        @Test
        void testDeleteById_UnAuthorized() {
            // Arrange
            User mockedTalent = mock(User.class);
            recommendation1.setTalent(mockedTalent);
            when(mockedTalent.getId()).thenReturn("idUser");
            when(recommendationRepository.findById(eq(recommendation1.getId()))).thenReturn(Optional.of(recommendation1));

            // Act & Assert
            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                recommendationService.deleteById("idUser123", recommendation1.getId());
            });

            assertEquals("You are not allowed to delete this recommendation.", exception.getMessage());
            verify(recommendationRepository, never()).save(any());
        }
    }
    @Nested
    class patchStatus{

        @Test
        void testEditStatusById_Success() {
            // Arrange
            Recommendation updated = new Recommendation();
            updated.setId("rec-id-1");
            updated.setStatus(StatusType.ACCEPTED);

            User mockedTalent = mock(User.class);
            recommendation1.setTalent(mockedTalent);
            when(mockedTalent.getId()).thenReturn("idUser");
            when(recommendationRepository.findById(any())).thenReturn(Optional.of(recommendation1));

            when(recommendationRepository.save(any(Recommendation.class))).thenReturn(updated);

            // Act
            RecommendationResponseDTO response = recommendationService.editStatusById("idUser", recommendation1.getId(), StatusType.ACCEPTED);

            // Assert
            assertNotNull(response);
            assertEquals(StatusType.ACCEPTED, response.getStatus());
            verify(recommendationRepository, times(1)).save(recommendation1);
        }

        @Test
        void testEditStatusById_RecommendationNotFound() {
            // Arrange
            when(recommendationRepository.findById("1")).thenReturn(Optional.empty());

            // Act + Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                    recommendationService.editStatusById( "idUser","1", StatusType.ACCEPTED)
            );

            assertEquals("Recommendation with ID 1 not found", exception.getMessage());
        }


        @Test
        void testEditStatusById_UnAuthorized() {
            // Arrange
            User mockedTalent = mock(User.class);
            recommendation1.setTalent(mockedTalent);
            when(mockedTalent.getId()).thenReturn("idUser");
            when(recommendationRepository.findById(eq(recommendation1.getId()))).thenReturn(Optional.of(recommendation1));

            // Act & Assert
            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                recommendationService.editStatusById("idUser123", recommendation1.getId(), StatusType.ACCEPTED);
            });

            assertEquals("You are not allowed to edit this recommendation.", exception.getMessage());
            verify(recommendationRepository, never()).save(any());
        }
    }

    @Nested
    class EditRecommendationTests {
        
        @Test
        void editById_Success() {
            String userId = "contractor-id";
            String recommendationId = "rec-id-1";
            
            RecommendationRequestDTO editRequest = new RecommendationRequestDTO();
            editRequest.setContractorId(101L);
            editRequest.setContractorName("Contractor A");
            editRequest.setMessage("Updated message");
            editRequest.setStatus(StatusType.PENDING);
            
            recommendation1.setStatus(StatusType.ACCEPTED);
            
            when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(recommendation1));
            
            Recommendation updatedRecommendation = new Recommendation();
            updatedRecommendation.setId(recommendationId);
            updatedRecommendation.setTalent(talent1);
            updatedRecommendation.setContractorId(101L);
            updatedRecommendation.setContractorName("Contractor A");
            updatedRecommendation.setMessage("Updated message");
            updatedRecommendation.setStatus(StatusType.PENDING); // Reset to PENDING
            
            when(recommendationRepository.save(any(Recommendation.class))).thenReturn(updatedRecommendation);
            
            RecommendationResponseDTO result = recommendationService.editById(userId, recommendationId, editRequest);
            
            assertNotNull(result);
            assertEquals(recommendationId, result.getId());
            assertEquals("Updated message", result.getMessage());
            assertEquals(StatusType.PENDING, result.getStatus());
            
            verify(recommendationRepository).findById(recommendationId);
            verify(recommendationRepository).save(recommendation1);
        }
        
        @Test
        void editById_RecommendationNotFound() {
            String userId = "contractor-id";
            String nonExistingId = "non-existing-id";
            
            RecommendationRequestDTO editRequest = new RecommendationRequestDTO();
            editRequest.setContractorId(101L);
            editRequest.setContractorName("Contractor A");
            editRequest.setMessage("Updated message");
            editRequest.setStatus(StatusType.PENDING);
            
            when(recommendationRepository.findById(nonExistingId)).thenReturn(Optional.empty());
            
            // Act & Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                recommendationService.editById(userId, nonExistingId, editRequest);
            });
            
            assertEquals("Recommendation with ID " + nonExistingId + " not found", exception.getMessage());
            
            verify(recommendationRepository).findById(nonExistingId);
            verify(recommendationRepository, never()).save(any());
        }
        
        @Test
        void editById_ContractorMismatch() {
            // Arrange
            String userId = "contractor-id";
            String recommendationId = "rec-id-1";
            
            RecommendationRequestDTO editRequest = new RecommendationRequestDTO();
            editRequest.setContractorId(999L); // Different contractor ID
            editRequest.setContractorName("Different Contractor");
            editRequest.setMessage("Updated message");
            editRequest.setStatus(StatusType.PENDING);
            
            when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(recommendation1));
            
            // Act & Assert
            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                recommendationService.editById(userId, recommendationId, editRequest);
            });
            
            assertEquals("Only the contractor who created this recommendation can edit it", exception.getMessage());
            
            verify(recommendationRepository).findById(recommendationId);
            verify(recommendationRepository, never()).save(any());
        }
        
        @Test
        void editById_KeepCurrentStatus() {
            String userId = "contractor-id";
            String recommendationId = "rec-id-1";
            
            recommendation1.setStatus(StatusType.PENDING);
            
            RecommendationRequestDTO editRequest = new RecommendationRequestDTO();
            editRequest.setContractorId(101L);
            editRequest.setContractorName("Contractor A");
            editRequest.setMessage("Updated message only");
            editRequest.setStatus(StatusType.PENDING); // Same status
            
            when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(recommendation1));
            
            Recommendation updatedRecommendation = new Recommendation();
            updatedRecommendation.setId(recommendationId);
            updatedRecommendation.setTalent(talent1);
            updatedRecommendation.setContractorId(101L);
            updatedRecommendation.setContractorName("Contractor A");
            updatedRecommendation.setMessage("Updated message only");
            updatedRecommendation.setStatus(StatusType.PENDING); // Status unchanged
            
            when(recommendationRepository.save(any(Recommendation.class))).thenReturn(updatedRecommendation);
            
            RecommendationResponseDTO result = recommendationService.editById(userId, recommendationId, editRequest);
            
            assertNotNull(result);
            assertEquals(recommendationId, result.getId());
            assertEquals("Updated message only", result.getMessage());
            assertEquals(StatusType.PENDING, result.getStatus());
            
            verify(recommendationRepository).findById(recommendationId);
            verify(recommendationRepository).save(recommendation1);
        }
        
        @Test
        void editById_ResetFromAcceptedToPending() {
            // Arrange
            String userId = "contractor-id";
            String recommendationId = "rec-id-1";
            
            // Original recommendation is ACCEPTED
            recommendation1.setStatus(StatusType.ACCEPTED);
            
            RecommendationRequestDTO editRequest = new RecommendationRequestDTO();
            editRequest.setContractorId(101L);
            editRequest.setContractorName("Contractor A");
            editRequest.setMessage("Updated message");
            editRequest.setStatus(StatusType.DECLINED); // Different status
            
            when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(recommendation1));
            
            Recommendation updatedRecommendation = new Recommendation();
            updatedRecommendation.setId(recommendationId);
            updatedRecommendation.setTalent(talent1);
            updatedRecommendation.setContractorId(101L);
            updatedRecommendation.setContractorName("Contractor A");
            updatedRecommendation.setMessage("Updated message");
            updatedRecommendation.setStatus(StatusType.PENDING); // Reset to PENDING
            
            when(recommendationRepository.save(any(Recommendation.class))).thenReturn(updatedRecommendation);
            
            RecommendationResponseDTO result = recommendationService.editById(userId, recommendationId, editRequest);
            
            assertNotNull(result);
            assertEquals(recommendationId, result.getId());
            assertEquals("Updated message", result.getMessage());
            assertEquals(StatusType.PENDING, result.getStatus()); // Reset to PENDING
            
            verify(recommendationRepository).findById(recommendationId);
            verify(recommendationRepository).save(recommendation1);
        }
        
        @Test
        void editById_ResetFromDeclinedToPending() {
            String userId = "contractor-id";
            String recommendationId = "rec-id-3";
            
            recommendation3.setStatus(StatusType.DECLINED);
            
            RecommendationRequestDTO editRequest = new RecommendationRequestDTO();
            editRequest.setContractorId(103L);
            editRequest.setContractorName("Contractor C");
            editRequest.setMessage("Updated declined recommendation");
            editRequest.setStatus(StatusType.ACCEPTED); // Different status
            
            when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(recommendation3));
            
            Recommendation updatedRecommendation = new Recommendation();
            updatedRecommendation.setId(recommendationId);
            updatedRecommendation.setTalent(talent2);
            updatedRecommendation.setContractorId(103L);
            updatedRecommendation.setContractorName("Contractor C");
            updatedRecommendation.setMessage("Updated declined recommendation");
            updatedRecommendation.setStatus(StatusType.PENDING); // Reset to PENDING
            
            when(recommendationRepository.save(any(Recommendation.class))).thenReturn(updatedRecommendation);
            
            RecommendationResponseDTO result = recommendationService.editById(userId, recommendationId, editRequest);
            
            assertNotNull(result);
            assertEquals(recommendationId, result.getId());
            assertEquals("Updated declined recommendation", result.getMessage());
            assertEquals(StatusType.PENDING, result.getStatus()); // Reset to PENDING
            
            verify(recommendationRepository).findById(recommendationId);
            verify(recommendationRepository).save(recommendation3);
        }
    }

    @Nested
    class ReadRecommendationTests {

        @Test
        void getById_ExistingId_ReturnsCorrectRecommendation() {

            String id = "rec-id-1";
            when(recommendationRepository.findById(id)).thenReturn(Optional.of(recommendation1));

            RecommendationResponseDTO result = recommendationService.getById(id);

            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals(recommendation1.getMessage(), result.getMessage());
            assertEquals(recommendation1.getContractorName(), result.getContractorName());
            assertEquals(recommendation1.getStatus(), result.getStatus());
            assertEquals(talent1.getId(), result.getTalentId());

            verify(recommendationRepository).findById(id);
        }

        @Test
        void getById_NonExistingId_ThrowsEntityNotFoundException() {
            String id = "non-existing-id";
            when(recommendationRepository.findById(id)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                recommendationService.getById(id);
            });

            assertEquals("Recommendation not found with id: " + id, exception.getMessage());

            verify(recommendationRepository).findById(id);
        }

        @Test
        void getByTalentId_ExistingTalentId_ReturnsFilteredRecommendations() {

            String talentId = "user-id-1";
            when(userRepository.findById(talentId)).thenReturn(Optional.of(talent1));
            when(recommendationRepository.findByTalent(talent1)).thenReturn(talent1Recommendations);

            List<RecommendationResponseDTO> result = recommendationService.getByTalentId(talentId);

            assertNotNull(result);
            assertEquals(2, result.size());

            List<String> recommendationIds = result.stream().map(RecommendationResponseDTO::getId).toList();
            assertTrue(recommendationIds.contains(recommendation1.getId()));
            assertTrue(recommendationIds.contains(recommendation2.getId()));

            verify(userRepository).findById(talentId);
            verify(recommendationRepository).findByTalent(talent1);
        }

        @Test
        void getByTalentId_NonExistingTalentId_ThrowsEntityNotFoundException() {

            String talentId = "non-existing-id";
            when(userRepository.findById(talentId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                recommendationService.getByTalentId(talentId);
            });

            assertEquals("User not found with id: " + talentId, exception.getMessage());

            verify(userRepository).findById(talentId);
            verify(recommendationRepository, never()).findByTalent(any());
        }

        @Test
        void getByTalentIdAndStatus_ExistingTalentAndStatus_ReturnsFilteredRecommendations() {

            String talentId = "user-id-1";
            StatusType status = StatusType.PENDING;
            when(userRepository.findById(talentId)).thenReturn(Optional.of(talent1));
            when(recommendationRepository.findByTalentAndStatus(talent1, status)).thenReturn(talent1PendingRecommendations);

            List<RecommendationResponseDTO> result = recommendationService.getByTalentIdAndStatus(talentId, status);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(recommendation1.getId(), result.get(0).getId());
            assertEquals(status, result.get(0).getStatus());

            verify(userRepository).findById(talentId);
            verify(recommendationRepository).findByTalentAndStatus(talent1, status);
        }

        @Test
        void getByTalentIdAndStatus_NonExistingTalent_ThrowsEntityNotFoundException() {

            String talentId = "non-existing-id";
            StatusType status = StatusType.PENDING;
            when(userRepository.findById(talentId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                recommendationService.getByTalentIdAndStatus(talentId, status);
            });

            assertEquals("User not found with id: " + talentId, exception.getMessage());

            verify(userRepository).findById(talentId);
            verify(recommendationRepository, never()).findByTalentAndStatus(any(), any());
        }

        @Test
        void getByTalentIdAndGroupedByStatus_ExistingTalent_ReturnsCorrectlyGroupedRecommendations() {

            String talentId = "user-id-1";
            when(userRepository.findById(talentId)).thenReturn(Optional.of(talent1));
            when(recommendationRepository.findByTalentAndStatus(talent1, StatusType.PENDING))
                    .thenReturn(talent1PendingRecommendations);
            when(recommendationRepository.findByTalentAndStatus(talent1, StatusType.ACCEPTED))
                    .thenReturn(Collections.singletonList(recommendation2));
            when(recommendationRepository.findByTalentAndStatus(talent1, StatusType.DECLINED))
                    .thenReturn(Collections.emptyList());

            Map<StatusType, List<RecommendationResponseDTO>> result =
                    recommendationService.getByTalentIdAndGroupedByStatus(talentId);

            assertNotNull(result);
            assertEquals(StatusType.values().length, result.size());
            assertEquals(1, result.get(StatusType.PENDING).size());
            assertEquals(1, result.get(StatusType.ACCEPTED).size());
            assertEquals(0, result.get(StatusType.DECLINED).size());

            assertEquals(recommendation1.getId(), result.get(StatusType.PENDING).get(0).getId());
            assertEquals(recommendation2.getId(), result.get(StatusType.ACCEPTED).get(0).getId());

            verify(userRepository, times(3)).findById(talentId);
            verify(recommendationRepository).findByTalentAndStatus(talent1, StatusType.PENDING);
            verify(recommendationRepository).findByTalentAndStatus(talent1, StatusType.ACCEPTED);
            verify(recommendationRepository).findByTalentAndStatus(talent1, StatusType.DECLINED);
        }

        @Test
        void getByTalentIdAndGroupedByStatus_NonExistingTalent_ThrowsEntityNotFoundException() {

            String talentId = "non-existing-id";
            when(userRepository.findById(talentId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                recommendationService.getByTalentIdAndGroupedByStatus(talentId);
            });

            assertEquals("User not found with id: " + talentId, exception.getMessage());

            verify(userRepository).findById(talentId);
            verify(recommendationRepository, never()).findByTalentAndStatus(any(), any());
        }
    }
}
