//package rencanakan.id.talentPool.controller;
//
//import rencanakan.id.talentPool.model.Experience;
//import rencanakan.id.talentPool.model.Talent;
//import rencanakan.id.talentPool.service.ExperienceService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//class ExperienceControllerTest {
//
//    @Mock
//    private ExperienceService experienceService;
//
//    @InjectMocks
//    private ExperienceController experienceController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testGetExperiencesByTalentId() throws Exception {
//        // Given: Data dummy untuk talent dan pengalaman
//        String talentId = "talent1";
//        Talent talent = new Talent();
//        talent.setId(talentId);
//
//        Experience exp1 = new Experience();
//        exp1.setId("exp1");
//        exp1.setTitle("Software Engineer");
//        exp1.setCompany("Company A");
//        exp1.setStartDate(new Date());
//        exp1.setTalent(talent);
//
//        Experience exp2 = new Experience();
//        exp2.setId("exp2");
//        exp2.setTitle("Senior Developer");
//        exp2.setCompany("Company B");
//        exp2.setStartDate(new Date());
//        exp2.setTalent(talent);
//
//        List<Experience> experiences = Arrays.asList(exp1, exp2);
//
//        // Ketika service dipanggil, kembalikan dummy data
//        when(experienceService.getExperiencesByTalentId(talentId)).thenReturn(experiences);
//
//        // When & Then: Lakukan request dan validasi response JSON
//        List<Experience> responseEntity = experienceController.getExperiencesByTalentId(talentId);
//
//        assertEquals(2, responseEntity.size());
//        assertEquals("Software Engineer", responseEntity.get(0).getTitle());
//    }
//}
