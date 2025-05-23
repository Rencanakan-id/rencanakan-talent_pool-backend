package rencanakan.id.talentpool.unit.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import rencanakan.id.talentpool.dto.LoginRequestDTO;
import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.model.PasswordResetToken;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.PasswordResetTokenRepository;
import rencanakan.id.talentpool.repository.UserRepository;
import rencanakan.id.talentpool.service.AuthenticationService;
import rencanakan.id.talentpool.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private Validator validator;

    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
         userRequestDTO = createValidUserRequestDTO();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UserRequestDTO createValidUserRequestDTO() {
        UserRequestDTO request = new UserRequestDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("1234567890");
        request.setPhoto("https://example.com/photo.jpg");
        request.setAboutMe("I am a software engineer with 5 years of experience.");
        request.setNik("1234567890123456");
        request.setNpwp("123456789012345");
        request.setPhotoKtp("https://example.com/photo_ktp.jpg");
        request.setPhotoNpwp("https://example.com/photo_npwp.jpg");
        request.setPhotoIjazah("https://example.com/photo_ijazah.jpg");
        request.setExperienceYears(5);
        request.setSkkLevel("Intermediate");
        request.setCurrentLocation("Jakarta");
        request.setPreferredLocations(List.of("Bandung, Surabaya"));
        request.setSkill("Java, Spring Boot, Microservices");
        request.setPrice(1000);
        return request;
    }

    @Test
    void testSignup_ValidUser_Success() throws BadRequestException {

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = authenticationService.signup(userRequestDTO);

        assertNotNull(savedUser);
        assertEquals("John", savedUser.getFirstName());
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void signup_DuplicateEmail_ThrowsException() {
        when(userRepository.findByEmail(userRequestDTO.getEmail()))
                .thenReturn(Optional.of(new User()));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authenticationService.signup(userRequestDTO);
        });

        assertEquals("Email " + userRequestDTO.getEmail() + " is already in use.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signup_DuplicateNik_ThrowsException() {
        when(userRepository.findByNik(userRequestDTO.getNik()))
                .thenReturn(Optional.of(new User()));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authenticationService.signup(userRequestDTO);
        });

        assertEquals("NIK " + userRequestDTO.getNik() + " is already in use.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signup_DuplicateNpwp_ThrowsException() {
        when(userRepository.findByNpwp(userRequestDTO.getNpwp()))
                .thenReturn(Optional.of(new User()));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authenticationService.signup(userRequestDTO);
        });

        assertEquals("NPWP " + userRequestDTO.getNpwp() + " is already in use.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signup_DuplicatePhoneNumber_ThrowsException() {
        when(userRepository.findByPhoneNumber(userRequestDTO.getPhoneNumber()))
                .thenReturn(Optional.of(new User()));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authenticationService.signup(userRequestDTO);
        });

        assertEquals("Phone number " + userRequestDTO.getPhoneNumber() + " is already in use.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signup_InvalidEmail_ValidationFails() {
        userRequestDTO.setEmail("invalid-email");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(userRequestDTO);

        assertFalse(violations.isEmpty(), "Validation should fail for invalid email");
        assertTrue(violations.iterator().next().getMessage().contains("must be a well-formed email address"),
                "Validation message should indicate an invalid email format");
    }

    @Test
    void testSignup_InvalidUser_ValidationFails() {
        userRequestDTO.setEmail("invalid-email");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(userRequestDTO);

        assertFalse(violations.isEmpty(), "Validation should fail for invalid email");
        assertTrue(violations.iterator().next().getMessage().contains("must be a well-formed email address"),
                "Validation message should indicate an invalid email format");
    }

    @Test
    void testAuthenticate_ValidCredentials_Success() {
        LoginRequestDTO input = new LoginRequestDTO();
        input.setEmail("john.doe@example.com");
        input.setPassword("password123");

        User mockUser = new User();
        mockUser.setEmail(input.getEmail());

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(mockUser));

        User authenticatedUser = authenticationService.authenticate(input);

        assertNotNull(authenticatedUser);
        assertEquals(input.getEmail(), authenticatedUser.getEmail());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(userRepository, times(1)).findByEmail(input.getEmail());
    }

    @Test
    void testAuthenticate_InvalidCredentials_ThrowsException() {
        LoginRequestDTO input = new LoginRequestDTO();
        input.setEmail("john.doe@example.com");
        input.setPassword("wrong-password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new UsernameNotFoundException("Invalid credentials"));

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authenticationService.authenticate(input)
        );
        assertEquals("Invalid credentials", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void resetPasswordWithToken_shouldUpdatePasswordAndMarkTokenUsed() {
        // Arrange
        String token = "abc";
        String newPassword = "Dummy123#";
        String encodedPassword = "Dummy123#";
        String email = "user@email.com";
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .email(email)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();
        User user = new User();
        user.setEmail(email);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        authenticationService.resetPasswordWithToken(token, newPassword);

        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).save(resetToken);
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
        assertThat(resetToken.isUsed()).isTrue();
    }

    @Test
    void resetPasswordWithToken_shouldThrowIfTokenInvalid() {
        when(passwordResetTokenRepository.findByToken("notfound")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authenticationService.resetPasswordWithToken("notfound", "pw"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Token tidak valid");
    }
}
