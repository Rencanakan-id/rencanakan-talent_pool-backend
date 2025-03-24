package rencanakan.id.talentpool.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ExperienceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Nested
    class FindByUserIdTests {

        private User user1;
        private User user2;

        @BeforeEach
        void setUp() {
            // Create users
            user1 = new User();
            user1.setEmail("user1@example.com");
            user1.setFirstName("User");
            user1.setLastName("One");
            user1.setPassword("password12345");
            user1.setPhoneNumber("081234567890");
            user1.setNik("1234567890123456");
            user1 = entityManager.persistAndFlush(user1);

            user2 = new User();
            user2.setEmail("user2@example.com");
            user2.setFirstName("User");
            user2.setLastName("Two");
            user2.setPassword("password12345");
            user2.setPhoneNumber("081234567891");
            user2.setNik("1234567890123457");
            user2 = entityManager.persistAndFlush(user2);

            // Create experiences for user1
            Experience experience1 = new Experience();
            experience1.setTitle("Software Engineer");
            experience1.setCompany("Tech Company A");
            experience1.setEmploymentType(EmploymentType.FULL_TIME);
            experience1.setStartDate(LocalDate.of(2020, 1, 1));
            experience1.setEndDate(LocalDate.of(2022, 1, 1));
            experience1.setLocation("Jakarta");
            experience1.setLocationType(LocationType.ON_SITE);
            experience1.setUser(user1);
            entityManager.persist(experience1);

            Experience experience2 = new Experience();
            experience2.setTitle("Senior Developer");
            experience2.setCompany("Tech Company B");
            experience2.setEmploymentType(EmploymentType.FULL_TIME);
            experience2.setStartDate(LocalDate.of(2022, 2, 1));
            experience2.setEndDate(null); // Current job
            experience2.setLocation("Bandung");
            experience2.setLocationType(LocationType.HYBRID);
            experience2.setUser(user1);
            entityManager.persist(experience2);

            // Create experience for user2
            Experience experience3 = new Experience();
            experience3.setTitle("Product Manager");
            experience3.setCompany("Tech Company C");
            experience3.setEmploymentType(EmploymentType.FULL_TIME);
            experience3.setStartDate(LocalDate.of(2019, 1, 1));
            experience3.setEndDate(LocalDate.of(2023, 1, 1));
            experience3.setLocation("Surabaya");
            experience3.setLocationType(LocationType.HYBRID);
            experience3.setUser(user2);
            entityManager.persist(experience3);

            entityManager.flush();
        }

        @Test
        void findByUserId_ShouldReturnExperiences_WhenUserIdExists() {
            // Act
            List<Experience> foundExperiencesUser1 = experienceRepository.findByUserId(user1.getId());

            // Assert
            assertNotNull(foundExperiencesUser1);
            assertEquals(2, foundExperiencesUser1.size());
            assertTrue(foundExperiencesUser1.stream().anyMatch(exp -> exp.getTitle().equals("Software Engineer")));
            assertTrue(foundExperiencesUser1.stream().anyMatch(exp -> exp.getTitle().equals("Senior Developer")));
        }

        @Test
        void findByUserId_ShouldReturnEmptyList_WhenUserIdDoesNotExist() {
            // Act
            List<Experience> foundExperiences = experienceRepository.findByUserId("non-existent-id");

            // Assert
            assertNotNull(foundExperiences);
            assertTrue(foundExperiences.isEmpty());
        }

        @Test
        void findByUserId_ShouldReturnCorrectExperiences_WhenMultipleUserIdsExist() {
            // Act
            List<Experience> user1Experiences = experienceRepository.findByUserId(user1.getId());
            List<Experience> user2Experiences = experienceRepository.findByUserId(user2.getId());

            // Assert
            // Check user1's experiences
            assertNotNull(user1Experiences);
            assertEquals(2, user1Experiences.size());
            assertTrue(user1Experiences.stream().anyMatch(exp -> exp.getTitle().equals("Software Engineer")));
            assertTrue(user1Experiences.stream().anyMatch(exp -> exp.getTitle().equals("Senior Developer")));

            // Check user2's experiences
            assertNotNull(user2Experiences);
            assertEquals(1, user2Experiences.size());
            assertEquals("Product Manager", user2Experiences.get(0).getTitle());
            assertEquals("Tech Company C", user2Experiences.get(0).getCompany());
            assertEquals(EmploymentType.FULL_TIME, user2Experiences.get(0).getEmploymentType());
        }

        @Test
        void findByUserId_ShouldReturnEmptyList_WhenUserIdHasNoExperiences() {
            // Arrange
            User userWithoutExperiences = new User();
            userWithoutExperiences.setEmail("noexp@example.com");
            userWithoutExperiences.setFirstName("No");
            userWithoutExperiences.setLastName("Experience");
            userWithoutExperiences.setPassword("password12345");
            userWithoutExperiences.setPhoneNumber("081234567892");
            userWithoutExperiences.setNik("1234567890123458");
            userWithoutExperiences = entityManager.persistAndFlush(userWithoutExperiences);

            // Act
            List<Experience> foundExperiences = experienceRepository.findByUserId(userWithoutExperiences.getId());

            // Assert
            assertNotNull(foundExperiences);
            assertTrue(foundExperiences.isEmpty());
        }
    }

    @Nested
    class SaveExperienceTests {

        @Test
        void saveExperience_ShouldPersistAndRetrieveExperience() {
            // Arrange
            User user500 = new User();
            user500.setEmail("user500@example.com");
            user500.setFirstName("User");
            user500.setLastName("500");
            user500.setPassword("password12345");
            user500.setPhoneNumber("081234567893");
            user500.setNik("1234567890123459");
            user500 = entityManager.persistAndFlush(user500);

            Experience experience = new Experience();
            experience.setTitle("Full Stack Developer");
            experience.setCompany("Startup XYZ");
            experience.setEmploymentType(EmploymentType.CONTRACT);
            experience.setStartDate(LocalDate.of(2021, 3, 15));
            experience.setEndDate(LocalDate.of(2022, 9, 30));
            experience.setLocation("Yogyakarta");
            experience.setLocationType(LocationType.HYBRID);
            experience.setUser(user500);

            // Act
            Experience savedExperience = experienceRepository.save(experience);
            Experience retrievedExperience = experienceRepository.findById(savedExperience.getId()).orElse(null);

            // Assert
            assertNotNull(retrievedExperience);
            assertEquals("Full Stack Developer", retrievedExperience.getTitle());
            assertEquals("Startup XYZ", retrievedExperience.getCompany());
            assertEquals(EmploymentType.CONTRACT, retrievedExperience.getEmploymentType());
            assertEquals(LocalDate.of(2021, 3, 15), retrievedExperience.getStartDate());
            assertEquals(LocalDate.of(2022, 9, 30), retrievedExperience.getEndDate());
            assertEquals("Yogyakarta", retrievedExperience.getLocation());
            assertEquals(LocationType.HYBRID, retrievedExperience.getLocationType());
            assertEquals(user500.getId(), retrievedExperience.getUser().getId());
        }
    }
}