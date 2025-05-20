package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import jakarta.validation.Valid;
import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import org.springframework.stereotype.Service;

import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.repository.RecommendationRepository;

import rencanakan.id.talentpool.model.User;

import rencanakan.id.talentpool.repository.UserRepository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository, UserRepository userRepository) {
        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
    }
    @Override
    public RecommendationResponseDTO editStatusById( String userId, String id, StatusType status) {

        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation with ID " + id + " not found"));

        if (!(recommendation.getTalent().getId().equals(userId))) {
            throw new AccessDeniedException("You are not allowed to edit this recommendation.");
        }
        recommendation.setStatus(status);
        recommendationRepository.save(recommendation);
        return DTOMapper.map(recommendation, RecommendationResponseDTO.class);
    }

    @Override
    public RecommendationResponseDTO getById(String id) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation not found with id: " + id));

        return DTOMapper.map(recommendation, RecommendationResponseDTO.class);
    }

    @Override
    public RecommendationResponseDTO deleteById(String userId, String id) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation with id " + id + " not found."));
        if (!(recommendation.getTalent().getId().equals(userId))) {
            throw new AccessDeniedException("You are not allowed to delete this recommendation.");
        }
        recommendationRepository.deleteById(id);
        return  DTOMapper.map(recommendation, RecommendationResponseDTO.class);

    }

    @Override
    public List<RecommendationResponseDTO> getByTalentId(String talentId) {
        User talent = userRepository.findById(talentId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + talentId));

        List<Recommendation> recommendations = recommendationRepository.findByTalent(talent);

        return recommendations.stream()
            .map(recommendation -> DTOMapper.map(recommendation, RecommendationResponseDTO.class))
            .toList();
    }

    @Override
    public List<RecommendationResponseDTO> getByTalentIdAndStatus(String talentId, StatusType status) {
        User talent = userRepository.findById(talentId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + talentId));

        List<Recommendation> recommendations = recommendationRepository.findByTalentAndStatus(talent, status);

        return recommendations.stream()
                .map(recommendation -> DTOMapper.map(recommendation, RecommendationResponseDTO.class))
                .toList();
    }

    @Override
    public Map<StatusType, List<RecommendationResponseDTO>> getByTalentIdAndGroupedByStatus(String talentId) {
        Map<StatusType, List<RecommendationResponseDTO>> groupedRecommendations = new EnumMap<>(StatusType.class);

        for (StatusType status : StatusType.values()) {
            groupedRecommendations.put(status, getByTalentIdAndStatus(talentId, status));
        }

        return groupedRecommendations;
    }

    @Override
    public RecommendationResponseDTO createRecommendation(String talentId, @Valid RecommendationRequestDTO recommendation) {

        if (talentId == null || recommendation == null) {
            throw new IllegalArgumentException("Recommendation request cannot be null");
        }

        User talent = userRepository.findById(talentId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + talentId));

        Recommendation newRecommendation = DTOMapper.map(recommendation, Recommendation.class);
        newRecommendation.setTalent(talent);
        Recommendation savedRecommendation = recommendationRepository.save(newRecommendation);
        return DTOMapper.map(savedRecommendation, RecommendationResponseDTO.class);
    }

    @Override
    public RecommendationResponseDTO editById(Long contractorId, String id, RecommendationRequestDTO editRequest) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation with ID " + id + " not found"));

        if (!recommendation.getContractorId().equals(contractorId)) {
            throw new AccessDeniedException("Only the contractor who created this recommendation can edit it");
        }

        recommendation.setMessage(editRequest.getMessage());
        
        StatusType currentStatus = recommendation.getStatus();
        StatusType requestedStatus = editRequest.getStatus();
        
        if (currentStatus == StatusType.ACCEPTED || currentStatus == StatusType.DECLINED) {
            recommendation.setStatus(StatusType.PENDING);
        } else if (requestedStatus != null) {
            recommendation.setStatus(requestedStatus);
        }

        Recommendation updatedRecommendation = recommendationRepository.save(recommendation);

        return DTOMapper.map(updatedRecommendation, RecommendationResponseDTO.class);
    }
}
