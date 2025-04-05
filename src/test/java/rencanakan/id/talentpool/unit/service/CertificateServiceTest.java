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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
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
    }

    @Nested
    class CreateCertificateTests {
        private User user;
        private String userId;
        private Certificate certificate;
        private CertificateResponseDTO certificateRequestDTO;
        private CertificateResponseDTO certificateResponseDTO;

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

            certificateRequestDTO = new CertificateResponseDTO();
            certificateRequestDTO.setTitle("Java Certificate");
            certificateRequestDTO.setFile("java-cert.pdf");

            certificate = Certificate.builder()
                    .id(1L)
                    .title("Java Certificate")
                    .file("java-cert.pdf")
                    .user(User.builder().id(userId).build())
                    .build();

            certificateResponseDTO = new CertificateResponseDTO();
            certificateResponseDTO.setId(1L);
            certificateResponseDTO.setTitle("Java Certificate");
            certificateResponseDTO.setFile("java-cert.pdf");
        }

        @Test
        void create_WithValidData_ShouldReturnSavedDTO() {
            try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
                // Create a certificate without user set to verify the method sets it correctly
                Certificate certificateWithoutUser = Certificate.builder()
                        .title("Java Certificate")
                        .file("java-cert.pdf")
                        .build();

                // Mock mapping from DTO to entity
                dtoMapperMock.when(() -> DTOMapper.map(eq(certificateRequestDTO), eq(Certificate.class)))
                        .thenReturn(certificateWithoutUser);

                // Use argument captor to verify the saved entity has the user set
                ArgumentCaptor<Certificate> certificateCaptor = ArgumentCaptor.forClass(Certificate.class);

                // Mock the repository save
                when(certificateRepository.save(certificateCaptor.capture())).thenReturn(certificate);

                // Mock mapping from entity back to DTO
                dtoMapperMock.when(() -> DTOMapper.map(eq(certificate), eq(CertificateResponseDTO.class)))
                        .thenReturn(certificateResponseDTO);

                // Execute the method
                CertificateResponseDTO result = certificateService.create(userId, certificateRequestDTO);

                // Verify the result
                assertNotNull(result);
                assertEquals(1L, result.getId());
                assertEquals("Java Certificate", result.getTitle());
                assertEquals("java-cert.pdf", result.getFile());

                // Verify the user was set correctly
                Certificate capturedCertificate = certificateCaptor.getValue();
                assertNotNull(capturedCertificate.getUser());
                assertEquals(userId, capturedCertificate.getUser().getId());

                // Verify interactions
                verify(certificateRepository).save(certificateCaptor.capture());
            }
        }

        @Test
        void create_WithNullUserId_ShouldThrowIllegalArgumentException() {
            // Execute and verify exception
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> certificateService.create(null, certificateRequestDTO)
            );

            assertEquals("Certification request cannot be null", exception.getMessage());

            // Verify no interactions with repository
            verifyNoInteractions(certificateRepository);
        }

        @Test
        void create_WithNullRequest_ShouldThrowIllegalArgumentException() {
            // Execute and verify exception
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> certificateService.create(userId, null)
            );

            assertEquals("Certification request cannot be null", exception.getMessage());

            // Verify no interactions with repository
            verifyNoInteractions(certificateRepository);
        }

        @Test
        void create_WhenMapperFails_ShouldPropagateException() {
            try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
                // Mock mapping exception
                dtoMapperMock.when(() -> DTOMapper.map(eq(certificateRequestDTO), eq(Certificate.class)))
                        .thenThrow(RuntimeException.class);

                // Execute and verify exception
                assertThrows(
                        RuntimeException.class,
                        () -> certificateService.create(userId, certificateRequestDTO)
                );

                // Verify no interactions with repository
                verifyNoInteractions(certificateRepository);
            }
        }

        @Test
        void create_WhenRepositorySaveFails_ShouldPropagateException() {
            try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
                // Mock mapping from DTO to entity
                dtoMapperMock.when(() -> DTOMapper.map(eq(certificateRequestDTO), eq(Certificate.class)))
                        .thenReturn(certificate);

                // Mock repository save to throw exception
                when(certificateRepository.save(any(Certificate.class)))
                        .thenThrow(new RuntimeException("Database error"));

                // Execute and verify exception
                RuntimeException exception = assertThrows(
                        RuntimeException.class,
                        () -> certificateService.create(userId, certificateRequestDTO)
                );

                assertEquals("Database error", exception.getMessage());

                // Verify interactions
                verify(certificateRepository).save(any(Certificate.class));
            }
        }

        @Test
        void create_WithEmptyTitle_ShouldProcessRequest() {
            // Create certificate with empty title
            CertificateResponseDTO emptyTitleRequest = new CertificateResponseDTO();
            emptyTitleRequest.setTitle("");
            emptyTitleRequest.setFile("java-cert.pdf");

            Certificate emptyTitleCertificate = Certificate.builder()
                    .id(1L)
                    .title("")
                    .file("java-cert.pdf")
                    .user(User.builder().id(userId).build())
                    .build();

            CertificateResponseDTO emptyTitleResponse = new CertificateResponseDTO();
            emptyTitleResponse.setId(1L);
            emptyTitleResponse.setTitle("");
            emptyTitleResponse.setFile("java-cert.pdf");

            try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
                // Mock mapping from DTO to entity
                dtoMapperMock.when(() -> DTOMapper.map(eq(emptyTitleRequest), eq(Certificate.class)))
                        .thenReturn(emptyTitleCertificate);

                // Mock the repository save
                when(certificateRepository.save(any(Certificate.class))).thenReturn(emptyTitleCertificate);

                // Mock mapping from entity back to DTO
                dtoMapperMock.when(() -> DTOMapper.map(eq(emptyTitleCertificate), eq(CertificateResponseDTO.class)))
                        .thenReturn(emptyTitleResponse);

                // Execute the method
                CertificateResponseDTO result = certificateService.create(userId, emptyTitleRequest);

                // Verify the result
                assertNotNull(result);
                assertEquals(1L, result.getId());
                assertEquals("", result.getTitle());

                // Verify interactions
                verify(certificateRepository).save(any(Certificate.class));
            }
        }
    }
}
