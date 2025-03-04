package rencanakan.id.talentPool.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import rencanakan.id.talentPool.dto.ExperienceRequestDTO;
import rencanakan.id.talentPool.model.Experience;
import rencanakan.id.talentPool.repository.ExperienceRepository;

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
        System.out.println(request.getCompany());

        Set<ConstraintViolation<ExperienceRequestDTO>> violations = validator.validate(request);
        System.out.println(violations);

        if (!violations.isEmpty()) {
            System.out.println("she");

            throw new IllegalArgumentException("Validation failed");
        }

        System.out.println("he");
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
