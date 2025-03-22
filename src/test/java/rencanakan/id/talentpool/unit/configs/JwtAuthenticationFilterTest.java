package rencanakan.id.talentpool.unit.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import rencanakan.id.talentpool.configs.JwtAuthenticationFilter;
import rencanakan.id.talentpool.service.JwtService;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ValidToken_AuthenticationSet() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String jwtToken = "valid-jwt-token";
        String username = "testuser";
        request.addHeader("Authorization", "Bearer " + jwtToken);
        UserDetails userDetails = new User(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwtToken, userDetails)).thenReturn(true);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(username, authentication.getName());
        assertTrue(authentication.isAuthenticated());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_AuthenticationAlreadySet_NoChanges() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String jwtToken = "valid-jwt-token";
        String username = "testuser";
        request.addHeader("Authorization", "Bearer " + jwtToken);
        UserDetails userDetails = new User(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(existingAuth, authentication);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).isTokenValid(anyString(), any(UserDetails.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NullUsername_NoAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String jwtToken = "invalid-jwt-token";
        request.addHeader("Authorization", "Bearer " + jwtToken);
        when(jwtService.extractUsername(jwtToken)).thenReturn(null);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader_NoAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidAuthorizationHeader_NoAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String invalidHeader = "InvalidPrefix valid-jwt-token";
        request.addHeader("Authorization", invalidHeader);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken_NoAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String jwtToken = "invalid-jwt-token";
        String username = "testuser";
        request.addHeader("Authorization", "Bearer " + jwtToken);
        UserDetails userDetails = new User(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwtToken, userDetails)).thenReturn(false);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @ParameterizedTest
    @MethodSource("provideNullParameters")
    void testDoFilterInternal_NullParameters_ThrowsNullPointerException(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            String parameterName) throws ServletException, IOException {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        });
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Cannot invoke") || exception.getMessage().contains("null"),
                "Expected NullPointerException for " + parameterName);
    }

    @Test
    void testDoFilterInternal_NonNullAnnotationValidation() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Exception exception = assertThrows(NullPointerException.class, () -> {
            jwtAuthenticationFilter.doFilterInternal(null, response, filterChain);
        });
    }

    @Test
    void testDoFilterInternal_ValidUsernameButAuthenticationAlreadySet() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String jwtToken = "valid-jwt-token";
        String username = "testuser";
        request.addHeader("Authorization", "Bearer " + jwtToken);
        UserDetails existingUser = new User("existinguser", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken(
                existingUser, null, existingUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);
        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(existingAuth, authentication);
        assertEquals("existinguser", authentication.getName());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    private static Stream<Arguments> provideNullParameters() {
        MockHttpServletRequest validRequest = new MockHttpServletRequest();
        MockHttpServletResponse validResponse = new MockHttpServletResponse();
        FilterChain validFilterChain = mock(FilterChain.class);
        return Stream.of(
                Arguments.of(null, validResponse, validFilterChain, "HttpServletRequest"),
                Arguments.of(validRequest, null, validFilterChain, "HttpServletResponse"),
                Arguments.of(validRequest, validResponse, null, "FilterChain")
        );
    }
}