package rencanakan.id.talentpool.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.repository.ExperienceRepository;

import java.util.Set;

@Service
public class ExperienceServiceImpl implements ExperienceService {
    private final ExperienceRepository experienceRepository;
    private final Validator validator;

    public ExperienceServiceImpl(ExperienceRepository experienceRepository, Validator validator) {
        this.experienceRepository = experienceRepository;
        this.validator = validator;
    }

    @Override
    public Experience createExperience(ExperienceRequestDTO request) {
        Set<ConstraintViolation<ExperienceRequestDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Validation failed");
        }

        Experience newExperience = Experience.builder()
                .title(request.getTitle())
                .company(request.getCompany())
                .employmentType(request.getEmploymentType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .location(request.getLocation())
                .locationType(request.getLocationType())
                .talentId(request.getTalentId())
                .build();

        return experienceRepository.save(newExperience);
    }
}
