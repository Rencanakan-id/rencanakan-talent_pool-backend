package rencanakan.id.talentpool.service;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.dto.LoginRequestDTO;
import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.model.PasswordResetToken;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.PasswordResetTokenRepository;
import rencanakan.id.talentpool.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserService userService,
            PasswordResetTokenRepository passwordResetTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public User signup(@Valid UserRequestDTO request) throws BadRequestException {
        Optional<User> existingUserByEmail = userRepository.findByEmail(request.getEmail());
        if (existingUserByEmail.isPresent()) {
            throw new BadRequestException("Email " + request.getEmail() + " is already in use.");
        }
        Optional<User> existingUserByNik = userRepository.findByNik(request.getNik());
        if (existingUserByNik.isPresent()) {
            throw new BadRequestException("NIK " + request.getNik() + " is already in use.");
        }
        Optional<User> existingUserByNpwp = userRepository.findByNpwp(request.getNpwp());
        if (existingUserByNpwp.isPresent()) {
            throw new BadRequestException("NPWP " + request.getNpwp() + " is already in use.");
        }
        Optional<User> existingUserByPhoneNumber = userRepository.findByPhoneNumber(request.getPhoneNumber());
        if (existingUserByPhoneNumber.isPresent()) {
            throw new BadRequestException("Phone number " + request.getPhoneNumber() + " is already in use.");
        }

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

    public void resetPasswordWithToken(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token tidak valid"));

        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token sudah kadaluarsa atau sudah digunakan");
        }

        var user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}