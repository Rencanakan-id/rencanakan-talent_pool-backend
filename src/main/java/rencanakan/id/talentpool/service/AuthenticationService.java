package rencanakan.id.talentpool.service;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.dto.LoginRequestDTO;
import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;

import java.util.Optional;


@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    private void validateUserUniqueness(UserRequestDTO request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new RuntimeException("Email " + request.getEmail() + " sudah terdaftar.");
                });

        userRepository.findByNik(request.getNik())
                .ifPresent(user -> {
                    throw new RuntimeException("NIK " + request.getNik() + " sudah terdaftar.");
                });

        userRepository.findByNpwp(request.getNpwp())
                .ifPresent(user -> {
                    throw new RuntimeException("NPWP " + request.getNpwp() + " sudah terdaftar.");
                });

        userRepository.findByPhoneNumber(request.getPhoneNumber())
                .ifPresent(user -> {
                    throw new RuntimeException("Nomor telepon " + request.getPhoneNumber() + " sudah terdaftar.");
                });
    }

    public User signup(@Valid UserRequestDTO request) throws BadRequestException {
        validateUserUniqueness(request);

        User newUser = User.builder()
                .firstName(request.getFirstName())
                .password(passwordEncoder.encode(request.getPassword()))
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .photo(request.getPhoto())
                .aboutMe(request.getAboutMe())
                .nik(request.getNik())
                .npwp(request.getNpwp())
                .photoKtp(request.getPhotoKtp())
                .photoNpwp(request.getPhotoNpwp())
                .photoIjazah(request.getPhotoIjazah())
                .experienceYears(request.getExperienceYears())
                .skkLevel(request.getSkkLevel())
                .currentLocation(request.getCurrentLocation())
                .preferredLocations(request.getPreferredLocations())
                .skill(request.getSkill())
                .price(request.getPrice())
                .build();
        return userRepository.save(newUser);
    }

    public User authenticate(LoginRequestDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        return userRepository.findByEmail(input.getEmail()).orElse(null);
    }
}