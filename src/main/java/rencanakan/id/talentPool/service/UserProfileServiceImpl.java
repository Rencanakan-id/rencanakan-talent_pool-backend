package rencanakan.id.talentPool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rencanakan.id.talentPool.dto.UserProfileResponseDTO;
import rencanakan.id.talentPool.model.UserProfile;
import rencanakan.id.talentPool.repository.UserProfileRepository;

import java.util.Optional;
import java.util.function.Consumer;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public UserProfileResponseDTO getById(String id) {
        Optional<UserProfile> userProfileOptional = userProfileRepository.findById(id);
        
        if (userProfileOptional.isPresent()) {
            UserProfile userProfile = userProfileOptional.get();
            return convertToDTO(userProfile);
        }
        
        return null;
    }

    @Override
    public UserProfileResponseDTO editProfile(String id, UserProfile editedProfile) {
        Optional<UserProfile> userProfileOptional = userProfileRepository.findById(id);

        if (userProfileOptional.isPresent()) {
            UserProfile userProfile = userProfileOptional.get();

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

    private void updateFields(UserProfile userProfile, UserProfile editedProfile) {
        applyIfNotNull(editedProfile.getEmail(), userProfile::setEmail);
        applyIfNotNull(editedProfile.getPassword(), userProfile::setPassword);
        applyIfNotNull(editedProfile.getNik(), userProfile::setNik);
        applyIfNotNull(editedProfile.getFirstName(), userProfile::setFirstName);
        applyIfNotNull(editedProfile.getLastName(), userProfile::setLastName);
        applyIfNotNull(editedProfile.getPhoneNumber(), userProfile::setPhoneNumber);
        applyIfNotNull(editedProfile.getAddress(), userProfile::setAddress);
        applyIfNotNull(editedProfile.getJob(), userProfile::setJob);
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

    private <T> void applyIfNotNull(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }

    private UserProfileResponseDTO convertToDTO(UserProfile userProfile) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(userProfile.getId());
        dto.setFirstName(userProfile.getFirstName());
        dto.setLastName(userProfile.getLastName());
        dto.setEmail(userProfile.getEmail());
        dto.setPhoneNumber(userProfile.getPhoneNumber());
        dto.setAddress(userProfile.getAddress());
        dto.setJob(userProfile.getJob());
        dto.setPhoto(userProfile.getPhoto());
        dto.setAboutMe(userProfile.getAboutMe());
        dto.setNik(userProfile.getNik());
        dto.setNpwp(userProfile.getNpwp());
        dto.setPhotoKtp(userProfile.getPhotoKtp());
        dto.setPhotoNpwp(userProfile.getPhotoNpwp());
        dto.setPhotoIjazah(userProfile.getPhotoIjazah());
        dto.setExperienceYears(userProfile.getExperienceYears());
        dto.setSkkLevel(userProfile.getSkkLevel());
        dto.setCurrentLocation(userProfile.getCurrentLocation());
        dto.setPreferredLocations(userProfile.getPreferredLocations());
        dto.setSkill(userProfile.getSkill());
        return dto;
    }
}
