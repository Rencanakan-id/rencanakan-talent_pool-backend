package rencanakan.id.talentPool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rencanakan.id.talentPool.dto.UserProfileResponseDTO;
import rencanakan.id.talentPool.model.UserProfile;
import rencanakan.id.talentPool.repository.UserProfileRepository;

import java.util.Optional;

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
