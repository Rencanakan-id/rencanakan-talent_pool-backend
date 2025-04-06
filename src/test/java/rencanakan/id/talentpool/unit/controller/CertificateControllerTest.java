package rencanakan.id.talentpool.unit.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
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
import rencanakan.id.talentpool.dto.CertificateResponseDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.CertificateService;

@ExtendWith(MockitoExtension.class)
class CertificateControllerTest {
    
    @Mock
    private CertificateService certificateService;
    
    @InjectMocks
    private CertificateController certificateController;
    
    private MockMvc mockMvc;

    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user123");
        testUser.setEmail("john.doe@example.com");

        mockMvc = MockMvcBuilders
                .standaloneSetup(certificateController)
                .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(testUser))
                .build();
    }
    
    @Nested
    class ReadCertificateTests {
        
        private String talentId;
        private Long certificateId;
        private List<CertificateResponseDTO> certificateList;
        private CertificateResponseDTO certificate;
        
        @BeforeEach
        void setUp() {
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
        void getCertificatesByUserId_WhenNoCertificates_ShouldReturnNotFound() throws Exception {
            List<CertificateResponseDTO> emptyList = new ArrayList<>();
            when(certificateService.getByUserId(talentId)).thenReturn(emptyList);
            
            mockMvc.perform(get("/certificates/user/{userId}", talentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").value("No certificates found for user ID: " + talentId))
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
            when(certificateService.getById(certificateId)).thenReturn(null);
            
            mockMvc.perform(get("/certificates/{id}", certificateId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").value("Certificate not found with ID: " + certificateId))
                .andExpect(jsonPath("$.data").doesNotExist());
            
            verify(certificateService, times(1)).getById(certificateId);
        }
        
        @Test
        void getUserData_WhenUserIsNull_ShouldReturnUnauthorized() throws Exception {
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
}
