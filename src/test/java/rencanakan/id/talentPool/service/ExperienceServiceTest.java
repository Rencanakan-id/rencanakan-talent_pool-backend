package rencanakan.id.talentPool.service;


import com.zaxxer.hikari.HikariConfig;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import rencanakan.id.talentPool.dto.ExperienceRequestDTO;
import rencanakan.id.talentPool.enums.EmploymentType;
import rencanakan.id.talentPool.enums.LocationType;
import rencanakan.id.talentPool.model.Experience;
import rencanakan.id.talentPool.repository.ExperienceRepository;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExperienceServiceTest {
    @Mock
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private ExperienceServiceImpl experienceService;

    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        String title = "Lead Construction Project Manager";
        String company = "Aman";
        EmploymentType employmentType = EmploymentType.FULL_TIME;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(1);
        String location = "Depok";
        LocationType locationType = LocationType.ON_SITE;
        Long talentId = 1L;

        ExperienceRequestDTO request = new ExperienceRequestDTO();
        request.setTitle(title);
        request.setCompany(company);
        request.setEmploymentType(employmentType);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setLocation(location);
        request.setLocationType(locationType);
        request.setTalentId(talentId);
    }
    
    @Nested
    class CreateExperienceTest {
        @Test
        public void testCreateExperience_Success() {
            String title = "Lead Construction Project Manager";
            String company = "Aman";
            EmploymentType employmentType = EmploymentType.FULL_TIME;
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(1);
            String location = "Depok";
            LocationType locationType = LocationType.ON_SITE;
            Long talentId = 1L;

            ExperienceRequestDTO request = new ExperienceRequestDTO();
            request.setTitle(title);
            request.setCompany(company);
            request.setEmploymentType(employmentType);
            request.setStartDate(startDate);
            request.setEndDate(endDate);
            request.setLocation(location);
            request.setLocationType(locationType);
            request.setTalentId(talentId);

            when(experienceRepository.save(ArgumentMatchers.any(Experience.class))).thenAnswer(invocation -> {
                Experience experienceToSave = invocation.getArgument(0, Experience.class);
                experienceToSave.setId(1L);
                return experienceToSave;
            });

            Experience result = experienceService.createExperience(request);

            assertNotNull(result, "Result should not be null");
            assertNotNull(result.getId(), "ID should be auto-generated");
            assertEquals(title, result.getTitle(), "Title mismatch");
            assertEquals(company, result.getCompany(), "Company mismatch");
            assertEquals(employmentType, result.getEmploymentType(), "Employment type mismatch");
            assertEquals(startDate, result.getStartDate(), "Start date mismatch");
            assertEquals(endDate, result.getEndDate(), "End date mismatch");
            assertEquals(location, result.getLocation(), "Location mismatch");
            assertEquals(locationType, result.getLocationType(), "Location type mismatch");
            assertEquals(talentId, result.getTalentId(), "Talent ID mismatch");
        }

        private static Stream<String> invalidTitles() {
            return Stream.of(
                    "",
                    "A".repeat(51)
            );
        }

        @ParameterizedTest
        @MethodSource("invalidTitles")
        public void testCreateExperience_TitleInvalid(String title) {
            ExperienceRequestDTO request = new ExperienceRequestDTO();
            request.setTitle(title);
            request.setCompany("Aman");
            request.setEmploymentType(EmploymentType.FULL_TIME);
            request.setStartDate(LocalDate.now());
            request.setEndDate(LocalDate.now().plusDays(1));
            request.setLocation("Depok");
            request.setLocationType(LocationType.ON_SITE);
            request.setTalentId(1L);
        }
    }



}
