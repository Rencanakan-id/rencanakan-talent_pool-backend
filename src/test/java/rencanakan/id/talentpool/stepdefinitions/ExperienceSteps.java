package rencanakan.id.talentpool.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.repository.ExperienceRepository;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;

import java.time.LocalDate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class ExperienceSteps {

    @Mock
    private ExperienceServiceImpl experienceService;

    @Mock
    private ExperienceRepository experienceRepository;

    private ExperienceRequestDTO request;

    public ExperienceSteps() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("I have a valid ExperienceRequestDTO")
    public void i_have_a_valid_ExperienceRequestDTO() {
        request = new ExperienceRequestDTO();
        request.setTitle("Lead Construction Project Manager");
        request.setCompany("Aman");
        request.setEmploymentType(EmploymentType.FULL_TIME);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(1));
        request.setLocation("Depok");
        request.setLocationType(LocationType.ON_SITE);
        request.setTalentId(1L);
    }

    @When("I call the createExperience method")
    public void i_call_the_createExperience_method() {
        experienceService.createExperience(request);
    }

    @Then("The experience should be saved successfully")
    public void the_experience_should_be_saved_successfully() {
        verify(experienceService, times(1)).createExperience(request);
    }
}