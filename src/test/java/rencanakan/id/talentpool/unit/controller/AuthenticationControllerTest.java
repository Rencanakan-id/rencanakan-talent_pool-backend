package rencanakan.id.talentpool.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rencanakan.id.talentpool.controller.AuthenticationController;
import rencanakan.id.talentpool.dto.LoginRequestDTO;
import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.AuthenticationService;
import rencanakan.id.talentpool.service.JwtService;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private UserRequestDTO validUserRequestDTO;

    @BeforeEach
    void setUp() {
        objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        validUserRequestDTO = createUser();
    }

    private UserRequestDTO createUser() {
        return UserRequestDTO.builder()
                .firstName("William")
                .lastName("Smith")
                .email("william@gmial.com")
                .phoneNumber("1234567890")
                .photo("profile-photo.jpg")
                .aboutMe("I am a software developer.")
                .nik("1234567890123456")
                .npwp("987654321098765")
                .photoKtp("ktp-photo.jpg")
                .photoNpwp("npwp-photo.jpg")
                .photoIjazah("ijazah-photo.jpg")
                .experienceYears(5)
                .skkLevel("Level 3")
                .currentLocation("Jakarta")
                .preferredLocations(Arrays.asList("Bandung", "Surabaya"))
                .skill("Java, Spring Boot, Microservices")
                .password("password")
                .price(1000000)
                .build();
    }

    @Test
    void testRegister_Success() throws Exception {
        User registeredUser = new User();
        registeredUser.setId(UUID.randomUUID().toString());
        registeredUser.setEmail(validUserRequestDTO.getEmail());

        when(authenticationService.signup(any(UserRequestDTO.class))).thenReturn(registeredUser);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(authenticationService, times(1)).signup(any(UserRequestDTO.class));
    }

    @Test
    void testRegister_InvalidEmail() throws Exception {
        UserRequestDTO invalidUserRequestDTO = createUser();
        invalidUserRequestDTO.setEmail("hehe");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).signup(any(UserRequestDTO.class));
    }

    @Test
    void testRegister_MissingRequiredFields() throws Exception {
        UserRequestDTO incompleteUserRequestDTO = createUser();
        incompleteUserRequestDTO.setEmail(null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incompleteUserRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).signup(any(UserRequestDTO.class));
    }

    private LoginRequestDTO createLoginRequest(String email, String password) {
        return LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();
    }

    @Test
    void testLogin_Success() throws Exception {
        User authenticatedUser = new User();
        authenticatedUser.setId(UUID.randomUUID().toString());
        authenticatedUser.setEmail("william@gmial.com");

        String jwtToken = "mocked-jwt-token";
        long expirationTime = 3600L;

        when(authenticationService.authenticate(any(LoginRequestDTO.class))).thenReturn(authenticatedUser);
        when(jwtService.generateToken(authenticatedUser)).thenReturn(jwtToken);
        when(jwtService.getExpirationTime()).thenReturn(expirationTime);

        LoginRequestDTO validLoginRequest = createLoginRequest("william@gmial.com", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value(jwtToken))
                .andExpect(jsonPath("$.data.expiresIn").value(expirationTime));

        verify(authenticationService, times(1)).authenticate(any(LoginRequestDTO.class));
        verify(jwtService, times(1)).generateToken(authenticatedUser);
        verify(jwtService, times(1)).getExpirationTime();
    }

    @Test
    void testRegister_DuplicateEmail_ThrowsBadRequest() throws Exception {
        when(authenticationService.signup(any(UserRequestDTO.class)))
                .thenThrow(new RuntimeException("Email " + validUserRequestDTO.getEmail() + " sudah terdaftar."));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Email " + validUserRequestDTO.getEmail() + " sudah terdaftar."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(authenticationService, times(1)).signup(any(UserRequestDTO.class));
    }

    @Test
    void testRegister_DuplicateNik_ThrowsBadRequest() throws Exception {
        when(authenticationService.signup(any(UserRequestDTO.class)))
                .thenThrow(new RuntimeException("NIK " + validUserRequestDTO.getNik() + " sudah terdaftar."));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("NIK " + validUserRequestDTO.getNik() + " sudah terdaftar."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(authenticationService, times(1)).signup(any(UserRequestDTO.class));
    }

    @Test
    void testRegister_DuplicateNpwp_ThrowsBadRequest() throws Exception {
        when(authenticationService.signup(any(UserRequestDTO.class)))
                .thenThrow(new RuntimeException("NPWP " + validUserRequestDTO.getNpwp() + " sudah terdaftar."));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("NPWP " + validUserRequestDTO.getNpwp() + " sudah terdaftar."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(authenticationService, times(1)).signup(any(UserRequestDTO.class));
    }

    @Test
    void testRegister_DuplicatePhoneNumber_ThrowsBadRequest() throws Exception {
        when(authenticationService.signup(any(UserRequestDTO.class)))
                .thenThrow(new RuntimeException("Nomor telepon " + validUserRequestDTO.getPhoneNumber() + " sudah terdaftar."));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Nomor telepon " + validUserRequestDTO.getPhoneNumber() + " sudah terdaftar."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(authenticationService, times(1)).signup(any(UserRequestDTO.class));
    }
}