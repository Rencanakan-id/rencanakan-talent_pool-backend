package rencanakan.id.talentpool.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rencanakan.id.talentpool.dto.UserProfileRequestDTO;
import rencanakan.id.talentpool.dto.UserProfileResponseDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserProfileRepository;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final Validator validator;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository, Validator validator) {
        this.userProfileRepository = userProfileRepository;
        this.validator = validator;
    }

    @Override
    public UserProfileResponseDTO getById(String id) {
        Optional<User> userProfileOptional = userProfileRepository.findById(id);

        if (userProfileOptional.isPresent()) {
            User user = userProfileOptional.get();
            return convertToDTO(user);
        }

        return null;
    }

    @Override
    public UserProfileResponseDTO editProfile(String id, User editedProfile) {
        Optional<User> userProfileOptional = userProfileRepository.findById(id);

        if (userProfileOptional.isPresent()) {
            User userProfile = userProfileOptional.get();

            try {
                updateFields(userProfile, editedProfile);

                userProfileRepository.save(userProfile);
                return convertToDTO(userProfile);
            } catch (IllegalArgumentException e) {
                throw e;
            }
        }

        return null;
    }

    private void updateFields(User userProfile, User editedProfile) {
        applyIfNotNull(editedProfile.getEmail(), userProfile::setEmail);
        applyIfNotNull(editedProfile.getPassword(), userProfile::setPassword);
        applyIfNotNull(editedProfile.getNik(), userProfile::setNik);
        applyIfNotNull(editedProfile.getFirstName(), userProfile::setFirstName);
        applyIfNotNull(editedProfile.getLastName(), userProfile::setLastName);
        applyIfNotNull(editedProfile.getPhoneNumber(), userProfile::setPhoneNumber);
        applyIfNotNull(editedProfile.getPhoto(), userProfile::setPhoto);
        applyIfNotNull(editedProfile.getAboutMe(), userProfile::setAboutMe);
        applyIfNotNull(editedProfile.getNpwp(), userProfile::setNpwp);
        applyIfNotNull(editedProfile.getPhotoKtp(), userProfile::setPhotoKtp);
        applyIfNotNull(editedProfile.getPhotoNpwp(), userProfile::setPhotoNpwp);
        applyIfNotNull(editedProfile.getPhotoIjazah(), userProfile::setPhotoIjazah);
        applyIfNotNull(editedProfile.getExperienceYears(), userProfile::setExperienceYears);
        applyIfNotNull(editedProfile.getSkkLevel(), userProfile::setSkkLevel);
        applyIfNotNull(editedProfile.getCurrentLocation(), userProfile::setCurrentLocation);
        applyIfNotNull(editedProfile.getPreferredLocations(), userProfile::setPreferredLocations);
        applyIfNotNull(editedProfile.getSkill(), userProfile::setSkill);
    }


    @Override
    public User findByEmail(String email) {
        Optional<User> userProfileOptional = userProfileRepository.findByEmail(email);
        return userProfileOptional.orElse(null);
    }

    private <T> void applyIfNotNull(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }

    private UserProfileResponseDTO convertToDTO(User user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setPhoto(user.getPhoto());
        dto.setAboutMe(user.getAboutMe());
        dto.setNik(user.getNik());
        dto.setNpwp(user.getNpwp());
        dto.setPhotoKtp(user.getPhotoKtp());
        dto.setPhotoNpwp(user.getPhotoNpwp());
        dto.setPhotoIjazah(user.getPhotoIjazah());
        dto.setExperienceYears(user.getExperienceYears());
        dto.setSkkLevel(user.getSkkLevel());
        dto.setCurrentLocation(user.getCurrentLocation());
        dto.setPreferredLocations(user.getPreferredLocations());
        dto.setSkill(user.getSkill());
        return dto;
    }

//    @Override
//    public User createProfile(UserProfileRequestDTO request) {
//        Set<ConstraintViolation<UserProfileRequestDTO>> violations = validator.validate(request);
//        if (!violations.isEmpty()) {
//            throw new IllegalArgumentException("Validation failed");
//        }
//
//        User newProfile = User.builder()
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .email(request.getEmail())
//                .phoneNumber(request.getPhoneNumber())
//                .address(request.getAddress())
//                .job(request.getJob())
//                .photo(request.getPhoto())
//                .aboutMe(request.getAboutMe())
//                .nik(request.getNik())
//                .npwp(request.getNpwp())
//                .photoKtp(request.getPhotoKtp())
//                .photoNpwp(request.getPhotoNpwp())
//                .photoIjazah(request.getPhotoIjazah())
//                .experienceYears(request.getExperienceYears())
//                .skkLevel(request.getSkkLevel())
//                .currentLocation(request.getCurrentLocation())
//                .preferredLocations(request.getPreferredLocations())
//                .skill(request.getSkill())
//                .build();
//        return userProfileRepository.save(newProfile);
//    }
}