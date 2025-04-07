package rencanakan.id.talentpool.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.ExperienceRepository;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ExperienceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Nested
    class DeleteExperienceTests {
        
        private Experience experience1;
        private Experience experience2;
        private Experience savedExperience1;
        private Experience savedExperience2;
        private User mockUser;
        
        @BeforeEach
        void setUp() {
            // Create and persist a mock User
            mockUser = new User();
            mockUser.setEmail("test@example.com");
            mockUser.setPassword("password12345");
            mockUser.setFirstName("Test");
            mockUser.setLastName("User");
            mockUser.setPhoneNumber("081234567890");
            mockUser.setNik("1234567890123456");
            entityManager.persist(mockUser);
            
            // Setup first experience
            experience1 = new Experience();
            experience1.setTitle("Software Engineer");
            experience1.setCompany("Tech Company A");
            experience1.setEmploymentType(EmploymentType.FULL_TIME);
            experience1.setStartDate(LocalDate.of(2020, 1, 1));
            experience1.setEndDate(LocalDate.of(2022, 1, 1));
            experience1.setLocation("Jakarta");
            experience1.setLocationType(LocationType.ON_SITE);
            experience1.setUser(mockUser);
            
            // Setup second experience
            experience2 = new Experience();
            experience2.setTitle("Product Manager");
            experience2.setCompany("Tech Company B");
            experience2.setEmploymentType(EmploymentType.FULL_TIME);
            experience2.setStartDate(LocalDate.of(2019, 1, 1));
            experience2.setEndDate(LocalDate.of(2023, 1, 1));
            experience2.setLocation("Surabaya");
            experience2.setLocationType(LocationType.HYBRID);
            experience2.setUser(mockUser);
            
            // Persist experiences
            savedExperience1 = entityManager.persist(experience1);
            savedExperience2 = entityManager.persist(experience2);
            entityManager.flush();
        }

        @Test
        void deleteById_ShouldDeleteSuccessfully_WhenExperienceExists() {
            // Act
            experienceRepository.deleteById(savedExperience1.getId());
            Experience deletedExperience = entityManager.find(Experience.class, savedExperience1.getId());

            // Assert
            assertNull(deletedExperience, "Experience should be deleted from the database");
        }

        @Test
        void deleteById_ShouldNotThrowException_WhenExperienceDoesNotExist() {
            // Arrange
            Long nonExistentId = 999L;

            // Act & Assert
            assertDoesNotThrow(() -> experienceRepository.deleteById(nonExistentId),
                    "Deleting non-existent experience should not throw an exception");
        }

        @Test
        void deleteById_ShouldThrowException_WhenExperienceIdIsNull() {
            // Act & Assert
            assertThrows(InvalidDataAccessApiUsageException.class, () -> experienceRepository.deleteById(null),
                    "Deleting experience with null ID should throw InvalidDataAccessApiUsageException");
        }

        @Test
        void deleteById_ShouldOnlyDeleteTargetExperience() {
            // Act
            experienceRepository.deleteById(savedExperience1.getId());

            // Assert
            assertNull(entityManager.find(Experience.class, savedExperience1.getId()), 
                    "Target experience should be deleted");
            assertNotNull(entityManager.find(Experience.class, savedExperience2.getId()), 
                    "Other experiences should remain in the database");
        }
    }
}