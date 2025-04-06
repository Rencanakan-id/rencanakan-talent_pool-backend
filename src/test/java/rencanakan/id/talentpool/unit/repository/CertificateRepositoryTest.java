package rencanakan.id.talentpool.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import rencanakan.id.talentpool.model.Certificate;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.CertificateRepository;
import rencanakan.id.talentpool.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CertificateRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    
    
    @Nested
    class ReadCertificateTests {
        private User testUser;
        private String testUserId;
        
        @BeforeEach
        void setUp() {
            certificateRepository.deleteAll();
            userRepository.deleteAll();
            
            testUser = createUser();
            testUser = entityManager.persistAndFlush(testUser);
            testUserId = testUser.getId();
        }

        private User createUser() {
            return User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
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
        }
        
        @Test
        void findByUserId_WithExistingCertificates_ShouldReturnList() {
            Certificate certificate1 = Certificate.builder()
                    .title("Java Certificate")
                    .file("java-cert.pdf")
                    .user(testUser)
                    .build();
            entityManager.persistAndFlush(certificate1);
            
            Certificate certificate2 = Certificate.builder()
                    .title("Python Certificate")
                    .file("python-cert.pdf")
                    .user(testUser)
                    .build();
            entityManager.persistAndFlush(certificate2);
            
            List<Certificate> certificates = certificateRepository.findByUserId(testUserId);
            
            assertNotNull(certificates);
            assertEquals(2, certificates.size());
            assertTrue(certificates.stream().anyMatch(cert -> cert.getTitle().equals("Java Certificate")));
            assertTrue(certificates.stream().anyMatch(cert -> cert.getTitle().equals("Python Certificate")));
        }
        
        @Test
        void findByUserId_WithNonExistingTalent_ShouldReturnEmptyList() {
            List<Certificate> certificates = certificateRepository.findByUserId("non-existing-id");
            
            assertNotNull(certificates);
            assertTrue(certificates.isEmpty());
        }
        
        @Test
        void findByUserId_WithNoCertificates_ShouldReturnEmptyList() {
            List<Certificate> certificates = certificateRepository.findByUserId(testUserId);
            
            assertNotNull(certificates);
            assertTrue(certificates.isEmpty());
        }

        @Test
        void findById_WithExistingCertificate_ShouldReturnCertificate() {
            Certificate certificate = Certificate.builder()
                    .title("Java Certificate")
                    .file("java-cert.pdf")
                    .user(testUser)
                    .build();
            
            Certificate savedCertificate = certificateRepository.save(certificate);
            
            Optional<Certificate> result = certificateRepository.findById(savedCertificate.getId());
    
            assertTrue(result.isPresent());
            assertEquals("Java Certificate", result.get().getTitle());
            assertEquals("java-cert.pdf", result.get().getFile());
        }
        
        @Test
        void findById_WithNonExistingId_ShouldReturnEmpty() {
            Optional<Certificate> result = certificateRepository.findById(999L);
            
            assertFalse(result.isPresent());
        }
        
        @Test
        void findById_WithNullId_ShouldThrowException() {
            assertThrows(Exception.class, () -> certificateRepository.findById(null));
        }
    }
}
