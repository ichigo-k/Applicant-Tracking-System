package com.kephas.applicanttrackingsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kephas.applicanttrackingsystem.dtos.job.JobCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@ActiveProfiles("test")
class JobIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void shouldCreateJobAndReadItBack() throws Exception {
        JobCreateRequest request = new JobCreateRequest();
        request.setTitle("Senior Backend Engineer");
        request.setDescription("Build scalable APIs");
        request.setLocation("Remote");

        MvcResult result = mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Senior Backend Engineer"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn();

        String jobId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asText();

        mockMvc.perform(get("/api/jobs/{id}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jobId))
                .andExpect(jsonPath("$.location").value("Remote"));
    }

    @Test
    void shouldReturnJobsListWithPagination() throws Exception {
        JobCreateRequest request = new JobCreateRequest();
        request.setTitle("Frontend Developer");
        request.setLocation("New York");

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/jobs")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].id").exists());
    }

    @Test
    void shouldFilterJobsByStatus() throws Exception {
        JobCreateRequest request = new JobCreateRequest();
        request.setTitle("DevOps Engineer");
        request.setLocation("Austin");

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/jobs").param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].status", everyItem(is("OPEN"))));
    }

    @Test
    void shouldReturn404ForNonExistentJob() throws Exception {
        mockMvc.perform(get("/api/jobs/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenTitleIsMissing() throws Exception {
        JobCreateRequest request = new JobCreateRequest();
        request.setLocation("Remote");

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.title").exists());
    }
}