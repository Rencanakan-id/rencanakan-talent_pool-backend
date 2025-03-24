package rencanakan.id.talentpool.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.RecommendationRepository;
import rencanakan.id.talentpool.repository.UserProfileRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private UserProfileRepository userRepository;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private User talent1;
    private User talent2;
    private Recommendation recommendation1;
    private Recommendation recommendation2;
    private Recommendation recommendation3;
    private List<Recommendation> allRecommendations;
    private List<Recommendation> talent1Recommendations;
    private List<Recommendation> pendingRecommendations;
    private List<Recommendation> acceptedRecommendations;
    private List<Recommendation> declinedRecommendations;
    private List<Recommendation> talent1PendingRecommendations;

    @BeforeEach
    void setUp() {
        setUpUsers();
        setUpRecommendations();
        setUpRecommendationLists();
    }
    
    private void setUpUsers() {
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
        allRecommendations = Arrays.asList(recommendation1, recommendation2, recommendation3);
        talent1Recommendations = Arrays.asList(recommendation1, recommendation2);
        pendingRecommendations = Collections.singletonList(recommendation1);
        acceptedRecommendations = Collections.singletonList(recommendation2);
        declinedRecommendations = Collections.singletonList(recommendation3);
        talent1PendingRecommendations = Collections.singletonList(recommendation1);
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
        void getById_NonExistingId_ThrowsRuntimeException() {
            String id = "non-existing-id";
            when(recommendationRepository.findById(id)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                recommendationService.getById(id);
            });
            
            assertEquals("Recommendation not found with id: " + id, exception.getMessage());
            
            verify(recommendationRepository).findById(id);
        }

        @Test
        void getAll_ReturnsAllRecommendations() {

            when(recommendationRepository.findAll()).thenReturn(allRecommendations);

            List<RecommendationResponseDTO> result = recommendationService.getAll();
            
            assertNotNull(result);
            assertEquals(3, result.size());
            
            RecommendationResponseDTO firstDto = result.stream()
                    .filter(dto -> dto.getId().equals(recommendation1.getId()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(firstDto);
            assertEquals(recommendation1.getMessage(), firstDto.getMessage());
            assertEquals(recommendation1.getStatus(), firstDto.getStatus());
            
            verify(recommendationRepository).findAll();
        }

        @Test
        void getAll_NoRecommendations_ReturnsEmptyList() {

            when(recommendationRepository.findAll()).thenReturn(Collections.emptyList());

            List<RecommendationResponseDTO> result = recommendationService.getAll();
            
            assertNotNull(result);
            assertTrue(result.isEmpty());
            
            verify(recommendationRepository).findAll();
        }

        @Test
        void getByStatus_ExistingStatus_ReturnsFilteredRecommendations() {

            StatusType status = StatusType.PENDING;
            when(recommendationRepository.findByStatus(status)).thenReturn(pendingRecommendations);

            List<RecommendationResponseDTO> result = recommendationService.getByStatus(status);
            
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(status, result.get(0).getStatus());
            assertEquals(recommendation1.getId(), result.get(0).getId());
            
            verify(recommendationRepository).findByStatus(status);
        }

        @Test
        void getByStatus_NoRecommendationsWithStatus_ReturnsEmptyList() {

            StatusType status = StatusType.ACCEPTED;
            when(recommendationRepository.findByStatus(status)).thenReturn(Collections.emptyList());

            List<RecommendationResponseDTO> result = recommendationService.getByStatus(status);
            
            assertNotNull(result);
            assertTrue(result.isEmpty());
            
            verify(recommendationRepository).findByStatus(status);
        }

        @Test
        void getAllGroupedByStatus_ReturnsCorrectlyGroupedRecommendations() {

            when(recommendationRepository.findByStatus(StatusType.PENDING)).thenReturn(pendingRecommendations);
            when(recommendationRepository.findByStatus(StatusType.ACCEPTED)).thenReturn(acceptedRecommendations);
            when(recommendationRepository.findByStatus(StatusType.DECLINED)).thenReturn(declinedRecommendations);

            Map<StatusType, List<RecommendationResponseDTO>> result = recommendationService.getAllGroupedByStatus();
            
            assertNotNull(result);
            assertEquals(StatusType.values().length, result.size());
            assertEquals(1, result.get(StatusType.PENDING).size());
            assertEquals(1, result.get(StatusType.ACCEPTED).size());
            assertEquals(1, result.get(StatusType.DECLINED).size());
            
            assertEquals(recommendation1.getId(), result.get(StatusType.PENDING).get(0).getId());
            assertEquals(recommendation2.getId(), result.get(StatusType.ACCEPTED).get(0).getId());
            assertEquals(recommendation3.getId(), result.get(StatusType.DECLINED).get(0).getId());
            
            verify(recommendationRepository).findByStatus(StatusType.PENDING);
            verify(recommendationRepository).findByStatus(StatusType.ACCEPTED);
            verify(recommendationRepository).findByStatus(StatusType.DECLINED);
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
        void getByTalentId_NonExistingTalentId_ThrowsRuntimeException() {

            String talentId = "non-existing-id";
            when(userRepository.findById(talentId)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
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
        void getByTalentIdAndStatus_NonExistingTalent_ThrowsRuntimeException() {

            String talentId = "non-existing-id";
            StatusType status = StatusType.PENDING;
            when(userRepository.findById(talentId)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
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
        void getByTalentIdAndGroupedByStatus_NonExistingTalent_ThrowsRuntimeException() {

            String talentId = "non-existing-id";
            when(userRepository.findById(talentId)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                recommendationService.getByTalentIdAndGroupedByStatus(talentId);
            });
            
            assertEquals("User not found with id: " + talentId, exception.getMessage());
            
            verify(userRepository).findById(talentId);
            verify(recommendationRepository, never()).findByTalentAndStatus(any(), any());
        }
    }
}
