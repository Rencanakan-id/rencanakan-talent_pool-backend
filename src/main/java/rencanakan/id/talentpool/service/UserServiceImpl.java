package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO getById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
        
        return DTOMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO editUser(String id, User edited) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));

        try {
            updateUserFields(user, edited);
            userRepository.save(user);
            return DTOMapper.map(user, UserResponseDTO.class);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to update user");
        }
    }

    private void updateUserFields(User user, User edited) {
        updateIfNotNull(edited.getEmail(), user::setEmail);
        updateIfNotNull(edited.getFirstName(), user::setFirstName);
        updateIfNotNull(edited.getLastName(), user::setLastName);
        updateIfNotNull(edited.getPhoneNumber(), user::setPhoneNumber);

        updateIfNotNull(edited.getPhoto(), user::setPhoto);
        updateIfNotNull(edited.getAboutMe(), user::setAboutMe);
        updateIfNotNull(edited.getNik(), user::setNik);
        updateIfNotNull(edited.getNpwp(), user::setNpwp);

        updateIfNotNull(edited.getPhotoKtp(), user::setPhotoKtp);
        updateIfNotNull(edited.getPhotoNpwp(), user::setPhotoNpwp);
        updateIfNotNull(edited.getPhotoIjazah(), user::setPhotoIjazah);
        
        updateIfNotNull(edited.getExperienceYears(), user::setExperienceYears);
        updateIfNotNull(edited.getSkkLevel(), user::setSkkLevel);
        updateIfNotNull(edited.getCurrentLocation(), user::setCurrentLocation);
        updateIfNotNull(edited.getPreferredLocations(), user::setPreferredLocations);
        updateIfNotNull(edited.getSkill(), user::setSkill);
    }

    private <T> void updateIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElse(null);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
