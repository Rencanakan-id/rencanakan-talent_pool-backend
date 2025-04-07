package rencanakan.id.talentpool.unit.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import rencanakan.id.talentpool.controller.CertificateController;
import rencanakan.id.talentpool.dto.CertificateRequestDTO;
import rencanakan.id.talentpool.dto.CertificateResponseDTO;
import rencanakan.id.talentpool.service.CertificateService;

@ExtendWith(MockitoExtension.class)
class CertificateControllerTest {
    
    @Mock
    private CertificateService certificateService;
    
    @InjectMocks
    private CertificateController certificateController;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(certificateController)
                .build();
    }
    
    @Nested
    class ReadCertificateTests {
        
        private String token;
        private String talentId;
        private Long certificateId;
        private List<CertificateResponseDTO> certificateList;
        private CertificateResponseDTO certificate;
        
        @BeforeEach
        void setUp() {
            token = "Bearer token";
            talentId = "talent-123";
            certificateId = 1L;
            
            certificate = new CertificateResponseDTO();
            certificate.setId(certificateId);
            certificate.setTitle("Java Certificate");
            certificate.setFile("certificate.pdf");
            certificate.setTalentId(talentId);
            
            certificateList = new ArrayList<>();
            certificateList.add(certificate);
        }
        
        @Test
        void getCertificatesByUserId_ShouldReturnCertificates() throws Exception {
            when(certificateService.getByUserId(talentId)).thenReturn(certificateList);
            
            mockMvc.perform(get("/certificates/user/{userId}", talentId)
                    .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(certificateId))
                .andExpect(jsonPath("$.data[0].title").value("Java Certificate"))
                .andExpect(jsonPath("$.data[0].file").value("certificate.pdf"));
            
            verify(certificateService, times(1)).getByUserId(talentId);
        }
        
        @Test
        void getCertificatesByUserId_WhenNoCertificates_ShouldReturnEmptyList() throws Exception {
            List<CertificateResponseDTO> emptyList = new ArrayList<>();
            when(certificateService.getByUserId(talentId)).thenReturn(emptyList);
            
            mockMvc.perform(get("/certificates/user/{userId}", talentId)
                    .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
            
            verify(certificateService, times(1)).getByUserId(talentId);
        }
        
        @Test
        void getCertificateById_ShouldReturnCertificate() throws Exception {
            when(certificateService.getById(certificateId)).thenReturn(certificate);
            
            mockMvc.perform(get("/certificates/{id}", certificateId)
                    .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(certificateId))
                .andExpect(jsonPath("$.data.title").value("Java Certificate"))
                .andExpect(jsonPath("$.data.file").value("certificate.pdf"));
            
            verify(certificateService, times(1)).getById(certificateId);
        }
        
        @Test
        void getCertificateById_WhenNotFound_ShouldReturnNotFound() throws Exception {
            when(certificateService.getById(certificateId)).thenReturn(null);
            
            mockMvc.perform(get("/certificates/{id}", certificateId)
                    .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
            
            verify(certificateService, times(1)).getById(certificateId);
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

}
