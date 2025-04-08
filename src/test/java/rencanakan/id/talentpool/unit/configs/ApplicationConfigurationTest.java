package rencanakan.id.talentpool.unit.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import rencanakan.id.talentpool.configs.ApplicationConfiguration;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigurationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationConfiguration applicationConfiguration;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstName("Burhan")
                .lastName("Wijaya")
                .email("burhan.wijaya@example.com")
                .password(applicationConfiguration.passwordEncoder().encode("burhan123"))
                .phoneNumber("1234567890")
                .nik("1234567890123456")
                .currentLocation("Jakarta")
                .skill("Terbang di malam hari")
                .price(20)
                .build();
    }

    @Test
    void testUserDetailsService_UserFound() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        UserDetailsService userDetailsService = applicationConfiguration.userDetailsService();

        UserDetails userDetails = userDetailsService.loadUserByUsername(testUser.getEmail());

        assertNotNull(userDetails);
        assertEquals(testUser.getEmail(), userDetails.getUsername());
    }

    @Test
    void testUserDetailsService_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = applicationConfiguration.userDetailsService();

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder passwordEncoder = applicationConfiguration.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
    }

    @Test
    void testAuthenticationManager() throws Exception {
        AuthenticationConfiguration mockAuthenticationConfiguration = mock(AuthenticationConfiguration.class);
        AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);

        when(mockAuthenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthenticationManager);

        ApplicationConfiguration config = new ApplicationConfiguration(userRepository);

        AuthenticationManager authenticationManager = config.authenticationManager(mockAuthenticationConfiguration);

        assertNotNull(authenticationManager);
        assertEquals(mockAuthenticationManager, authenticationManager);
    }

    @Test
    void testAuthenticationProvider() {
        ApplicationConfiguration config = new ApplicationConfiguration(userRepository);

        DaoAuthenticationProvider authProvider = (DaoAuthenticationProvider) config.authenticationProvider();

        assertNotNull(authProvider);
    }
}