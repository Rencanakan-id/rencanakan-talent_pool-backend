package rencanakan.id.talentpool.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.dto.LoginUserDTO;
import rencanakan.id.talentpool.dto.UserProfileRequestDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserProfileRepository;

import java.util.Set;

@Service
public class AuthenticationService {
    private final UserProfileRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final Validator validator;

    public AuthenticationService(
            UserProfileRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder, Validator validator

    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    public User signup(@Valid UserProfileRequestDTO request) {
        User newProfile = User.builder()
                .firstName(request.getFirstName())
                .password(passwordEncoder.encode(request.getPassword()))
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .job(request.getJob())
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
                .build();
        return userRepository.save(newProfile);
    }

    public User authenticate(LoginUserDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail()).orElseThrow();
    }
}