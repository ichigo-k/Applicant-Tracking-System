package com.kephas.applicanttrackingsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kephas.applicanttrackingsystem.dtos.application.ApplicationCreateRequest;
import com.kephas.applicanttrackingsystem.dtos.application.ApplicationNoteRequest;
import com.kephas.applicanttrackingsystem.dtos.score.ScoreRequest;
import com.kephas.applicanttrackingsystem.entities.Job;
import com.kephas.applicanttrackingsystem.entities.TeamMember;
import com.kephas.applicanttrackingsystem.enums.JobStatus;
import com.kephas.applicanttrackingsystem.enums.MemberRole;
import com.kephas.applicanttrackingsystem.repositories.JobRepository;
import com.kephas.applicanttrackingsystem.repositories.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UUID jobId;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        Job job = new Job();
        job.setTitle("Backend Engineer");
        job.setDescription("Build APIs");
        job.setLocation("Remote");
        job.setStatus(JobStatus.OPEN);
        jobId = jobRepository.save(job).getId();

        TeamMember member = new TeamMember();
        member.setName("Alice Smith");
        member.setEmail("alice" + UUID.randomUUID() + "@company.com");
        member.setRole(MemberRole.RECRUITER);
        memberId = teamMemberRepository.save(member).getId();
    }

    @Test
    void shouldCreateApplicationAndReadItBack() throws Exception {
        ApplicationCreateRequest request = new ApplicationCreateRequest();
        request.setName("Jane Doe");
        request.setEmail("jane@example.com");

        MvcResult result = mockMvc.perform(post("/api/jobs/{jobId}/applications", jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.candidateName").value("Jane Doe"))
                .andExpect(jsonPath("$.currentStage").value("APPLIED"))
                .andReturn();

        String appId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asText();

        mockMvc.perform(get("/api/applications/{id}", appId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.candidateEmail").value("jane@example.com"))
                .andExpect(jsonPath("$.currentStage").value("APPLIED"));
    }

    @Test
    void shouldRejectDuplicateApplication() throws Exception {
        ApplicationCreateRequest request = new ApplicationCreateRequest();
        request.setName("Sara Lee");
        request.setEmail("sara@example.com");

        mockMvc.perform(post("/api/jobs/{jobId}/applications", jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/jobs/{jobId}/applications", jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldAddNoteAndConfirmAuthorNameIsResolved() throws Exception {
        ApplicationCreateRequest appRequest = new ApplicationCreateRequest();
        appRequest.setName("Tom Brown");
        appRequest.setEmail("tom@example.com");

        MvcResult appResult = mockMvc.perform(post("/api/jobs/{jobId}/applications", jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String appId = objectMapper.readTree(appResult.getResponse().getContentAsString())
                .get("id").asText();

        ApplicationNoteRequest noteRequest = new ApplicationNoteRequest();
        noteRequest.setType("GENERAL");
        noteRequest.setDescription("Strong candidate, good communication.");

        mockMvc.perform(post("/api/applications/{id}/notes", appId)
                        .header("X-Team-Member-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.authorName").value("Alice Smith"));

        mockMvc.perform(get("/api/applications/{id}/notes", appId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorName").value("Alice Smith"))
                .andExpect(jsonPath("$[0].description").value("Strong candidate, good communication."));
    }

    @Test
    void shouldMoveStageAndRecordHistory() throws Exception {
        ApplicationCreateRequest appRequest = new ApplicationCreateRequest();
        appRequest.setName("Mark Taylor");
        appRequest.setEmail("mark@example.com");

        MvcResult appResult = mockMvc.perform(post("/api/jobs/{jobId}/applications", jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String appId = objectMapper.readTree(appResult.getResponse().getContentAsString())
                .get("id").asText();

        String stageBody = "{ \"newStage\": \"SCREENING\", \"reason\": \"Passed initial review\" }";

        mockMvc.perform(patch("/api/applications/{id}/stage", appId)
                        .header("X-Team-Member-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stageBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStage").value("SCREENING"));

        mockMvc.perform(get("/api/applications/{id}", appId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageHistory", hasSize(1)))
                .andExpect(jsonPath("$.stageHistory[0].fromStage").value("APPLIED"))
                .andExpect(jsonPath("$.stageHistory[0].toStage").value("SCREENING"))
                .andExpect(jsonPath("$.stageHistory[0].changedByName").value("Alice Smith"));
    }

    @Test
    void shouldRejectInvalidStageTransition() throws Exception {
        ApplicationCreateRequest appRequest = new ApplicationCreateRequest();
        appRequest.setName("Nina Patel");
        appRequest.setEmail("nina@example.com");

        MvcResult appResult = mockMvc.perform(post("/api/jobs/{jobId}/applications", jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String appId = objectMapper.readTree(appResult.getResponse().getContentAsString())
                .get("id").asText();

        mockMvc.perform(patch("/api/applications/{id}/stage", appId)
                        .header("X-Team-Member-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"newStage\": \"HIRED\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSubmitScoreAndSecondSubmissionWins() throws Exception {
        ApplicationCreateRequest appRequest = new ApplicationCreateRequest();
        appRequest.setName("Chris Evans");
        appRequest.setEmail("chris@example.com");

        MvcResult appResult = mockMvc.perform(post("/api/jobs/{jobId}/applications", jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String appId = objectMapper.readTree(appResult.getResponse().getContentAsString())
                .get("id").asText();

        ScoreRequest first = new ScoreRequest();
        first.setScore(2);
        first.setComment("Needs improvement");

        mockMvc.perform(put("/api/applications/{id}/scores/interview", appId)
                        .header("X-Team-Member-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(2));

        ScoreRequest second = new ScoreRequest();
        second.setScore(5);
        second.setComment("Much better on second look");

        mockMvc.perform(put("/api/applications/{id}/scores/interview", appId)
                        .header("X-Team-Member-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(5))
                .andExpect(jsonPath("$.updatedByName").value("Alice Smith"));
    }
}
