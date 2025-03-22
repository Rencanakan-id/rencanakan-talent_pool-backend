package rencanakan.id.talentpool.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import rencanakan.id.talentpool.service.JwtService;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long JWT_EXPIRATION = 3600000; // 1 hour

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        setPrivateField(jwtService, "secretKey", SECRET_KEY);
        setPrivateField(jwtService, "jwtExpiration", JWT_EXPIRATION);

        userDetails = User.withUsername("brighterdaysahead")
                .password("password")
                .build();
    }

    @Test
    public void testGenerateToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void testExtractUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals("brighterdaysahead", username);
    }

    @Test
    public void testIsTokenValid() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid, "Token should be valid for the correct user.");
    }

    @Test
    public void testIsTokenInvalidForDifferentUser() {
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUser = User.withUsername("eternalsunshine")
                .password("password")
                .build();

        boolean isValid = jwtService.isTokenValid(token, otherUser);
        assertFalse(isValid, "Token should be invalid for a different user.");
    }

    @Test
    public void testIsTokenInvalidForExpiredToken() throws InterruptedException {
        Map<String, Object> claims = new HashMap<>();
        long shortExpiration = 100;
        String token = jwtService.buildToken(claims, userDetails, shortExpiration);

        Thread.sleep(200);

        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertFalse(isValid, "Token should be invalid if it has expired.");
    }

    @Test
    public void testIsTokenInvalidForExpiredTokenAndDifferentUser() throws InterruptedException {
        Map<String, Object> claims = new HashMap<>();
        long shortExpiration = 100;
        String token = jwtService.buildToken(claims, userDetails, shortExpiration);

        Thread.sleep(200);

        UserDetails otherUser = User.withUsername("eternalsunshine")
                .password("password")
                .build();

        boolean isValid = jwtService.isTokenValid(token, otherUser);
        assertFalse(isValid, "Token should be invalid if it has expired or the user does not match.");
    }

    @Test
    public void testExtractClaim() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");
        String token = jwtService.generateToken(extraClaims, userDetails);

        String customClaimValue = jwtService.extractClaim(token, claims -> (String) claims.get("customClaim"));
        assertEquals("customValue", customClaimValue);
    }

    @Test
    public void testGetExpirationTime() throws Exception {
        JwtService jwtService = new JwtService();

        Field jwtExpirationField = JwtService.class.getDeclaredField("jwtExpiration");
        jwtExpirationField.setAccessible(true);
        jwtExpirationField.set(jwtService, 3600000L);

        long expirationTime = jwtService.getExpirationTime();
        assertEquals(3600000L, expirationTime);
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}