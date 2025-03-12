package rencanakan.id.talentpool.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ExperienceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Test
    void testFindByTalentId_ShouldReturnExperiences_WhenTalentIdExists() {
        // Arrange
        Experience experience1 = new Experience();
        experience1.setTitle("Software Engineer");
        experience1.setCompany("Tech Company A");
        experience1.setEmploymentType(EmploymentType.FULL_TIME);
        experience1.setStartDate(LocalDate.of(2020, 1, 1));
        experience1.setEndDate(LocalDate.of(2022, 1, 1));
        experience1.setLocation("Jakarta");
        experience1.setLocationType(LocationType.ON_SITE);
        experience1.setTalentId(1L);
        entityManager.persist(experience1);

        Experience experience2 = new Experience();
        experience2.setTitle("Senior Developer");
        experience2.setCompany("Tech Company B");
        experience2.setEmploymentType(EmploymentType.FULL_TIME);
        experience2.setStartDate(LocalDate.of(2022, 2, 1));
        experience2.setEndDate(null); // Current job
        experience2.setLocation("Bandung");
        experience2.setLocationType(LocationType.HYBRID);
        experience2.setTalentId(1L);
        entityManager.persist(experience2);

        Experience experience3 = new Experience();
        experience3.setTitle("Product Manager");
        experience3.setCompany("Tech Company C");
        experience3.setEmploymentType(EmploymentType.FULL_TIME);
        experience3.setStartDate(LocalDate.of(2019, 1, 1));
        experience3.setEndDate(LocalDate.of(2023, 1, 1));
        experience3.setLocation("Surabaya");
        experience3.setLocationType(LocationType.HYBRID);
        experience3.setTalentId(2L);
        entityManager.persist(experience3);

        entityManager.flush();

        // Act
        List<Experience> foundExperiencesTalent1 = experienceRepository.findByTalentId(1L);

        // Assert
        assertNotNull(foundExperiencesTalent1);
        assertEquals(2, foundExperiencesTalent1.size());
        assertTrue(foundExperiencesTalent1.stream().anyMatch(exp -> exp.getTitle().equals("Software Engineer")));
        assertTrue(foundExperiencesTalent1.stream().anyMatch(exp -> exp.getTitle().equals("Senior Developer")));
    }

    @Test
    void testFindByTalentId_ShouldReturnEmptyList_WhenTalentIdDoesNotExist() {
        // Act
        List<Experience> foundExperiences = experienceRepository.findByTalentId(999L); // Non-existent ID

        // Assert
        assertNotNull(foundExperiences);
        assertTrue(foundExperiences.isEmpty());
    }

    @Test
    void testFindByTalentId_ShouldReturnCorrectExperiences_WhenMultipleTalentIdsExist() {
        // Arrange
        // Create experiences for talent with ID 101
        Experience experience1 = new Experience();
        experience1.setTitle("Software Engineer");
        experience1.setCompany("Tech Company A");
        experience1.setEmploymentType(EmploymentType.FULL_TIME);
        experience1.setStartDate(LocalDate.of(2020, 1, 1));
        experience1.setEndDate(LocalDate.of(2022, 1, 1));
        experience1.setLocation("Jakarta");
        experience1.setLocationType(LocationType.ON_SITE);
        experience1.setTalentId(101L);
        entityManager.persist(experience1);

        // Create experiences for talent with ID 102
        Experience experience2 = new Experience();
        experience2.setTitle("Product Manager");
        experience2.setCompany("Tech Company B");
        experience2.setEmploymentType(EmploymentType.FULL_TIME);
        experience2.setStartDate(LocalDate.of(2019, 1, 1));
        experience2.setEndDate(LocalDate.of(2023, 1, 1));
        experience2.setLocation("Surabaya");
        experience2.setLocationType(LocationType.HYBRID);
        experience2.setTalentId(102L);
        entityManager.persist(experience2);

        entityManager.flush();

        // Act
        List<Experience> talent101Experiences = experienceRepository.findByTalentId(101L);
        List<Experience> talent102Experiences = experienceRepository.findByTalentId(102L);

        // Assert
        // Check talent101's experiences
        assertNotNull(talent101Experiences);
        assertEquals(1, talent101Experiences.size());
        assertEquals("Software Engineer", talent101Experiences.get(0).getTitle());
        assertEquals("Tech Company A", talent101Experiences.get(0).getCompany());
        assertEquals(EmploymentType.FULL_TIME, talent101Experiences.get(0).getEmploymentType());

        // Check talent102's experiences
        assertNotNull(talent102Experiences);
        assertEquals(1, talent102Experiences.size());
        assertEquals("Product Manager", talent102Experiences.get(0).getTitle());
        assertEquals("Tech Company B", talent102Experiences.get(0).getCompany());
        assertEquals(EmploymentType.FULL_TIME, talent102Experiences.get(0).getEmploymentType());
    }

    @Test
    void testFindByTalentId_ShouldReturnEmptyList_WhenTalentIdHasNoExperiences() {
        // Arrange - We have experiences in the database but not for talent ID 200
        Experience experience = new Experience();
        experience.setTitle("Software Engineer");
        experience.setCompany("Tech Company A");
        experience.setEmploymentType(EmploymentType.FULL_TIME);
        experience.setStartDate(LocalDate.of(2020, 1, 1));
        experience.setEndDate(LocalDate.of(2022, 1, 1));
        experience.setLocation("Jakarta");
        experience.setLocationType(LocationType.ON_SITE);
        experience.setTalentId(300L);
        entityManager.persist(experience);
        entityManager.flush();

        // Act
        List<Experience> foundExperiences = experienceRepository.findByTalentId(200L); // ID without experiences

        // Assert
        assertNotNull(foundExperiences);
        assertTrue(foundExperiences.isEmpty());
    }

    @Test
    void testSaveExperience_ShouldPersistAndRetrieveExperience() {
        // Arrange
        Experience experience = new Experience();
        experience.setTitle("Full Stack Developer");
        experience.setCompany("Startup XYZ");
        experience.setEmploymentType(EmploymentType.CONTRACT);
        experience.setStartDate(LocalDate.of(2021, 3, 15));
        experience.setEndDate(LocalDate.of(2022, 9, 30));
        experience.setLocation("Yogyakarta");
        experience.setLocationType(LocationType.HYBRID);
        experience.setTalentId(500L);

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
        assertEquals(500L, retrievedExperience.getTalentId());
    }
}