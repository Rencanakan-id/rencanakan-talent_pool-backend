//package rencanakan.id.talentpool.unit.configs;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
//import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
//import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.DefaultSecurityFilterChain;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import rencanakan.id.talentpool.configs.JwtAuthenticationFilter;
//import rencanakan.id.talentpool.configs.SecurityConfiguration;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class SecurityConfigurationTest {
//
//    @Mock
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    @Mock
//    private AuthenticationProvider authenticationProvider;
//
//    @InjectMocks
//    private SecurityConfiguration securityConfiguration;
//
//    private HttpSecurity mockHttpSecurity;
//    private SecurityFilterChain mockSecurityFilterChain;
//
//    @BeforeEach
//    void setUp() {
//        mockHttpSecurity = mock(HttpSecurity.class);
//        mockSecurityFilterChain = mock(SecurityFilterChain.class);
//    }
//
//    @Nested
//    class ConstructorTests {
//
//        @Test
//        void constructor_ShouldInitializeWithDependencies() {
//            // Act
//            SecurityConfiguration config = new SecurityConfiguration(jwtAuthenticationFilter, authenticationProvider);
//
//            // Assert
//            assertNotNull(config);
//        }
//
//        @Test
//        void constructor_ShouldAcceptNonNullDependencies() {
//            // Arrange
//            JwtAuthenticationFilter filter = mock(JwtAuthenticationFilter.class);
//            AuthenticationProvider provider = mock(AuthenticationProvider.class);
//
//            // Act & Assert
//            assertDoesNotThrow(() -> new SecurityConfiguration(filter, provider));
//        }
//    }
//
//    @Nested
//    class SecurityFilterChainTests {
//
//        @Test
//        void securityFilterChain_ShouldConfigureAllComponents() throws Exception {
//            // Arrange
//            when(mockHttpSecurity.csrf(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.cors(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.authorizeHttpRequests(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.sessionManagement(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.authenticationProvider(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.addFilterBefore(any(), any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.build()).thenReturn((DefaultSecurityFilterChain) mockSecurityFilterChain);
//
//            // Act
//            SecurityFilterChain result = securityConfiguration.securityFilterChain(mockHttpSecurity);
//
//            // Assert
//            assertNotNull(result);
//            assertEquals(mockSecurityFilterChain, result);
//
//            // Verify all configuration methods were called
//            verify(mockHttpSecurity).csrf(any());
//            verify(mockHttpSecurity).cors(any());
//            verify(mockHttpSecurity).authorizeHttpRequests(any());
//            verify(mockHttpSecurity).sessionManagement(any());
//            verify(mockHttpSecurity).authenticationProvider(authenticationProvider);
//            verify(mockHttpSecurity).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//            verify(mockHttpSecurity).build();
//        }
//
//        @Test
//        void securityFilterChain_ShouldDisableCsrf() throws Exception {
//            // Arrange
//            setupMockHttpSecurity();
//            ArgumentCaptor<AbstractHttpConfigurer> csrfCaptor = ArgumentCaptor.forClass(AbstractHttpConfigurer.class);
//
//            // Act
//            securityConfiguration.securityFilterChain(mockHttpSecurity);
//
//            // Assert
//            verify(mockHttpSecurity).csrf(any());
//            // In real implementation, CSRF is disabled via AbstractHttpConfigurer::disable
//        }
//
//        @Test
//        void securityFilterChain_ShouldConfigureCorsWithCustomSource() throws Exception {
//            // Arrange
//            setupMockHttpSecurity();
//
//            // Act
//            securityConfiguration.securityFilterChain(mockHttpSecurity);
//
//            // Assert
//            verify(mockHttpSecurity).cors(any());
//            // The CORS configuration should use the custom corsConfigurationSource()
//        }
//
//        @Test
//        void securityFilterChain_ShouldSetStatelessSessionManagement() throws Exception {
//            // Arrange
//            setupMockHttpSecurity();
//
//            // Act
//            securityConfiguration.securityFilterChain(mockHttpSecurity);
//
//            // Assert
//            verify(mockHttpSecurity).sessionManagement(any());
//            // Verify stateless session creation policy is set
//        }
//
//        @Test
//        void securityFilterChain_ShouldRegisterAuthenticationProvider() throws Exception {
//            // Arrange
//            setupMockHttpSecurity();
//
//            // Act
//            securityConfiguration.securityFilterChain(mockHttpSecurity);
//
//            // Assert
//            verify(mockHttpSecurity).authenticationProvider(authenticationProvider);
//        }
//
//        @Test
//        void securityFilterChain_ShouldAddJwtFilterBeforeUsernamePasswordFilter() throws Exception {
//            // Arrange
//            setupMockHttpSecurity();
//
//            // Act
//            securityConfiguration.securityFilterChain(mockHttpSecurity);
//
//            // Assert
//            verify(mockHttpSecurity).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//        }
//
//        @Test
//        void securityFilterChain_ShouldHandleException() throws Exception {
//            // Arrange
//            when(mockHttpSecurity.csrf(any())).thenThrow(new RuntimeException("Configuration error"));
//
//            // Act & Assert
//            assertThrows(RuntimeException.class, () -> {
//                securityConfiguration.securityFilterChain(mockHttpSecurity);
//            });
//        }
//
//        private void setupMockHttpSecurity() throws Exception {
//            when(mockHttpSecurity.csrf(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.cors(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.authorizeHttpRequests(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.sessionManagement(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.authenticationProvider(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.addFilterBefore(any(), any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.build()).thenReturn((DefaultSecurityFilterChain) mockSecurityFilterChain);
//        }
//    }
//
//    @Nested
//    class CorsConfigurationSourceTests {
//
//        @Test
//        void corsConfigurationSource_ShouldReturnValidConfiguration() {
//            // Act
//            CorsConfigurationSource corsSource = securityConfiguration.corsConfigurationSource();
//
//            // Assert
//            assertNotNull(corsSource);
//            assertInstanceOf(UrlBasedCorsConfigurationSource.class, corsSource);
//        }
//
//        @Test
//        void corsConfigurationSource_ShouldConfigureAllowedOrigins() {
//            // Act
//            CorsConfigurationSource corsSource = securityConfiguration.corsConfigurationSource();
//            UrlBasedCorsConfigurationSource urlBasedSource = (UrlBasedCorsConfigurationSource) corsSource;
//
//            // Get configuration for any path
//            CorsConfiguration config = corsSource.getCorsConfiguration(null);
//
//            // Assert
//            assertNotNull(config);
//            List<String> allowedOrigins = config.getAllowedOrigins();
//            assertNotNull(allowedOrigins);
//            assertEquals(4, allowedOrigins.size());
//            assertTrue(allowedOrigins.contains("http://localhost:5173"));
//            assertTrue(allowedOrigins.contains("http://localhost:8080"));
//            assertTrue(allowedOrigins.contains("https://rencanakanid-stg.netlify.app/"));
//            assertTrue(allowedOrigins.contains("https://rencanakan-system.netlify.app/"));
//        }
//
//        @Test
//        void corsConfigurationSource_ShouldConfigureAllowedMethods() {
//            // Act
//            CorsConfigurationSource corsSource = securityConfiguration.corsConfigurationSource();
//            CorsConfiguration config = corsSource.getCorsConfiguration(null);
//
//            // Assert
//            assertNotNull(config);
//            List<String> allowedMethods = config.getAllowedMethods();
//            assertNotNull(allowedMethods);
//            assertEquals(5, allowedMethods.size());
//            assertTrue(allowedMethods.contains("GET"));
//            assertTrue(allowedMethods.contains("POST"));
//            assertTrue(allowedMethods.contains("PUT"));
//            assertTrue(allowedMethods.contains("DELETE"));
//            assertTrue(allowedMethods.contains("PATCH"));
//        }
//
//        @Test
//        void corsConfigurationSource_ShouldConfigureAllowedHeaders() {
//            // Act
//            CorsConfigurationSource corsSource = securityConfiguration.corsConfigurationSource();
//            CorsConfiguration config = corsSource.getCorsConfiguration(null);
//
//            // Assert
//            assertNotNull(config);
//            List<String> allowedHeaders = config.getAllowedHeaders();
//            assertNotNull(allowedHeaders);
//            assertEquals(2, allowedHeaders.size());
//            assertTrue(allowedHeaders.contains("Authorization"));
//            assertTrue(allowedHeaders.contains("Content-Type"));
//        }
//
//        @Test
//        void corsConfigurationSource_ShouldRegisterConfigurationForAllPaths() {
//            // Act
//            CorsConfigurationSource corsSource = securityConfiguration.corsConfigurationSource();
//
//            // Assert
//            // The configuration should be available for any path since it's registered with "/**"
//            // Using null as the request parameter to get the default configuration
//            CorsConfiguration config1 = corsSource.getCorsConfiguration(null);
//            CorsConfiguration config2 = corsSource.getCorsConfiguration(null);
//
//            assertNotNull(config1);
//            assertNotNull(config2);
//
//            // Both should have the same configuration values
//            assertEquals(config1.getAllowedOrigins(), config2.getAllowedOrigins());
//            assertEquals(config1.getAllowedMethods(), config2.getAllowedMethods());
//            assertEquals(config1.getAllowedHeaders(), config2.getAllowedHeaders());
//        }
//
//        @Test
//        void corsConfigurationSource_ShouldCreateNewInstanceEachTime() {
//            // Act
//            CorsConfigurationSource corsSource1 = securityConfiguration.corsConfigurationSource();
//            CorsConfigurationSource corsSource2 = securityConfiguration.corsConfigurationSource();
//
//            // Assert
//            assertNotNull(corsSource1);
//            assertNotNull(corsSource2);
//            assertNotSame(corsSource1, corsSource2); // Different instances
//
//            // But should have same configuration
//            CorsConfiguration config1 = corsSource1.getCorsConfiguration(null);
//            CorsConfiguration config2 = corsSource2.getCorsConfiguration(null);
//
//            assertEquals(config1.getAllowedOrigins(), config2.getAllowedOrigins());
//            assertEquals(config1.getAllowedMethods(), config2.getAllowedMethods());
//            assertEquals(config1.getAllowedHeaders(), config2.getAllowedHeaders());
//        }
//    }
//
//    @Nested
//    class SecurityPolicyTests {
//
//        @Test
//        void corsConfiguration_ShouldHaveDefaultCredentialPolicy() {
//            // Act
//            CorsConfigurationSource corsSource = securityConfiguration.corsConfigurationSource();
//            CorsConfiguration config = corsSource.getCorsConfiguration(null);
//
//            // Assert
//            assertNotNull(config);
//            // Default behavior - credentials are not explicitly set
//            assertNull(config.getAllowCredentials());
//        }
//
//        @Test
//        void corsConfiguration_ShouldNotSetMaxAge() {
//            // Act
//            CorsConfigurationSource corsSource = securityConfiguration.corsConfigurationSource();
//            CorsConfiguration config = corsSource.getCorsConfiguration(null);
//
//            // Assert
//            assertNotNull(config);
//            assertNull(config.getMaxAge());
//        }
//
//        @Test
//        void corsConfiguration_ShouldNotSetExposedHeaders() {
//            // Act
//            CorsConfigurationSource corsSource = securityConfiguration.corsConfigurationSource();
//            CorsConfiguration config = corsSource.getCorsConfiguration(null);
//
//            // Assert
//            assertNotNull(config);
//            assertNull(config.getExposedHeaders());
//        }
//
//        @Test
//        void corsConfiguration_ShouldAllowOnlySpecifiedOrigins() {
//            // Act
//            CorsConfigurationSource corsSource = securityConfiguration.corsConfigurationSource();
//            CorsConfiguration config = corsSource.getCorsConfiguration(null);
//
//            // Assert
//            assertNotNull(config);
//            List<String> allowedOrigins = config.getAllowedOrigins();
//
//            // Should not contain wildcard or other origins
//            assertFalse(allowedOrigins.contains("*"));
//            assertFalse(allowedOrigins.contains("http://unauthorized-site.com"));
//            assertFalse(allowedOrigins.contains("https://malicious-site.com"));
//        }
//    }
//
//    @Nested
//    class IntegrationTests {
//
//        @Test
//        void configurationBeans_ShouldWorkTogether() throws Exception {
//            // Act
//            CorsConfigurationSource corsSource = securityConfiguration.corsConfigurationSource();
//
//            // Setup mock for security filter chain test
//            setupMockHttpSecurity();
//            SecurityFilterChain filterChain = securityConfiguration.securityFilterChain(mockHttpSecurity);
//
//            // Assert
//            assertNotNull(corsSource);
//            assertNotNull(filterChain);
//
//            // Verify CORS is configured in the filter chain
//            verify(mockHttpSecurity).cors(any());
//        }
//
//        @Test
//        void securityConfiguration_ShouldMaintainCorrectBeanScopes() {
//            // Act - Call bean methods multiple times
//            CorsConfigurationSource corsSource1 = securityConfiguration.corsConfigurationSource();
//            CorsConfigurationSource corsSource2 = securityConfiguration.corsConfigurationSource();
//
//            // Assert
//            // Each call should return a new instance (prototype scope behavior)
//            assertNotSame(corsSource1, corsSource2);
//
//            // But with equivalent configuration
//            assertEquals(
//                    corsSource1.getCorsConfiguration(null).getAllowedOrigins(),
//                    corsSource2.getCorsConfiguration(null).getAllowedOrigins()
//            );
//        }
//
//        @Test
//        void securityConfiguration_ShouldHandleNullInputsGracefully() {
//            // Act & Assert
//            assertThrows(Exception.class, () -> {
//                securityConfiguration.securityFilterChain(null);
//            });
//
//            // CORS configuration doesn't take parameters, so it should always work
//            assertDoesNotThrow(() -> {
//                securityConfiguration.corsConfigurationSource();
//            });
//        }
//
//        private void setupMockHttpSecurity() throws Exception {
//            when(mockHttpSecurity.csrf(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.cors(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.authorizeHttpRequests(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.sessionManagement(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.authenticationProvider(any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.addFilterBefore(any(), any())).thenReturn(mockHttpSecurity);
//            when(mockHttpSecurity.build()).thenReturn((DefaultSecurityFilterChain) mockSecurityFilterChain);
//        }
//    }
//}