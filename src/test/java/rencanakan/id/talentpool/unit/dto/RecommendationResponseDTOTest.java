package rencanakan.id.talentpool.unit.dto;

import org.junit.jupiter.api.Test;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationResponseDTOTest {

    @Test
    void testNoArgsConstructorDefaults() {
        RecommendationResponseDTO dto = new RecommendationResponseDTO();
        assertNull(dto.getId());
        assertNull(dto.getTalentId());
        assertNull(dto.getContractorId());
        assertNull(dto.getContractorName());
        assertNull(dto.getMessage());
        assertNull(dto.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        User mockTalent = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("081234567890")
                .nik("1234567890123456")
                .build();

        String id = UUID.randomUUID().toString();

        RecommendationResponseDTO dto = new RecommendationResponseDTO(
                id,
                mockTalent.getId(),
                1L,
                "Contractor Name",
                "Test message",
                StatusType.PENDING);

        assertEquals(id, dto.getId());
        assertEquals(mockTalent.getId(), dto.getTalentId());
        assertEquals(1L, dto.getContractorId());
        assertEquals("Contractor Name", dto.getContractorName());
        assertEquals("Test message", dto.getMessage());
        assertEquals(StatusType.PENDING, dto.getStatus());
    }

    @Test
    void testBuilder() {
        User mockTalent = User.builder()
                .firstName("Builder")
                .lastName("User")
                .email("builder@example.com")
                .password("password123")
                .phoneNumber("081234567890")
                .nik("1234567890123456")
                .build();

        String id = UUID.randomUUID().toString();

        RecommendationResponseDTO dto = RecommendationResponseDTO.builder()
                .id(id)
                .talentId(mockTalent.getId())
                .contractorId(2L)
                .contractorName("Builder Contractor")
                .message("Test builder message")
                .status(StatusType.ACCEPTED)
                .build();

        assertEquals(id, dto.getId());
        assertEquals(mockTalent.getId(), dto.getTalentId());
        assertEquals(2L, dto.getContractorId());
        assertEquals("Builder Contractor", dto.getContractorName());
        assertEquals("Test builder message", dto.getMessage());
        assertEquals(StatusType.ACCEPTED, dto.getStatus());
    }

    @Test
    void testSettersAndGetters() {
        User mockTalent = User.builder()
                .firstName("Setter")
                .lastName("User")
                .email("setter@example.com")
                .password("password123")
                .phoneNumber("081234567890")
                .nik("1234567890123456")
                .build();

        String id = UUID.randomUUID().toString();

        RecommendationResponseDTO dto = new RecommendationResponseDTO();
        dto.setId(id);
        dto.setTalentId(mockTalent.getId());
        dto.setContractorId(3L);
        dto.setContractorName("Setter Contractor");
        dto.setMessage("Test setter message");
        dto.setStatus(StatusType.DECLINED);

        assertEquals(id, dto.getId());
        assertEquals(mockTalent.getId(), dto.getTalentId());
        assertEquals(3L, dto.getContractorId());
        assertEquals("Setter Contractor", dto.getContractorName());
        assertEquals("Test setter message", dto.getMessage());
        assertEquals(StatusType.DECLINED, dto.getStatus());
    }

    @Test
    void testDifferentStatusTypes() {
        RecommendationResponseDTO pendingDto = RecommendationResponseDTO.builder()
                .status(StatusType.PENDING)
                .build();

        RecommendationResponseDTO acceptedDto = RecommendationResponseDTO.builder()
                .status(StatusType.ACCEPTED)
                .build();

        RecommendationResponseDTO declinedDto = RecommendationResponseDTO.builder()
                .status(StatusType.DECLINED)
                .build();

        assertEquals(StatusType.PENDING, pendingDto.getStatus());
        assertEquals(StatusType.ACCEPTED, acceptedDto.getStatus());
        assertEquals(StatusType.DECLINED, declinedDto.getStatus());
    }

    @Test
    void testUserDetails() {
        String userId = UUID.randomUUID().toString();
        User mockTalent = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .phoneNumber("081234567890")
                .nik("1234567890123456")
                .build();

        RecommendationResponseDTO dto = RecommendationResponseDTO.builder()
                .talentId(mockTalent.getId())
                .build();


        assertEquals(userId, dto.getTalentId());
    }

    @Test
    void testNullableFields() {
        RecommendationResponseDTO dto = RecommendationResponseDTO.builder()
                .contractorId(1L)
                .contractorName("Contractor Name")
                .message("Test message")
                .status(StatusType.PENDING)
                .build();

        assertNull(dto.getId());
        assertNull(dto.getTalentId());
        assertNotNull(dto.getContractorId());
        assertNotNull(dto.getContractorName());
        assertNotNull(dto.getMessage());
        assertNotNull(dto.getStatus());
    }
}
