package rencanakan.id.talentPool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rencanakan.id.talentPool.dto.TalentDTO;
import rencanakan.id.talentPool.model.Talent;
import rencanakan.id.talentPool.repository.TalentRepository;

import java.util.Optional;

@Service
public class TalentService implements TalentServiceInterface {

    @Autowired
    private TalentRepository talentRepository;
    private TalentDTO convertToDTO(Talent talent) {
        TalentDTO dto = new TalentDTO();
        dto.setId(talent.getId());
        dto.setFirstName(talent.getFirstName());
        dto.setLastName(talent.getLastName());
        dto.setEmail(talent.getEmail());
        dto.setPhoneNumber(talent.getPhoneNumber());
        dto.setJobTitle(talent.getJobTitle());
        dto.setLocation(talent.getLocation());
        dto.setDomicile(talent.getDomicile());
        return dto;
    }

    private Talent convertToEntity(TalentDTO talentDTO) {
        Talent talent = new Talent();
        talent.setId(talentDTO.getId());
        talent.setFirstName(talentDTO.getFirstName());
        talent.setLastName(talentDTO.getLastName());
        talent.setEmail(talentDTO.getEmail());
        talent.setPhoneNumber(talentDTO.getPhoneNumber());
        talent.setJobTitle(talentDTO.getJobTitle());
        talent.setLocation(talentDTO.getLocation());
        talent.setDomicile(talentDTO.getDomicile());
        return talent;
    }

    public TalentDTO getTalentDTOById(String id) {
        Talent talent = talentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Talent not found with id: " + id));
        return convertToDTO(talent);
    }

    public TalentDTO addTalent(TalentDTO talentDTO) {
        Talent talent = convertToEntity(talentDTO);
        talent = talentRepository.save(talent);
        return convertToDTO(talent);
    }

}
