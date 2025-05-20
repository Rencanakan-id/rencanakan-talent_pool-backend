package rencanakan.id.talentpool.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver;import rencanakan.id.talentpool.controller.ErrorController;
import rencanakan.id.talentpool.dto.*;
import rencanakan.id.talentpool.service.RecommendationService;
import rencanakan.id.talentpool.enums.StatusType;

import org.junit.jupiter.api.Nested;

import org.mockito.InjectMocks;

import rencanakan.id.talentpool.controller.RecommendationController;
import rencanakan.id.talentpool.model.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    private User mockTalent;

    private RecommendationRequestDTO createValidRequestDTO() {
        return RecommendationRequestDTO.builder()
                .contractorId(1L)
                .contractorName("Contractor name")
                .message("Test controller message")
                .status(StatusType.PENDING)
                .build();
    }

    private RecommendationResponseDTO createMockResponseDTO() {
        return RecommendationResponseDTO.builder()
                .id("recommendation123")
                .talentId("user123")
                .contractorId(1L)
                .contractorName("Contractor name")
                .message("Test controller message")
                .status(StatusType.PENDING)
                .build();
    }

    private ObjectMapper objectMapper;

    private String id;

    private RecommendationResponseDTO responseDTO;

    private void setupUnauthorizedMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(recommendationController)
                .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(null))
                .build();
    }

    @Nested
    class CreateRecommendationTest {

        @BeforeEach
        void setUp() {
            mapper = Jackson2ObjectMapperBuilder.json()
                    .modules(new JavaTimeModule())
                    .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .build();

            mockMvc = MockMvcBuilders.standaloneSetup(recommendationController)
                    .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                    .build();

            mockTalent = User.builder()
                    .id("user123")
                    .firstName("Test")
                    .lastName("Talent")
                    .email("test@example.com")
                    .password("Password123")
                    .phoneNumber("081234567890")
                    .nik("1234567890123456")
                    .build();
        }

        @Test
        void testCreateRecommendation_Success() throws Exception {
            RecommendationRequestDTO request = createValidRequestDTO();
            RecommendationResponseDTO mockResponseDTO = createMockResponseDTO();

            when(recommendationService.createRecommendation(eq("user123"), any(RecommendationRequestDTO.class)))
                    .thenReturn(mockResponseDTO);

            mockMvc.perform(post("/recommendations/contractor/{talentId}", "user123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(SecurityMockMvcRequestPostProcessors.user(mockTalent))
                            .content(mapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value("recommendation123"))
                    .andExpect(jsonPath("$.data.contractorId").value(1L))
                    .andExpect(jsonPath("$.data.contractorName").value("Contractor name"))
                    .andExpect(jsonPath("$.data.message").value("Test controller message"))
                    .andExpect(jsonPath("$.data.status").value("PENDING"));
        }

        @Test
        void testCreateRecommendation_BadRequest() throws Exception {
            RecommendationRequestDTO invalidRequest = new RecommendationRequestDTO();

            mockMvc.perform(post("/recommendations/contractor/{talentId}", "user123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(SecurityMockMvcRequestPostProcessors.user(mockTalent))
                            .content(mapper.writeValueAsString(invalidRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testCreateRecommendation_NotFound() throws Exception {
            RecommendationRequestDTO request = createValidRequestDTO();

            when(recommendationService.createRecommendation(eq("user123"), any(RecommendationRequestDTO.class)))
                    .thenThrow(new RuntimeException("Talent not found"));

            mockMvc.perform(post("/recommendations/contractor/{talentId}", "user123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(SecurityMockMvcRequestPostProcessors.user(mockTalent))
                            .content(mapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors").value("Talent not found"));
        }
    }


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(recommendationController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setControllerAdvice(new ErrorController())
                .setCustomArgumentResolvers(new PrincipalMethodArgumentResolver())
                .build();

        id = "1";

        responseDTO = new RecommendationResponseDTO(
                id, "talentId", 123L, "contractorName", "Some message", StatusType.ACCEPTED
        );
    }

    @Nested
    class PatchStatus {
        @Test
        void testEditStatusById() throws Exception {
            when(recommendationService.editStatusById(any(), eq(id), any())).thenReturn(responseDTO);

            mockMvc.perform(patch("/recommendations/{id}", id)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(StatusType.ACCEPTED)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(responseDTO.getId()))
                    .andExpect(jsonPath("$.data.talentId").value(responseDTO.getTalentId()))
                    .andExpect(jsonPath("$.data.contractorId").value(responseDTO.getContractorId()))
                    .andExpect(jsonPath("$.data.contractorName").value(responseDTO.getContractorName()))
                    .andExpect(jsonPath("$.data.message").value(responseDTO.getMessage()))
                    .andExpect(jsonPath("$.data.status").value(responseDTO.getStatus().toString()))
                    .andExpect(jsonPath("$.errors").isEmpty());
        }

        @Test
        void testEditStatusById_RecommendationNotFound() throws Exception {
            when(recommendationService.editStatusById(any(), eq(id), any()))
                    .thenThrow(new EntityNotFoundException("Recommendation with ID " + id + " not found"));

            mockMvc.perform(patch("/recommendations/{id}", id)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(StatusType.ACCEPTED)))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void testEditStatus_UserNotAuthorized_ReturnsForbidden() throws Exception {

            when(recommendationService.editStatusById(any(), eq(id), any()))
                    .thenThrow(new AccessDeniedException("You are not allowed to edit this recommendation."));

            mockMvc.perform(patch("/recommendations/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(StatusType.ACCEPTED)))
                    .andExpect(status().isForbidden()) // Expect 403 Forbidden status
                    .andExpect(jsonPath("$.errors").value("You are not allowed to edit this recommendation.")); // Check error message
        }

        @Test
        void testEditStatusById_ModifiedResponse() throws Exception {
            RecommendationResponseDTO modifiedResponseDTO = new RecommendationResponseDTO(
                    "2", "newTalentId", 456L, "newContractorName", "Modified message", StatusType.PENDING
            );

            when(recommendationService.editStatusById(any(), eq(id), any())).thenReturn(modifiedResponseDTO);

            mockMvc.perform(patch("/recommendations/{id}", id)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(StatusType.ACCEPTED)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(modifiedResponseDTO.getId()))
                    .andExpect(jsonPath("$.data.talentId").value(modifiedResponseDTO.getTalentId()))
                    .andExpect(jsonPath("$.data.contractorId").value(modifiedResponseDTO.getContractorId()))
                    .andExpect(jsonPath("$.data.contractorName").value(modifiedResponseDTO.getContractorName()))
                    .andExpect(jsonPath("$.data.message").value(modifiedResponseDTO.getMessage()))
                    .andExpect(jsonPath("$.data.status").value(modifiedResponseDTO.getStatus().toString()))
                    .andExpect(jsonPath("$.errors").isEmpty());


        }
    }

    @Nested
    class DeleteRecommendationTests{
        @Test
        void deleteByStatusId_NotFound_ShouldReturn404() throws Exception {
            String notFoundId = "999";
            doThrow(new EntityNotFoundException("Recommendation with id " + notFoundId + " not found."))
                    .when(recommendationService).deleteById(any(), any());

            mockMvc.perform(delete("/recommendations/{id}", notFoundId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors").value("Recommendation with id " + notFoundId + " not found."));

        }

        @Test
        public void testDelete_UserNotAuthorized_ReturnsForbidden() throws Exception {

            when(recommendationService.deleteById(any(), eq(id)))
                    .thenThrow(new AccessDeniedException("You are not allowed to delete this recommendation."));

            mockMvc.perform(delete("/recommendations/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(StatusType.ACCEPTED)))
                    .andExpect(status().isForbidden()) // Expect 403 Forbidden status
                    .andExpect(jsonPath("$.errors").value("You are not allowed to delete this recommendation.")); // Check error message
        }
        @Test
        void deleteByStatusId_Success_ShouldReturn200() throws Exception {
            when(recommendationService.deleteById(any(), eq(id))).thenReturn(responseDTO);
            mockMvc.perform(delete("/recommendations/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()) // Harus 200 OK
                    .andExpect(jsonPath("$.data.id").value(id))
                    .andExpect(jsonPath("$.data.talentId").value("talentId"))
                    .andExpect(jsonPath("$.data.contractorId").value(123L))
                    .andExpect(jsonPath("$.data.contractorName").value("contractorName"))
                    .andExpect(jsonPath("$.data.message").value("Some message"))
                    .andExpect(jsonPath("$.data.status").value("ACCEPTED"));

        }
    }

	@Nested
	class AcceptRecommendationTests {
		private User testUser;

		@BeforeEach
		void setUp() {
			testUser = new User();
			testUser.setId("user-id-1");
			testUser.setEmail("john.doe@email.com");

			mockMvc = MockMvcBuilders
					.standaloneSetup(recommendationController)
					.setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(testUser))
					.setControllerAdvice(new ErrorController())
					.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
					.build();
		}

		@Test
		void testAcceptRecommendation_Success() throws Exception {
			String recommendationId = "rec-id-1";
			RecommendationResponseDTO acceptedRecommendation = new RecommendationResponseDTO(
					recommendationId, "user-id-1", 123L, "contractorName", "Some message", StatusType.ACCEPTED
			);
			
			when(recommendationService.editStatusById(testUser.getId(), recommendationId, StatusType.ACCEPTED))
					.thenReturn(acceptedRecommendation);

			mockMvc.perform(patch("/recommendations/{recommendationId}/accept", recommendationId)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.data.id").value(recommendationId))
					.andExpect(jsonPath("$.data.status").value("ACCEPTED"))
					.andExpect(jsonPath("$.errors").isEmpty());
			
			verify(recommendationService).editStatusById(testUser.getId(), recommendationId, StatusType.ACCEPTED);
		}
		
		@Test
		void testAcceptRecommendation_NotFound() throws Exception {
			String nonExistingId = "non-existing-id";
			
			when(recommendationService.editStatusById(testUser.getId(), nonExistingId, StatusType.ACCEPTED))
					.thenThrow(new EntityNotFoundException("Recommendation with ID " + nonExistingId + " not found"));

			mockMvc.perform(patch("/recommendations/{recommendationId}/accept", nonExistingId)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.errors").value("Recommendation with ID " + nonExistingId + " not found"));
			
			verify(recommendationService).editStatusById(testUser.getId(), nonExistingId, StatusType.ACCEPTED);
		}
		
		@Test
		void testAcceptRecommendation_Unauthorized() throws Exception {
			String recommendationId = "rec-id-1";
			
			when(recommendationService.editStatusById(testUser.getId(), recommendationId, StatusType.ACCEPTED))
					.thenThrow(new AccessDeniedException("You are not allowed to edit this recommendation."));

			mockMvc.perform(patch("/recommendations/{recommendationId}/accept", recommendationId)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isForbidden())
					.andExpect(jsonPath("$.errors").value("You are not allowed to edit this recommendation."));
			
			verify(recommendationService).editStatusById(testUser.getId(), recommendationId, StatusType.ACCEPTED);
		}
		
		@Test
		void testAcceptRecommendation_AlreadyAccepted() throws Exception {
			String recommendationId = "already-accepted-id";
			RecommendationResponseDTO alreadyAcceptedRecommendation = new RecommendationResponseDTO(
					recommendationId, "user-id-1", 123L, "contractorName", "Already accepted message", StatusType.ACCEPTED
			);
			
			when(recommendationService.editStatusById(testUser.getId(), recommendationId, StatusType.ACCEPTED))
					.thenReturn(alreadyAcceptedRecommendation);

			mockMvc.perform(patch("/recommendations/{recommendationId}/accept", recommendationId)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.data.id").value(recommendationId))
					.andExpect(jsonPath("$.data.status").value("ACCEPTED"))
					.andExpect(jsonPath("$.errors").isEmpty());
			
			verify(recommendationService).editStatusById(testUser.getId(), recommendationId, StatusType.ACCEPTED);
		}
		
		@Test
		void testAcceptRecommendation_ServerError() throws Exception {
			String recommendationId = "error-id";
			
			when(recommendationService.editStatusById(testUser.getId(), recommendationId, StatusType.ACCEPTED))
					.thenThrow(new RuntimeException("Unexpected server error"));

			mockMvc.perform(patch("/recommendations/{recommendationId}/accept", recommendationId)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.errors").value("Unexpected server error"));
			
			verify(recommendationService).editStatusById(testUser.getId(), recommendationId, StatusType.ACCEPTED);
		}
	}

	@Nested
	class RejectRecommendationTests {
		private User testUser;

		@BeforeEach
		void setUp() {
			testUser = new User();
			testUser.setId("user-id-1");
			testUser.setEmail("john.doe@email.com");

			mockMvc = MockMvcBuilders
					.standaloneSetup(recommendationController)
					.setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(testUser))
					.setControllerAdvice(new ErrorController())
					.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
					.build();
		}

		@Test
		void testRejectRecommendation_Success() throws Exception {
			String recommendationId = "rec-id-1";
			RecommendationResponseDTO rejectedRecommendation = new RecommendationResponseDTO(
					recommendationId, "user-id-1", 123L, "contractorName", "Some message", StatusType.DECLINED
			);
			
			when(recommendationService.editStatusById(testUser.getId(), recommendationId, StatusType.DECLINED))
					.thenReturn(rejectedRecommendation);

			mockMvc.perform(patch("/recommendations/{recommendationId}/reject", recommendationId)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.data.id").value(recommendationId))
					.andExpect(jsonPath("$.data.status").value("DECLINED"))
					.andExpect(jsonPath("$.errors").isEmpty());
			
			verify(recommendationService).editStatusById(testUser.getId(), recommendationId, StatusType.DECLINED);
		}
		
		@Test
		void testRejectRecommendation_NotFound() throws Exception {
			String nonExistingId = "non-existing-id";
			
			when(recommendationService.editStatusById(testUser.getId(), nonExistingId, StatusType.DECLINED))
					.thenThrow(new EntityNotFoundException("Recommendation with ID " + nonExistingId + " not found"));

			mockMvc.perform(patch("/recommendations/{recommendationId}/reject", nonExistingId)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.errors").value("Recommendation with ID " + nonExistingId + " not found"));
			
			verify(recommendationService).editStatusById(testUser.getId(), nonExistingId, StatusType.DECLINED);
		}
		
		@Test
		void testRejectRecommendation_Unauthorized() throws Exception {
			String recommendationId = "rec-id-1";
			
			when(recommendationService.editStatusById(testUser.getId(), recommendationId, StatusType.DECLINED))
					.thenThrow(new AccessDeniedException("You are not allowed to edit this recommendation."));

			mockMvc.perform(patch("/recommendations/{recommendationId}/reject", recommendationId)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isForbidden())
					.andExpect(jsonPath("$.errors").value("You are not allowed to edit this recommendation."));
			
			verify(recommendationService).editStatusById(testUser.getId(), recommendationId, StatusType.DECLINED);
		}
		
		@Test
		void testRejectRecommendation_AlreadyRejected() throws Exception {
			String recommendationId = "already-rejected-id";
			RecommendationResponseDTO alreadyRejectedRecommendation = new RecommendationResponseDTO(
					recommendationId, "user-id-1", 123L, "contractorName", "Already rejected message", StatusType.DECLINED
			);
			
			when(recommendationService.editStatusById(testUser.getId(), recommendationId, StatusType.DECLINED))
					.thenReturn(alreadyRejectedRecommendation);

			mockMvc.perform(patch("/recommendations/{recommendationId}/reject", recommendationId)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.data.id").value(recommendationId))
					.andExpect(jsonPath("$.data.status").value("DECLINED"))
					.andExpect(jsonPath("$.errors").isEmpty());
			
			verify(recommendationService).editStatusById(testUser.getId(), recommendationId, StatusType.DECLINED);
		}
		
		@Test
		void testRejectRecommendation_ServerError() throws Exception {
			String recommendationId = "error-id";
			
			when(recommendationService.editStatusById(testUser.getId(), recommendationId, StatusType.DECLINED))
					.thenThrow(new RuntimeException("Unexpected server error"));

			mockMvc.perform(patch("/recommendations/{recommendationId}/reject", recommendationId)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.errors").value("Unexpected server error"));
			
			verify(recommendationService).editStatusById(testUser.getId(), recommendationId, StatusType.DECLINED);
		}
	}

	@Nested
	class EditRecommendationTests {
		private RecommendationRequestDTO validEditRequest;
		private String recommendationId;
		private Long contractorId;
		@SuppressWarnings("unused")
		private RecommendationResponseDTO originalRecommendation;
		private RecommendationResponseDTO editedRecommendation;

		@BeforeEach
		void setUp() {
			mockMvc = MockMvcBuilders
							.standaloneSetup(recommendationController)
							.setControllerAdvice(new ErrorController())
							.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
							.build();

			recommendationId = "rec-123";
			contractorId = 123L;
			
			originalRecommendation = new RecommendationResponseDTO(
							recommendationId, "talent-id", contractorId, "Contractor Name", 
							"Original message", StatusType.ACCEPTED
			);
			
			validEditRequest = RecommendationRequestDTO.builder()
							.contractorId(contractorId)
							.contractorName("Contractor Name")
							.message("Updated recommendation message")
							.status(StatusType.PENDING)
							.build();
			
			editedRecommendation = new RecommendationResponseDTO(
							recommendationId, "talent-id", contractorId, "Contractor Name", 
							"Updated recommendation message", StatusType.PENDING
			);
		}

		@Test
		void testEditRecommendation_Success() throws Exception {
			when(recommendationService.editById(anyLong(), anyString(), any(RecommendationRequestDTO.class)))
							.thenReturn(editedRecommendation);

			mockMvc.perform(put("/recommendations/{recommendationId}/contractor/{contractorId}", recommendationId, contractorId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(validEditRequest)))
							.andDo(print())
							.andExpect(status().isOk())
							.andExpect(jsonPath("$.data.id").value(recommendationId))
							.andExpect(jsonPath("$.data.message").value("Updated recommendation message"))
							.andExpect(jsonPath("$.data.status").value("PENDING"))
							.andExpect(jsonPath("$.errors").isEmpty());
			
			verify(recommendationService).editById(anyLong(), anyString(), any(RecommendationRequestDTO.class));
		}
		
		@Test
		void testEditRecommendation_RecommendationNotFound() throws Exception {
			when(recommendationService.editById(anyLong(), anyString(), any(RecommendationRequestDTO.class)))
							.thenThrow(new EntityNotFoundException("Recommendation with ID " + recommendationId + " not found"));

			mockMvc.perform(put("/recommendations/{recommendationId}/contractor/{contractorId}", recommendationId, contractorId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(validEditRequest)))
							.andExpect(status().isNotFound())
							.andExpect(jsonPath("$.errors").value("Recommendation with ID " + recommendationId + " not found"));
			
			verify(recommendationService).editById(anyLong(), anyString(), any(RecommendationRequestDTO.class));
		}
		
		@Test
		void testEditRecommendation_Unauthorized() throws Exception {
			when(recommendationService.editById(anyLong(), anyString(), any(RecommendationRequestDTO.class)))
							.thenThrow(new AccessDeniedException("Only the contractor who created this recommendation can edit it"));

			mockMvc.perform(put("/recommendations/{recommendationId}/contractor/{contractorId}", recommendationId, contractorId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(validEditRequest)))
							.andExpect(status().isForbidden())
							.andExpect(jsonPath("$.errors").value("Only the contractor who created this recommendation can edit it"));
			
			verify(recommendationService).editById(anyLong(), anyString(), any(RecommendationRequestDTO.class));
		}
		
		@Test
		void testEditRecommendation_InvalidRequest() throws Exception {
			RecommendationRequestDTO invalidRequest = new RecommendationRequestDTO();
			
			mockMvc.perform(put("/recommendations/{recommendationId}/contractor/{contractorId}", recommendationId, contractorId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(invalidRequest)))
							.andExpect(status().isBadRequest());
			
			verify(recommendationService, never()).editById(any(), any(), any(RecommendationRequestDTO.class));
		}
		
		@Test
		void testEditRecommendation_KeepCurrentStatus() throws Exception {
			RecommendationRequestDTO requestWithCurrentStatus = RecommendationRequestDTO.builder()
							.contractorId(contractorId)
							.contractorName("Contractor Name")
							.message("Updated message only")
							.status(StatusType.ACCEPTED) // Keeping the ACCEPTED status
							.build();
			
			RecommendationResponseDTO responseWithStatusUnchanged = new RecommendationResponseDTO(
							recommendationId, "talent-id", contractorId, "Contractor Name", 
							"Updated message only", StatusType.ACCEPTED // Status remains ACCEPTED
			);
			
			when(recommendationService.editById(anyLong(), anyString(), any(RecommendationRequestDTO.class)))
							.thenReturn(responseWithStatusUnchanged);

			mockMvc.perform(put("/recommendations/{recommendationId}/contractor/{contractorId}", recommendationId, contractorId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(requestWithCurrentStatus)))
							.andExpect(status().isOk())
							.andExpect(jsonPath("$.data.id").value(recommendationId))
							.andExpect(jsonPath("$.data.message").value("Updated message only"))
							.andExpect(jsonPath("$.data.status").value("ACCEPTED")) // Status remains unchanged
							.andExpect(jsonPath("$.errors").isEmpty());
			
			verify(recommendationService).editById(anyLong(), anyString(), any(RecommendationRequestDTO.class));
		}
		
		@Test
		void testEditRecommendation_ServerError() throws Exception {
			when(recommendationService.editById(anyLong(), anyString(), any(RecommendationRequestDTO.class)))
							.thenThrow(new RuntimeException("Unexpected server error"));

			mockMvc.perform(put("/recommendations/{recommendationId}/contractor/{contractorId}", recommendationId, contractorId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(validEditRequest)))
							.andExpect(status().isInternalServerError())
							.andExpect(jsonPath("$.errors").value("Unexpected server error"));
			
			verify(recommendationService).editById(anyLong(), anyString(), any(RecommendationRequestDTO.class));
		}
	}

        @Nested
        class ReadRecommendationTests {
            private User testUser;
            private RecommendationResponseDTO recommendation1;
            private RecommendationResponseDTO recommendation2;
            private RecommendationResponseDTO recommendation3;
            private Map<StatusType, List<RecommendationResponseDTO>> groupedRecommendations;

            @BeforeEach
            void setUp() {
                testUser = new User();
                testUser.setId("user-id-1");
                testUser.setEmail("john.doe@email.com");

                mockMvc = MockMvcBuilders
                        .standaloneSetup(recommendationController)
                        .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(testUser))
                        .build();

                setUpRecommendations();
            }

            private void setUpRecommendations() {
                recommendation1 = new RecommendationResponseDTO();
                recommendation1.setId("rec-id-1");
                recommendation1.setTalentId("user-id-1");
                recommendation1.setContractorId(101L);
                recommendation1.setContractorName("Contractor A");
                recommendation1.setMessage("Excellent performance on project X");
                recommendation1.setStatus(StatusType.PENDING);

                recommendation2 = new RecommendationResponseDTO();
                recommendation2.setId("rec-id-2");
                recommendation2.setTalentId("user-id-1");
                recommendation2.setContractorId(102L);
                recommendation2.setContractorName("Contractor B");
                recommendation2.setMessage("Great collaboration on project Y");
                recommendation2.setStatus(StatusType.ACCEPTED);

                recommendation3 = new RecommendationResponseDTO();
                recommendation3.setId("rec-id-3");
                recommendation3.setTalentId("user-id-2");
                recommendation3.setContractorId(103L);
                recommendation3.setContractorName("Contractor C");
                recommendation3.setMessage("Good technical skills");
                recommendation3.setStatus(StatusType.DECLINED);

                groupedRecommendations = new EnumMap<>(StatusType.class);
                groupedRecommendations.put(StatusType.PENDING, Collections.singletonList(recommendation1));
                groupedRecommendations.put(StatusType.ACCEPTED, Collections.singletonList(recommendation2));
                groupedRecommendations.put(StatusType.DECLINED, Collections.singletonList(recommendation3));
            }

            @Test
            void getRecommendationById_ExistingId_ReturnsRecommendation() throws Exception {
                String id = "rec-id-1";
                when(recommendationService.getById(id)).thenReturn(recommendation1);

                mockMvc.perform(get("/recommendations/{recommendationId}", id))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data.id", is(recommendation1.getId())))
                        .andExpect(jsonPath("$.data.talentId", is(recommendation1.getTalentId())))
                        .andExpect(jsonPath("$.data.contractorName", is(recommendation1.getContractorName())))
                        .andExpect(jsonPath("$.data.message", is(recommendation1.getMessage())))
                        .andExpect(jsonPath("$.data.status", is(recommendation1.getStatus().toString())));

                verify(recommendationService).getById(id);
            }

            @Test
            void getRecommendationsByTalentId_ExistingTalentId_ReturnsFilteredRecommendations() throws Exception {
                String talentId = "user-id-1";
                List<RecommendationResponseDTO> talent1Recommendations = Arrays.asList(recommendation1, recommendation2);
                when(recommendationService.getByTalentId(talentId)).thenReturn(talent1Recommendations);

                mockMvc.perform(get("/recommendations/user/{userId}", talentId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data", hasSize(2)))
                        .andExpect(jsonPath("$.data[0].id", is(recommendation1.getId())))
                        .andExpect(jsonPath("$.data[1].id", is(recommendation2.getId())))
                        .andExpect(jsonPath("$.data[0].talentId", is(talentId)))
                        .andExpect(jsonPath("$.data[1].talentId", is(talentId)));

                verify(recommendationService).getByTalentId(talentId);
            }

            @Test
            void getRecommendationsByTalentIdAndStatus_ExistingCombination_ReturnsFilteredRecommendations() throws Exception {
                String talentId = "user-id-1";
                StatusType status = StatusType.PENDING;
                when(recommendationService.getByTalentIdAndStatus(talentId, status))
                        .thenReturn(Collections.singletonList(recommendation1));

                mockMvc.perform(get("/recommendations/user/{userId}/status/{status}", talentId, status))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data", hasSize(1)))
                        .andExpect(jsonPath("$.data[0].id", is(recommendation1.getId())))
                        .andExpect(jsonPath("$.data[0].talentId", is(talentId)))
                        .andExpect(jsonPath("$.data[0].status", is(status.toString())));

                verify(recommendationService).getByTalentIdAndStatus(talentId, status);
            }

            @Test
            void getRecommendationsByTalentIdGroupedByStatus_ExistingTalent_ReturnsGroupedRecommendations() throws Exception {
                String talentId = "user-id-1";
                Map<StatusType, List<RecommendationResponseDTO>> groupedByTalent = new EnumMap<>(StatusType.class);
                groupedByTalent.put(StatusType.PENDING, Collections.singletonList(recommendation1));
                groupedByTalent.put(StatusType.ACCEPTED, Collections.singletonList(recommendation2));
                groupedByTalent.put(StatusType.DECLINED, Collections.emptyList());

                when(recommendationService.getByTalentIdAndGroupedByStatus(talentId)).thenReturn(groupedByTalent);

                mockMvc.perform(get("/recommendations/user/{userId}/grouped-by-status", talentId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data." + StatusType.PENDING, hasSize(1)))
                        .andExpect(jsonPath("$.data." + StatusType.ACCEPTED, hasSize(1)))
                        .andExpect(jsonPath("$.data." + StatusType.PENDING + "[0].id", is(recommendation1.getId())))
                        .andExpect(jsonPath("$.data." + StatusType.ACCEPTED + "[0].id", is(recommendation2.getId())));

                verify(recommendationService).getByTalentIdAndGroupedByStatus(talentId);
            }
    
            @Test
            void getRecommendationById_NotFound_ReturnsErrorResponse() throws Exception {
                String nonExistentId = "non-existent-id";
                when(recommendationService.getById(nonExistentId)).thenThrow(new IllegalArgumentException("Recommendation not found"));

                mockMvc.perform(get("/recommendations/{recommendationId}", nonExistentId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("Recommendation not found")));

                verify(recommendationService).getById(nonExistentId);
            }

            @Test
            void getRecommendationsByTalentId_EmptyResults_ReturnsNotFoundResponse() throws Exception {
                String talentId = "user-with-no-recommendations";
                when(recommendationService.getByTalentId(talentId)).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/recommendations/user/{userId}", talentId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("No recommendations found")));

                verify(recommendationService).getByTalentId(talentId);
            }

            @Test
            void getRecommendationsByTalentIdAndStatus_EmptyResults_ReturnsNotFoundResponse() throws Exception {
                String talentId = "user-id-1";
                StatusType status = StatusType.DECLINED;
                when(recommendationService.getByTalentIdAndStatus(talentId, status))
                        .thenReturn(Collections.emptyList());

                mockMvc.perform(get("/recommendations/user/{userId}/status/{status}", talentId, status))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("No recommendations found")));

                verify(recommendationService).getByTalentIdAndStatus(talentId, status);
            }

            @Test
            void getRecommendationsByTalentIdGroupedByStatus_EmptyResults_ReturnsNotFoundResponse() throws Exception {
                String talentId = "user-with-no-recommendations";
                Map<StatusType, List<RecommendationResponseDTO>> emptyMap = Collections.emptyMap();
                when(recommendationService.getByTalentIdAndGroupedByStatus(talentId)).thenReturn(emptyMap);

                mockMvc.perform(get("/recommendations/user/{userId}/grouped-by-status", talentId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("No recommendations found")));

                verify(recommendationService).getByTalentIdAndGroupedByStatus(talentId);
            }

            @Test
            void getRecommendationById_UnauthorizedUser_ReturnsUnauthorizedResponse() throws Exception {
                setupUnauthorizedMockMvc();

                mockMvc.perform(get("/recommendations/{recommendationId}", "rec-id-1"))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("Unauthorized access")));

                verify(recommendationService, never()).getById(anyString());
            }

            @Test
            void getRecommendationsByTalentId_UnauthorizedUser_ReturnsUnauthorizedResponse() throws Exception {
                setupUnauthorizedMockMvc();

                mockMvc.perform(get("/recommendations/user/{userId}", "user-id-1"))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("Unauthorized access")));

                verify(recommendationService, never()).getByTalentId(anyString());
            }

            @Test
            void getRecommendationsByTalentIdAndStatus_UnauthorizedUser_ReturnsUnauthorizedResponse() throws Exception {
                setupUnauthorizedMockMvc();

                mockMvc.perform(get("/recommendations/user/{userId}/status/{status}", "user-id-1", StatusType.PENDING))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("Unauthorized access")));

                verify(recommendationService, never()).getByTalentIdAndStatus(anyString(), org.mockito.ArgumentMatchers.any(StatusType.class));
            }

            @Test
            void getRecommendationById_ServiceThrowsException_ReturnsNotFoundResponse() throws Exception {
                String id = "rec-id-1";
                when(recommendationService.getById(id)).thenThrow(new RuntimeException("Some unexpected error"));

                mockMvc.perform(get("/recommendations/{recommendationId}", id))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("Some unexpected error")));

                verify(recommendationService).getById(id);
            }

            @Test
            void getRecommendationsByTalentId_ServiceThrowsException_ReturnsNotFoundResponse() throws Exception {
                String talentId = "user-id-1";
                when(recommendationService.getByTalentId(talentId)).thenThrow(new RuntimeException("Permission denied"));

                mockMvc.perform(get("/recommendations/user/{userId}", talentId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("Permission denied")));

                verify(recommendationService).getByTalentId(talentId);
            }

            @Test
            void getRecommendationsByTalentIdAndStatus_ServiceThrowsException_ReturnsNotFoundResponse() throws Exception {
                String talentId = "user-id-1";
                StatusType status = StatusType.PENDING;
                when(recommendationService.getByTalentIdAndStatus(talentId, status))
                        .thenThrow(new RuntimeException("Error retrieving recommendations"));

                mockMvc.perform(get("/recommendations/user/{userId}/status/{status}", talentId, status))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("Error retrieving recommendations")));

                verify(recommendationService).getByTalentIdAndStatus(talentId, status);
            }

            @Test
            void getRecommendationsByTalentIdGroupedByStatus_ServiceThrowsException_ReturnsNotFoundResponse() throws Exception {
                String talentId = "user-id-1";
                when(recommendationService.getByTalentIdAndGroupedByStatus(talentId))
                        .thenThrow(new RuntimeException("Error grouping recommendations"));

                mockMvc.perform(get("/recommendations/user/{userId}/grouped-by-status", talentId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("Error grouping recommendations")));

                verify(recommendationService).getByTalentIdAndGroupedByStatus(talentId);
            }

            @Test
            void getRecommendationsByTalentIdGroupedByStatus_UnauthorizedUser_ReturnsUnauthorizedResponse() throws Exception {
                setupUnauthorizedMockMvc();

                mockMvc.perform(get("/recommendations/user/{userId}/grouped-by-status", "user-id-1"))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("Unauthorized access")));

                verify(recommendationService, never()).getByTalentIdAndGroupedByStatus(anyString());
            }
            
            @Test
            void getRecommendationTalentFromContractorById_ExistingContractor_ReturnsRecommendations() throws Exception {
                String contractorId = "contractor-id-1";
                List<RecommendationResponseDTO> contractorRecommendations = Arrays.asList(recommendation1, recommendation2);
                
                when(recommendationService.getByTalentId(contractorId)).thenReturn(contractorRecommendations);

                mockMvc.perform(get("/recommendations/user/contractor/{userId}", contractorId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.data", hasSize(2)))
                        .andExpect(jsonPath("$.data[0].id", is(recommendation1.getId())))
                        .andExpect(jsonPath("$.data[1].id", is(recommendation2.getId())));

                verify(recommendationService).getByTalentId(contractorId);
            }
            
            @Test
            void getRecommendationTalentFromContractorById_NoRecommendations_ReturnsNotFound() throws Exception {
                String talentId = "contractor-with-no-recommendations";
                
                when(recommendationService.getByTalentId(talentId)).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/recommendations/user/contractor/{userId}", talentId))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.errors", containsString("No recommendations found for user with id: " + talentId)));

                verify(recommendationService).getByTalentId(talentId);
            }
        }
}



