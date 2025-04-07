package rencanakan.id.talentpool.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.JwtService;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private User user;

    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long JWT_EXPIRATION = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        setPrivateField(jwtService, "secretKey", SECRET_KEY);
        setPrivateField(jwtService, "jwtExpiration", JWT_EXPIRATION);

        user = User.builder()
                .id("1")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);
        assertEquals("brighterdaysahead", username);
    }

    @Test
    void testIsTokenValid() {
        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, user);
        assertTrue(isValid, "Token should be valid for the correct user.");
    }

    @Test
    void testIsTokenInvalidForDifferentUser() {
        String token = jwtService.generateToken(user);

        User otherUser = User.builder()
                .id("1")
                .firstName("ariana")
                .lastName("Doe")
                .email("grande.doe@example.com")
                .build();

        boolean isValid = jwtService.isTokenValid(token, otherUser);
        assertFalse(isValid, "Token should be invalid for a different user.");
    }

    @Test
    void testIsTokenInvalidForExpiredToken() {
        Map<String, Object> claims = new HashMap<>();
        long shortExpiration = -1000;

        String token = jwtService.buildToken(claims, user, shortExpiration);
        boolean isValid = jwtService.isTokenValid(token, user);

        assertFalse(isValid, "Token should be invalid if it has expired.");
    }

    @Test
    void testIsTokenInvalidForExpiredTokenAndDifferentUser() {
        Map<String, Object> claims = new HashMap<>();
        long shortExpiration = -1000;

        User otherUser = User.builder()
                .id("1")
                .firstName("ariana")
                .lastName("Doe")
                .email("grande.doe@example.com")
                .build();

        String token = jwtService.buildToken(claims, user, shortExpiration);
        boolean isValid = jwtService.isTokenValid(token, otherUser);

        assertFalse(isValid, "Token should be invalid if it has expired or the user does not match.");
    }

    @Test
    void testExtractClaim() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");
        String token = jwtService.generateToken(extraClaims, user);

        String customClaimValue = jwtService.extractClaim(token, claims -> (String) claims.get("customClaim"));
        assertEquals("customValue", customClaimValue);
    }

    @Test
    void testGetExpirationTime() throws Exception {
        Field jwtExpirationField = JwtService.class.getDeclaredField("jwtExpiration");
        jwtExpirationField.setAccessible(true);
        jwtExpirationField.set(jwtService, 3600000L);

        long expirationTime = jwtService.getExpirationTime();
        assertEquals(3600000L, expirationTime);
    }

    void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}