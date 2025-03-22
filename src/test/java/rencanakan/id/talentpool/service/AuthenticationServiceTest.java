package rencanakan.id.talentpool.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import rencanakan.id.talentpool.dto.LoginUserDTO;
import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @InjectMocks
    private AuthenticationService authenticationService;
    
    @Captor
    private ArgumentCaptor<User> userCaptor;
    
    private User testUser;
    private UserRequestDTO testUserRequest;
    private LoginUserDTO testLoginRequest;
    private List<String> preferredLocations;
    
    @BeforeEach
    void setUp() {
        // Initialize preferred locations
        preferredLocations = createPreferredLocations();
        
        // Setup test objects
        testUser = createUser();
        testUserRequest = createUserRequestDTO();
        testLoginRequest = createLoginUserDTO();
    }
    
    private List<String> createPreferredLocations() {
        List<String> locations = new ArrayList<>();
        locations.add("Jakarta");
        locations.add("Bandung");
        locations.add("Surabaya");
        return locations;
    }
    
    private User createUser() {
        User user = new User();
        user.setId("user123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("encodedPassword123");
        user.setNik("1234567890123456");
        user.setPhoneNumber("0812345678");
        user.setPreferredLocations(preferredLocations);
        user.setAboutMe("Experienced software developer");
        user.setPhoto("profile.jpg");
        user.setNpwp("123456789012345");
        user.setPhotoKtp("ktp.jpg");
        user.setPhotoNpwp("npwp.jpg");
        user.setPhotoIjazah("ijazah.jpg");
        user.setExperienceYears(5);
        user.setSkkLevel("Professional");
        user.setCurrentLocation("Jakarta");
        user.setSkill("Java, Spring Boot, React");
        user.setPrice(10000000);
        return user;
    }
    
    private UserRequestDTO createUserRequestDTO() {
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setFirstName("John");
        requestDTO.setLastName("Doe");
        requestDTO.setEmail("john.doe@example.com");
        requestDTO.setPassword("password123");
        requestDTO.setNik("1234567890123456");
        requestDTO.setPhoneNumber("0812345678");
        requestDTO.setPreferredLocations(preferredLocations);
        requestDTO.setAboutMe("Experienced software developer");
        requestDTO.setPhoto("profile.jpg");
        requestDTO.setNpwp("123456789012345");
        requestDTO.setPhotoKtp("ktp.jpg");
        requestDTO.setPhotoNpwp("npwp.jpg");
        requestDTO.setPhotoIjazah("ijazah.jpg");
        requestDTO.setExperienceYears(5);
        requestDTO.setSkkLevel("Professional");
        requestDTO.setCurrentLocation("Jakarta");
        requestDTO.setSkill("Java, Spring Boot, React");
        requestDTO.setPrice(10000000);
        return requestDTO;
    }
    
    private LoginUserDTO createLoginUserDTO() {
        LoginUserDTO loginDTO = new LoginUserDTO();
        loginDTO.setEmail("john.doe@example.com");
        loginDTO.setPassword("password123");
        return loginDTO;
    }
    
    @Nested
    class SignupTests {
        
        @Test
        void signup_WithValidData_CreatesUser() {
            // Arrange
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            
            // Act
            User result = authenticationService.signup(testUserRequest);
            
            // Assert
            assertNotNull(result);
            assertEquals("user123", result.getId());
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            assertEquals("john.doe@example.com", result.getEmail());
            assertEquals("encodedPassword123", result.getPassword());
            
            verify(userRepository, times(1)).save(any(User.class));
            verify(passwordEncoder, times(1)).encode("password123");
        }
        
        @Test
        void signup_ShouldEncodePassword() {
            // Arrange
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            
            // Act
            authenticationService.signup(testUserRequest);
            
            // Assert
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertEquals("encodedPassword123", savedUser.getPassword());
        }
        
        @Test
        void signup_ShouldSaveAllUserFields() {
            // Arrange
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            
            // Act
            authenticationService.signup(testUserRequest);
            
            // Assert
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            
            assertEquals("John", savedUser.getFirstName());
            assertEquals("Doe", savedUser.getLastName());
            assertEquals("john.doe@example.com", savedUser.getEmail());
            assertEquals("encodedPassword123", savedUser.getPassword());
            assertEquals("0812345678", savedUser.getPhoneNumber());
            assertEquals("Experienced software developer", savedUser.getAboutMe());
            assertEquals("profile.jpg", savedUser.getPhoto());
            assertEquals("1234567890123456", savedUser.getNik());
            assertEquals("123456789012345", savedUser.getNpwp());
            assertEquals("ktp.jpg", savedUser.getPhotoKtp());
            assertEquals("npwp.jpg", savedUser.getPhotoNpwp());
            assertEquals("ijazah.jpg", savedUser.getPhotoIjazah());
            assertEquals(5, savedUser.getExperienceYears());
            assertEquals("Professional", savedUser.getSkkLevel());
            assertEquals("Jakarta", savedUser.getCurrentLocation());
            assertEquals(preferredLocations, savedUser.getPreferredLocations());
            assertEquals("Java, Spring Boot, React", savedUser.getSkill());
            assertEquals(10000000, savedUser.getPrice());
        }
    }
    
    @Nested
    class AuthenticateTests {
        
        @Test
        void authenticate_WithValidCredentials_ReturnsUser() {
            // Arrange
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    testLoginRequest.getEmail(), testLoginRequest.getPassword());
            
            when(userRepository.findByEmail(testLoginRequest.getEmail())).thenReturn(Optional.of(testUser));
            
            // Act
            User result = authenticationService.authenticate(testLoginRequest);
            
            // Assert
            assertNotNull(result);
            assertEquals("user123", result.getId());
            assertEquals("john.doe@example.com", result.getEmail());
            
            verify(authenticationManager, times(1)).authenticate(refEq(authToken));
            verify(userRepository, times(1)).findByEmail(testLoginRequest.getEmail());
        }
        
        @Test
        void authenticate_WithInvalidCredentials_ThrowsException() {
            // Arrange
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    testLoginRequest.getEmail(), testLoginRequest.getPassword());
            
            doThrow(new RuntimeException("Bad credentials")).when(authenticationManager).authenticate(refEq(authToken));
            
            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () -> {
                authenticationService.authenticate(testLoginRequest);
            });
            
            assertEquals("Bad credentials", exception.getMessage());
            verify(userRepository, never()).findByEmail(anyString());
        }
        
        @Test
        void authenticate_WithNonExistentUser_ThrowsException() {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            
            // Act & Assert
            assertThrows(NoSuchElementException.class, () -> {
                authenticationService.authenticate(testLoginRequest);
            });
            
            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }
    }
}
