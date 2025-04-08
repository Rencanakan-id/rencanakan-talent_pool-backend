package rencanakan.id.talentpool.unit.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.persistence.EntityNotFoundException;
import rencanakan.id.talentpool.controller.CertificateController;
import rencanakan.id.talentpool.dto.CertificateRequestDTO;
import rencanakan.id.talentpool.dto.CertificateResponseDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.CertificateService;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CertificateControllerTest {

    @Mock
    private CertificateService certificateService;

    @InjectMocks
    private CertificateController certificateController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private User user;

    private String token;
    private String talentId;
    private Long certificateId;
    private List<CertificateResponseDTO> certificateList;
    private CertificateResponseDTO certificate;

    @BeforeEach
    void setUp() {
        objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        mockMvc = MockMvcBuilders
                .standaloneSetup(certificateController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        user = createUser();

        talentId = "user123";
        certificateId = 1L;

        certificate = new CertificateResponseDTO();
        certificate.setId(certificateId);
        certificate.setTitle("Java Certificate");
        certificate.setFile("certificate.pdf");
        certificate.setTalentId(talentId);

        certificateList = new ArrayList<>();
        certificateList.add(certificate);
    }

    private User createUser() {
        return User.builder()
                .id("talent-123")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
    }

    private CertificateRequestDTO createValidRequestDTO() {
        return CertificateRequestDTO.builder()
                .title("Java Certificate")
                .file("certificate.pdf")
                .build();
    }

    private CertificateResponseDTO createMockResponseDTO() {
        return CertificateResponseDTO.builder()
                .id(1L)
                .title("Java Certificate")
                .file("certificate.pdf")
                .talentId("talent-123")
                .build();
    }

    @Nested
    class CreateCertificateTests {
        @Test
        void createCertificate_Success() throws Exception {
            // Arrange
            CertificateRequestDTO request = createValidRequestDTO();
            CertificateResponseDTO mockResponse = createMockResponseDTO();

            // Use any() for more flexible argument matching
            when(certificateService.create(any(), any(CertificateRequestDTO.class)))
                    .thenReturn(mockResponse);

            // Act & Assert
            mockMvc.perform(post("/certificates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(SecurityMockMvcRequestPostProcessors.user(user))
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1L))
                    .andExpect(jsonPath("$.data.title").value("Java Certificate"))
                    .andExpect(jsonPath("$.data.file").value("certificate.pdf"))
                    .andExpect(jsonPath("$.data.talentId").value("talent-123"));

            // Verify service was called (without strict argument matching)
            verify(certificateService, times(1))
                    .create(any(), any(CertificateRequestDTO.class));
        }

        @Test
        void createCertificate_InvalidRequest_ShouldReturnBadRequest() throws Exception {
            // Arrange
            CertificateRequestDTO invalidRequest = new CertificateRequestDTO(); // Empty request

            // Act & Assert
            mockMvc.perform(post("/certificates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(SecurityMockMvcRequestPostProcessors.user(user))
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(MethodArgumentNotValidException.class)));

            verify(certificateService, never()).create(any(), any());
        }

        @Test
        void createCertificate_WithMissingTitle_ShouldReturnBadRequest() throws Exception {
            // Arrange
            CertificateRequestDTO invalidRequest = CertificateRequestDTO.builder()
                    .file("certificate.pdf")
                    .build(); // Missing title

            // Act & Assert
            mockMvc.perform(post("/certificates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(SecurityMockMvcRequestPostProcessors.user(user))
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(certificateService, never()).create(any(), any());
        }

        @Test
        void createCertificate_WithMissingFile_ShouldReturnBadRequest() throws Exception {
            // Arrange
            CertificateRequestDTO invalidRequest = CertificateRequestDTO.builder()
                    .title("Java Certificate")
                    .build(); // Missing file

            // Act & Assert
            mockMvc.perform(post("/certificates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(SecurityMockMvcRequestPostProcessors.user(user))
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(certificateService, never()).create(any(), any());
        }
    }

    @Nested
    class ReadCertificateTests {
        @Test
        void getCertificatesByUserId_ShouldReturnCertificates() throws Exception {
            when(certificateService.getByUserId(talentId)).thenReturn(certificateList);

            mockMvc.perform(get("/certificates/user/{userId}", talentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(certificateId))
                .andExpect(jsonPath("$.data[0].title").value("Java Certificate"))
                .andExpect(jsonPath("$.data[0].file").value("certificate.pdf"));

            verify(certificateService, times(1)).getByUserId(talentId);
        }

        @Test
        void getCertificateByUserId_WhenNotFound_ShouldReturnNotFound() throws Exception {
            when(certificateService.getByUserId(talentId)).thenThrow(
                new EntityNotFoundException("Certificates not found for user with id " + talentId));

            mockMvc.perform(get("/certificates/user/{userId}", talentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").value("Certificates not found for user with id " + talentId))
                .andExpect(jsonPath("$.data").doesNotExist());

            verify(certificateService, times(1)).getByUserId(talentId);
        }

        @Test
        void getCertificateById_ShouldReturnCertificate() throws Exception {
            when(certificateService.getById(certificateId)).thenReturn(certificate);

            mockMvc.perform(get("/certificates/{id}", certificateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(certificateId))
                .andExpect(jsonPath("$.data.title").value("Java Certificate"))
                .andExpect(jsonPath("$.data.file").value("certificate.pdf"));

            verify(certificateService, times(1)).getById(certificateId);
        }

        @Test
        void getCertificateById_WhenNotFound_ShouldReturnNotFound() throws Exception {
            when(certificateService.getById(certificateId)).thenThrow(
                new EntityNotFoundException("Certificate with id " + certificateId + " not found"));

            mockMvc.perform(get("/certificates/{id}", certificateId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").value("Certificate with id " + certificateId + " not found"))
                .andExpect(jsonPath("$.data").doesNotExist());

            verify(certificateService, times(1)).getById(certificateId);
        }


        @Test
        void getCertificateEndpoints_WhenUserIsNull_ShouldReturnUnauthorized() throws Exception {
            MockMvc mockMvcWithNullUser = MockMvcBuilders
                    .standaloneSetup(certificateController)
                    .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(null))
                    .build();

            mockMvcWithNullUser.perform(get("/certificates/{id}", certificateId))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors").value("Unauthorized access"));

            mockMvcWithNullUser.perform(get("/certificates/user/{userId}", talentId))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors").value("Unauthorized access"));
        }
    }

    @Nested
    class EditCertificateTests {
        private Long certificateId;
        private CertificateRequestDTO requestDTO;
        private CertificateResponseDTO responseDTO;

        @BeforeEach
        void setUp() {
            certificateId = 1L;

            requestDTO = new CertificateRequestDTO();
            requestDTO.setTitle("Updated Java Certificate");
            requestDTO.setFile("updated-certificate.pdf");

            responseDTO = new CertificateResponseDTO();
            responseDTO.setId(certificateId);
            responseDTO.setTitle("Updated Java Certificate");
            responseDTO.setFile("updated-certificate.pdf");
            responseDTO.setTalentId("talent-123");
        }

        @Test
        void editCertificateById_ShouldReturnUpdatedCertificate() throws Exception {
            when(certificateService.editById(eq(certificateId), any(CertificateRequestDTO.class)))
                    .thenReturn(responseDTO);

            mockMvc.perform(put("/certificates/{certificateId}", certificateId)
                            .contentType("application/json")
                            .content("""
                    {
                        "title": "Updated Java Certificate",
                        "file": "updated-certificate.pdf"
                    }
                """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(certificateId))
                    .andExpect(jsonPath("$.data.title").value("Updated Java Certificate"))
                    .andExpect(jsonPath("$.data.file").value("updated-certificate.pdf"));

            verify(certificateService, times(1)).editById(eq(certificateId), any(CertificateRequestDTO.class));
        }

        @Test
        void editCertificateById_WhenCertificateNotFound_ShouldReturn404() throws Exception {
            when(certificateService.editById(eq(certificateId), any(CertificateRequestDTO.class)))
                    .thenThrow(new RuntimeException("Not found"));

            mockMvc.perform(put("/certificates/{certificateId}", certificateId)
                            .principal(() -> "mock-user")
                            .contentType("application/json")
                            .content("""
                    {
                        "title": "Updated Java Certificate",
                        "file": "updated-certificate.pdf"
                    }
                """))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors").value("Certificate not found with ID: " + certificateId));

            verify(certificateService, times(1)).editById(eq(certificateId), any(CertificateRequestDTO.class));
        }

        @Test
        void editCertificateById_WhenValidationFails_ShouldReturn400() throws Exception {
            mockMvc.perform(put("/certificates/{certificateId}", certificateId)
                            .principal(() -> "mock-user")
                            .contentType("application/json")
                            .content("""
                    {
                        "title": "",
                        "file": ""
                    }
                """))
                    .andExpect(status().isBadRequest());
        }

    }

    @Test
    void deleteCertificateById_AllScenarios() throws Exception {
        mockMvc.perform(delete("/certificates/{certificateId}", certificateId)
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andReturn();

        doThrow(new RuntimeException("Sertifikat tidak ditemukan"))
                .when(certificateService).deleteById(any(), any());
        mockMvc.perform(delete("/certificates/{certificateId}", certificateId)
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andReturn();

        doThrow(new RuntimeException("tidak memiliki akses"))
                .when(certificateService).deleteById(any(), any());
        mockMvc.perform(delete("/certificates/{certificateId}", certificateId)
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andReturn();

        doThrow(new RuntimeException("error lain"))
                .when(certificateService).deleteById(any(), any());
        mockMvc.perform(delete("/certificates/{certificateId}", certificateId)
                        .with(SecurityMockMvcRequestPostProcessors.user(user)))
                .andReturn();
    }

    @Nested
    class DeleteCertificateTests {

        private Long certificateId;

        @BeforeEach
        void setUp() {
            certificateId = 1L;
        }

        @Test
        void deleteCertificateById_Success() throws Exception {
            lenient().doNothing().when(certificateService).deleteById(any(), any());

            mockMvc.perform(delete("/certificates/{certificateId}", certificateId)
                            .with(SecurityMockMvcRequestPostProcessors.user(user)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value("Sertifikat berhasil dihapus."));
        }

        @Test
        void deleteCertificateById_WhenCertificateNotFound_ShouldReturnNotFound() throws Exception {
            lenient().doThrow(new RuntimeException("Sertifikat tidak ditemukan"))
                    .when(certificateService).deleteById(any(), any());

            mockMvc.perform(delete("/certificates/{certificateId}", certificateId)
                            .with(SecurityMockMvcRequestPostProcessors.user(user)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors").value("Sertifikat tidak ditemukan"));
        }

        @Test
        void deleteCertificateById_WhenUnauthorizedAccess_ShouldReturnForbidden() throws Exception {
            lenient().doThrow(new RuntimeException("Anda tidak memiliki akses"))
                    .when(certificateService).deleteById(any(), any());

            mockMvc.perform(delete("/certificates/{certificateId}", certificateId)
                            .with(SecurityMockMvcRequestPostProcessors.user(user)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errors").value("Anda tidak memiliki akses"));
        }
    }
}