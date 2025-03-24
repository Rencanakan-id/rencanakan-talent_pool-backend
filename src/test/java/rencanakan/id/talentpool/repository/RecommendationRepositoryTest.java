package rencanakan.id.talentpool.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.persistence.EntityManager;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RecommendationRepositoryTest {

    @Autowired
    private EntityManager entityManager;
        
    @Autowired
    private RecommendationRepository recommendationRepository;
    
    private User talent1;
    private User talent2;
    private Recommendation recommendation1;
    private Recommendation recommendation2;
    private Recommendation recommendation3;
    
    @BeforeEach
    void setUp() {
        recommendationRepository.deleteAll();
        setUpUsers();
        setUpRecommendations();
        entityManager.flush();
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
        entityManager.persist(talent1);

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
        entityManager.persist(talent2);
    }
    
    private void setUpRecommendations() {
        recommendation1 = new Recommendation();
        recommendation1.setTalent(talent1);
        recommendation1.setContractorId(101L);
        recommendation1.setContractorName("Contractor A");
        recommendation1.setMessage("Excellent performance on project X");
        recommendation1.setStatus(StatusType.PENDING);
        entityManager.persist(recommendation1);

        recommendation2 = new Recommendation();
        recommendation2.setTalent(talent1);
        recommendation2.setContractorId(102L);
        recommendation2.setContractorName("Contractor B");
        recommendation2.setMessage("Great collaboration on project Y");
        recommendation2.setStatus(StatusType.ACCEPTED);
        entityManager.persist(recommendation2);

        recommendation3 = new Recommendation();
        recommendation3.setTalent(talent2);
        recommendation3.setContractorId(103L);
        recommendation3.setContractorName("Contractor C");
        recommendation3.setMessage("Good technical skills");
        recommendation3.setStatus(StatusType.DECLINED);
        entityManager.persist(recommendation3);
    }

    @Nested
    class ReadRecommendationTests {

        @Test
        void findByTalent_ExistingTalent_ReturnsMatchingRecommendations() {
            List<Recommendation> result = recommendationRepository.findByTalent(talent1);
            
            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(rec -> rec.getId().equals(recommendation1.getId())));
            assertTrue(result.stream().anyMatch(rec -> rec.getId().equals(recommendation2.getId())));
            assertFalse(result.stream().anyMatch(rec -> rec.getId().equals(recommendation3.getId())));
        }
        
        @Test
        void findByTalent_NonExistingTalent_ReturnsEmptyList() {
            User nonExistingTalent = new User();
            nonExistingTalent.setId("non-existing-id");
            
            List<Recommendation> result = recommendationRepository.findByTalent(nonExistingTalent);
            
            assertTrue(result.isEmpty());
        }
        
        @Test
        void findByStatus_ExistingStatus_ReturnsMatchingRecommendations() {
            List<Recommendation> pendingResults = recommendationRepository.findByStatus(StatusType.PENDING);
            List<Recommendation> acceptedResults = recommendationRepository.findByStatus(StatusType.ACCEPTED);
            List<Recommendation> declinedResults = recommendationRepository.findByStatus(StatusType.DECLINED);
            
            assertEquals(1, pendingResults.size());
            assertEquals(recommendation1.getId(), pendingResults.get(0).getId());
            
            assertEquals(1, acceptedResults.size());
            assertEquals(recommendation2.getId(), acceptedResults.get(0).getId());
            
            assertEquals(1, declinedResults.size());
            assertEquals(recommendation3.getId(), declinedResults.get(0).getId());
        }
        
        @Test
        void findByTalentAndStatus_ValidCombination_ReturnsMatchingRecommendations() {
            List<Recommendation> talent1PendingResults = 
                    recommendationRepository.findByTalentAndStatus(talent1, StatusType.PENDING);
            List<Recommendation> talent1AcceptedResults = 
                    recommendationRepository.findByTalentAndStatus(talent1, StatusType.ACCEPTED);
            List<Recommendation> talent2DeclinedResults = 
                    recommendationRepository.findByTalentAndStatus(talent2, StatusType.DECLINED);
            
            assertEquals(1, talent1PendingResults.size());
            assertEquals(recommendation1.getId(), talent1PendingResults.get(0).getId());
            
            assertEquals(1, talent1AcceptedResults.size());
            assertEquals(recommendation2.getId(), talent1AcceptedResults.get(0).getId());
            
            assertEquals(1, talent2DeclinedResults.size());
            assertEquals(recommendation3.getId(), talent2DeclinedResults.get(0).getId());
        }
        
        @Test
        void findByTalentAndStatus_InvalidCombination_ReturnsEmptyList() {
            List<Recommendation> talent1DeclinedResults = 
                    recommendationRepository.findByTalentAndStatus(talent1, StatusType.DECLINED);
            List<Recommendation> talent2PendingResults = 
                    recommendationRepository.findByTalentAndStatus(talent2, StatusType.PENDING);
            
            assertTrue(talent1DeclinedResults.isEmpty());
            assertTrue(talent2PendingResults.isEmpty());
        }
    }
}
