package rencanakan.id.talentpool.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rencanakan.id.talentpool.controller.ErrorController;
import rencanakan.id.talentpool.controller.ExperienceController;
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ExperienceControllerTest {

   @Mock
   private ExperienceServiceImpl experienceService;

   @InjectMocks
   private ExperienceController experienceController;

   private MockMvc mockMvc;

   private ObjectMapper objectMapper;

   private ExperienceResponseDTO response;
   private  ExperienceRequestDTO request;

   @BeforeEach
   void setUp() {
       objectMapper = Jackson2ObjectMapperBuilder.json()
               .modules(new JavaTimeModule())
               .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
               .build();

       mockMvc = MockMvcBuilders.standaloneSetup(experienceController)
               .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
               .setControllerAdvice(new ErrorController()) //
               .build();
       request = createValidRequestDTO();
       response= createMockResponseDTO();
   }

   private ExperienceRequestDTO createValidRequestDTO() {
       return ExperienceRequestDTO.builder()
               .title("Lead Construction Project Manager")
               .company("Aman")
               .employmentType(EmploymentType.FULL_TIME)
               .startDate(LocalDate.now())
               .endDate(LocalDate.now().plusDays(1))
               .location("Depok")
               .locationType(LocationType.ON_SITE)
               .build();
   }

   private ExperienceResponseDTO createMockResponseDTO() {
       return ExperienceResponseDTO.builder()
               .id(1L)
               .title("Lead Construction Project Manager")
               .company("Aman")
               .employmentType(EmploymentType.FULL_TIME)
               .startDate(LocalDate.now())
               .endDate(LocalDate.now().plusDays(1))
               .location("Depok")
               .locationType(LocationType.ON_SITE)
               .build();
   }

   @Nested
   class CreateExperienceTest {

       @Test
       void testCreateExperience_Success() throws Exception {
           ExperienceRequestDTO request = createValidRequestDTO();
           ExperienceResponseDTO mockResponseDTO = createMockResponseDTO();

           String expectedStartDate = mockResponseDTO.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
           String expectedEndDate = mockResponseDTO.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);

           when(experienceService.createExperience(any(ExperienceRequestDTO.class)))
                   .thenReturn(mockResponseDTO);

           mockMvc.perform(post("/experiences")
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(request)))
                   .andExpect(jsonPath("$.data.id").value(1L))
                   .andExpect(jsonPath("$.data.title").value("Lead Construction Project Manager"))
                   .andExpect(jsonPath("$.data.company").value("Aman"))
                   .andExpect(jsonPath("$.data.employmentType").value("FULL_TIME"))
                   .andExpect(jsonPath("$.data.startDate").value(expectedStartDate))
                   .andExpect(jsonPath("$.data.endDate").value(expectedEndDate))
                   .andExpect(jsonPath("$.data.location").value("Depok"))
                   .andExpect(jsonPath("$.data.locationType").value("ON_SITE"));
       }

//       @Test
//       void testCreateExperience_BadRequest() throws Exception {
//           ExperienceRequestDTO invalidRequest = new ExperienceRequestDTO();
//
//           mockMvc.perform(post("/experience")
//                           .contentType(MediaType.APPLICATION_JSON)
//                           .content(objectMapper.writeValueAsString(invalidRequest)))
//                   .andExpect(status().isBadRequest());
//       }
   }

   @Nested
   class EditExperienceTest {

       @Test
       void testEditExperience_Success() throws Exception {
           Long experienceId = 1L;

           String expectedStartDate = response.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
           String expectedEndDate = response.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);

           when(experienceService.editById(eq(experienceId), any(ExperienceRequestDTO.class)))
                   .thenReturn(response);

           mockMvc.perform(put("/experiences/" + experienceId)
                           .header("Authorization", "Bearer sample-token")
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(request)))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data.id").value(1L))
                   .andExpect(jsonPath("$.data.title").value("Lead Construction Project Manager"))
                   .andExpect(jsonPath("$.data.company").value("Aman"))
                   .andExpect(jsonPath("$.data.employmentType").value("FULL_TIME"))
                   .andExpect(jsonPath("$.data.startDate").value(expectedStartDate))
                   .andExpect(jsonPath("$.data.endDate").value(expectedEndDate))
                   .andExpect(jsonPath("$.data.location").value("Depok"))
                   .andExpect(jsonPath("$.data.locationType").value("ON_SITE"));
       }
       @Test
       void testValidationError() throws Exception {
           String token = "Bearer sample-token";
           Long id = 1L;
           request.setTitle(null);

           mockMvc.perform(put("/experiences/" + id)
                           .header("Authorization", token)
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(request)))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.errors").exists());
       }



       @Test
       void testEntityNotFoundException() throws Exception {

           Long invalidId = 999L;
           String token = "Bearer sample-token";

           when(experienceService.editById(eq(invalidId), any(ExperienceRequestDTO.class)))
                   .thenThrow(new EntityNotFoundException("Experience Not Found"));

           mockMvc.perform(put("/experiences/" + invalidId)
                           .header("Authorization", token)  // Tambahkan header Authorization
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(request)))
                   .andExpect(status().isNotFound())
                   .andExpect(jsonPath("$.errors").value("Experience Not Found"));  // Pastikan pesan sesuai dengan exception
           verify(experienceService, times(1)).editById(eq(invalidId), any(ExperienceRequestDTO.class));

       }
       @Test
       void testMissingAuthorizationHeader() throws Exception {
           Long id = 999L;

           mockMvc.perform(put("/experiences/" + id)
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(request)))
                   .andExpect(status().isUnauthorized());
       }


   }

//   @Test
//   void testDeleteExperienceById_Success() throws Exception {
//       Long id = 1L;
//       String token = "Bearer sample-token";
//
//       doNothing().when(experienceService).deleteById(id);
//
//       mockMvc.perform(delete("/api/experiences/" + id)
//                       .header("Authorization", token)
//                       .contentType(MediaType.APPLICATION_JSON))
//               .andExpect(status().isOk())
//               .andExpect(jsonPath("$.data").value("Experience with id " + id + " deleted"));
//
//       verify(experienceService, times(1)).deleteById(id);
//   }

//   @Test
//   void testGetDeleteByTalentId_MissingAuthorizationHeader() throws Exception {
//       // Arrange
//       Long id = 10L;
//
//       // Act & Assert - No Authorization header
//       mockMvc.perform(delete("/api/experiences/" + id)
//                       .contentType(MediaType.APPLICATION_JSON))
//               .andExpect(status().isUnauthorized());
//
//       // Verify service was never called
//       verify(experienceService, never()).getByTalentId(anyLong());
//   }
}