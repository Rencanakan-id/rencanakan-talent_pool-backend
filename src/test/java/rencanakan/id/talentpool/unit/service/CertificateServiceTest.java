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
    
    @Nested
    class ReadCertificateTests {

        
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

    @Nested
    class CreateCertificateTests {
        private User user;
        private String userId;
        private CertificateRequestDTO certificateRequestDTO;
        private Certificate certificate;
        private Certificate savedCertificate;
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

            certificateRequestDTO = new CertificateRequestDTO();
            certificateRequestDTO.setTitle("AWS Certified Developer");
            certificateRequestDTO.setFile("aws-cert.pdf");

            certificate = Certificate.builder()
                    .title("AWS Certified Developer")
                    .file("aws-cert.pdf")
                    .build();

            savedCertificate = Certificate.builder()
                    .id(1L)
                    .title("AWS Certified Developer")
                    .file("aws-cert.pdf")
                    .user(User.builder().id(userId).build())
                    .build();

            certificateResponseDTO = CertificateResponseDTO.builder()
                    .id(1L)
                    .title("AWS Certified Developer")
                    .file("aws-cert.pdf")
                    .talentId(userId)
                    .build();
        }

        // Positive test case - successful creation
        @Test
        void create_WithValidData_ShouldReturnDTO() {
            try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
                // Setup mocks
                dtoMapperMock.when(() -> DTOMapper.map(certificateRequestDTO, Certificate.class))
                        .thenReturn(certificate);
                dtoMapperMock.when(() -> DTOMapper.map(savedCertificate, CertificateResponseDTO.class))
                        .thenReturn(certificateResponseDTO);

                when(certificateRepository.save(any(Certificate.class))).thenReturn(savedCertificate);

                // Execute
                CertificateResponseDTO result = certificateService.create(userId, certificateRequestDTO);

                // Verify
                assertNotNull(result);
                assertEquals(1L, result.getId());
                assertEquals("AWS Certified Developer", result.getTitle());
                assertEquals("aws-cert.pdf", result.getFile());
                assertEquals(userId, result.getTalentId());

                // Verify user is set correctly
                ArgumentCaptor<Certificate> certificateCaptor = ArgumentCaptor.forClass(Certificate.class);
                verify(certificateRepository).save(certificateCaptor.capture());

                Certificate capturedCertificate = certificateCaptor.getValue();
                assertNotNull(capturedCertificate.getUser());
                assertEquals(userId, capturedCertificate.getUser().getId());
            }
        }

        // Negative test case - null userId
        @Test
        void create_WithNullUserId_ShouldThrowIllegalArgumentException() {
            // Execute and verify
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> certificateService.create(null, certificateRequestDTO));

            assertEquals("Certification request cannot be null", exception.getMessage());

            // Verify repository was not called
            verify(certificateRepository, never()).save(any());
        }

        // Negative test case - null certificateRequest
        @Test
        void create_WithNullCertificateRequest_ShouldThrowIllegalArgumentException() {
            // Execute and verify
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> certificateService.create(userId, null));

            assertEquals("Certification request cannot be null", exception.getMessage());

            // Verify repository was not called
            verify(certificateRepository, never()).save(any());
        }

        // Negative test case - both null
        @Test
        void create_WithBothParamsNull_ShouldThrowIllegalArgumentException() {
            // Execute and verify
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> certificateService.create(null, null));

            assertEquals("Certification request cannot be null", exception.getMessage());

            // Verify repository was not called
            verify(certificateRepository, never()).save(any());
        }

        // Edge case - mapper throws exception
        @Test
        void create_WhenMapperThrowsException_ShouldPropagateException() {
            try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
                // Setup mock to throw exception
                dtoMapperMock.when(() -> DTOMapper.map(certificateRequestDTO, Certificate.class))
                        .thenThrow(new RuntimeException("Mapping error"));

                // Execute and verify
                RuntimeException exception = assertThrows(RuntimeException.class,
                        () -> certificateService.create(userId, certificateRequestDTO));

                assertEquals("Mapping error", exception.getMessage());

                // Verify repository was not called
                verify(certificateRepository, never()).save(any());
            }
        }

        // Edge case - repository throws exception
        @Test
        void create_WhenRepositoryThrowsException_ShouldPropagateException() {
            try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
                // Setup mocks
                dtoMapperMock.when(() -> DTOMapper.map(certificateRequestDTO, Certificate.class))
                        .thenReturn(certificate);

                when(certificateRepository.save(any(Certificate.class)))
                        .thenThrow(new RuntimeException("Database error"));

                // Execute and verify
                RuntimeException exception = assertThrows(RuntimeException.class,
                        () -> certificateService.create(userId, certificateRequestDTO));

                assertEquals("Database error", exception.getMessage());
            }
        }

        // Edge case - Response mapper throws exception
        @Test
        void create_WhenResponseMapperThrowsException_ShouldPropagateException() {
            try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
                // Setup mocks
                dtoMapperMock.when(() -> DTOMapper.map(certificateRequestDTO, Certificate.class))
                        .thenReturn(certificate);
                dtoMapperMock.when(() -> DTOMapper.map(savedCertificate, CertificateResponseDTO.class))
                        .thenThrow(new RuntimeException("Response mapping error"));

                when(certificateRepository.save(any(Certificate.class))).thenReturn(savedCertificate);

                // Execute and verify
                RuntimeException exception = assertThrows(RuntimeException.class,
                        () -> certificateService.create(userId, certificateRequestDTO));

                assertEquals("Response mapping error", exception.getMessage());

                // Verify repository was called
                verify(certificateRepository).save(any());
            }
        }

        // Edge case - empty but not null certificate request fields
        @Test
        void create_WithEmptyFields_ShouldStillCreate() {
            // Setup
            CertificateRequestDTO emptyFieldsRequest = new CertificateRequestDTO();
            emptyFieldsRequest.setTitle("");
            emptyFieldsRequest.setFile("");

            Certificate emptyFieldsCertificate = Certificate.builder()
                    .title("")
                    .file("")
                    .build();

            Certificate savedEmptyCertificate = Certificate.builder()
                    .id(1L)
                    .title("")
                    .file("")
                    .user(User.builder().id(userId).build())
                    .build();

            CertificateResponseDTO emptyResponseDTO = CertificateResponseDTO.builder()
                    .id(1L)
                    .title("")
                    .file("")
                    .talentId(userId)
                    .build();

            try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
                // Setup mocks
                dtoMapperMock.when(() -> DTOMapper.map(emptyFieldsRequest, Certificate.class))
                        .thenReturn(emptyFieldsCertificate);
                dtoMapperMock.when(() -> DTOMapper.map(savedEmptyCertificate, CertificateResponseDTO.class))
                        .thenReturn(emptyResponseDTO);

                when(certificateRepository.save(any(Certificate.class))).thenReturn(savedEmptyCertificate);

                // Execute
                CertificateResponseDTO result = certificateService.create(userId, emptyFieldsRequest);

                // Verify
                assertNotNull(result);
                assertEquals(1L, result.getId());
                assertEquals("", result.getTitle());
                assertEquals("", result.getFile());
                assertEquals(userId, result.getTalentId());
            }
        }
    }

    @Test
    void testDeleteById_Success() {
        // Arrange
        Long certificateId = 1L;
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));

        // Act
        certificateService.deleteById(certificateId, userId);

        // Assert
        verify(certificateRepository, times(1)).delete(certificate);
    }

    @Test
    void testDeleteById_CertificateNotFound() {
        // Arrange
        Long certificateId = 999L; // ID yang tidak ada di database
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            certificateService.deleteById(certificateId, userId);
        });

        assertEquals("Sertifikat tidak ditemukan", exception.getMessage());
        verify(certificateRepository, never()).delete(any());
    }

    @Test
    void testDeleteById_UnauthorizedAccess() {
        // Arrange
        Long certificateId = 1L;
        String unauthorizedUserId = "unauthorized-user-id"; // Pengguna lain

        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            certificateService.deleteById(certificateId, unauthorizedUserId);
        });

        assertEquals("Anda tidak memiliki akses", exception.getMessage());
        verify(certificateRepository, never()).delete(any());
    }
}
