package rencanakan.id.talentpool.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import rencanakan.id.talentpool.dto.CertificateRequestDTO;
import rencanakan.id.talentpool.dto.CertificateResponseDTO;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Certificate;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.CertificateRepository;
import rencanakan.id.talentpool.service.CertificateServiceImpl;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @InjectMocks
    private CertificateServiceImpl certificateService;

    
    @Nested
    class ReadCertificateTests {
        private User user;
        private String userId;
        private Certificate certificate;
        private CertificateResponseDTO certificateResponseDTO;
        private CertificateRequestDTO certificateRequestDTO;
    
        @BeforeEach
        void setUp() {
            user = User.builder()
                    .id("talent-123")
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .password("password123")
                    .phoneNumber("1234567890")
                    .photo("profile.jpg")
                    .aboutMe("About me text")
                    .nik("1234567890123456")
                    .npwp("12.345.678.9-012.345")
                    .photoKtp("ktp.jpg")
                    .photoNpwp("npwp.jpg")
                    .photoIjazah("ijazah.jpg")
                    .experienceYears(5)
                    .skkLevel("Intermediate")
                    .currentLocation("Jakarta")
                    .preferredLocations(Arrays.asList("Jakarta", "Bandung"))
                    .skill("Java, Spring Boot")
                    .build();

            userId = user.getId();
    
            certificate = Certificate.builder()
                    .id(1L)
                    .title("Java Certificate")
                    .file("java-cert.pdf")
                    .user(user)
                    .build();
    
            certificateResponseDTO = new CertificateResponseDTO();
            certificateResponseDTO.setId(1L);
            certificateResponseDTO.setTitle("Java Certificate");
            certificateResponseDTO.setFile("java-cert.pdf");

            certificateRequestDTO = new CertificateRequestDTO();
            certificateRequestDTO.setTitle("Python Certificate");
            certificateRequestDTO.setFile("python-cert.pdf");
        }
        
        @Test
        void getByTalentId_WithExistingCertificates_ShouldReturnDTOList() {
            List<Certificate> certificates = List.of(certificate);
            when(certificateRepository.findByUserId(user.getId())).thenReturn(certificates);
            
            try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
                dtoMapperMock.when(() -> DTOMapper.map(any(Certificate.class), eq(CertificateResponseDTO.class)))
                    .thenReturn(certificateResponseDTO);
                
                List<CertificateResponseDTO> result = certificateService.getByUserId(user.getId());
                
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals("Java Certificate", result.get(0).getTitle());
            }
            
            verify(certificateRepository).findByUserId(user.getId());
        }
        
        @Test
        void getByTalentId_WithNoCertificates_ShouldThrowEntityNotFoundException() {
            when(certificateRepository.findByUserId(user.getId())).thenReturn(Collections.emptyList());
            
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
                    () -> certificateService.getByUserId(userId));
            
            assertEquals("Certificates not found for user with id talent-123", exception.getMessage());
            verify(certificateRepository).findByUserId(user.getId());
        }
        
        @Test
        void getByTalentId_WithNullId_ShouldHandleNullPointerException() {
            when(certificateRepository.findByUserId(null)).thenThrow(NullPointerException.class);
            
            assertThrows(NullPointerException.class, () -> certificateService.getByUserId(null));
            verify(certificateRepository).findByUserId(null);
        }

        @Test
        void getById_WithExistingId_ShouldReturnDTO() {
            when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
            
            try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
                dtoMapperMock.when(() -> DTOMapper.map(certificate, CertificateResponseDTO.class))
                    .thenReturn(certificateResponseDTO);
                
                CertificateResponseDTO result = certificateService.getById(1L);
                
                assertNotNull(result);
                assertEquals(1L, result.getId());
                assertEquals("Java Certificate", result.getTitle());
            }
            
            verify(certificateRepository).findById(1L);
        }
        
        @Test
        void getById_WithNonExistingId_ShouldThrowEntityNotFoundException() {
            when(certificateRepository.findById(999L)).thenReturn(Optional.empty());
            
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
                    () -> certificateService.getById(999L));
            
            assertEquals("Certificate with id 999 not found", exception.getMessage());
            verify(certificateRepository).findById(999L);
        }
        
        @Test
        void getById_WithNullId_ShouldHandleNullPointerException() {
            when(certificateRepository.findById(null)).thenThrow(IllegalArgumentException.class);
            
            assertThrows(IllegalArgumentException.class, () -> certificateService.getById(null));
            verify(certificateRepository).findById(null);
        }
        
        @Test
        void getById_WhenMapperFails_ShouldPropagateException() {
            when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
            
            try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
                dtoMapperMock.when(() -> DTOMapper.map(certificate, CertificateResponseDTO.class))
                    .thenThrow(RuntimeException.class);
                
                assertThrows(RuntimeException.class, () -> certificateService.getById(1L));
            }
            
            verify(certificateRepository).findById(1L);
        }

        @Test
        void testEditById_Success() {
            Long certificateId = 1L;

            Certificate newCertificate = Certificate.builder()
                    .id(1L)
                    .title("Python Certificate")
                    .file("python-cert.pdf")
                    .user(user)
                    .build();

            when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
            when(certificateRepository.save(any(Certificate.class))).thenReturn(newCertificate);

            CertificateResponseDTO response = certificateService.editById(certificateId, certificateRequestDTO);

            assertNotNull(response);
            assertEquals("Python Certificate", response.getTitle());
            assertEquals("python-cert.pdf", response.getFile());

            verify(certificateRepository, times(1)).findById(certificateId);
            verify(certificateRepository, times(1)).save(any(Certificate.class));
        }

        @Test
        void testEditById_EntityNotFound() {

            Long nonExistentId = 999L;
            when(certificateRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                certificateService.editById(nonExistentId, certificateRequestDTO);
            });

            assertEquals("Certificate with ID 999 not found", exception.getMessage());

            verify(certificateRepository, times(1)).findById(nonExistentId);
            verify(certificateRepository, never()).save(any(Certificate.class));
        }
    }
}
