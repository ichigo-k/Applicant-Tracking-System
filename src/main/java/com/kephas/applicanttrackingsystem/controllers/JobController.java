package com.kephas.applicanttrackingsystem.controllers;

import com.kephas.applicanttrackingsystem.dtos.job.JobCreateRequest;
import com.kephas.applicanttrackingsystem.dtos.job.JobResponse;
import com.kephas.applicanttrackingsystem.services.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Create and retrieve job postings")
public class JobController {

    private final JobService jobService;

    @Operation(
            summary = "Create a job posting",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Job created",
                            content = @Content(schema = @Schema(implementation = JobResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody JobCreateRequest request) {

        JobResponse createdJob = jobService.createJob(request);
        return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
    }

    @Operation(
            summary = "List all jobs",
            description = "Returns a paginated list of jobs, optionally filtered by status (OPEN, CLOSED, DRAFT)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of jobs")
            }
    )
    @GetMapping
    public ResponseEntity<Page<JobResponse>> getAllJobs(
            @Parameter(description = "Filter by job status: OPEN, CLOSED, DRAFT")
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<JobResponse> jobs = jobService.getAllJobs(status, pageable);
        return ResponseEntity.ok(jobs);
    }

    @Operation(
            summary = "Get a job by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Job found",
                            content = @Content(schema = @Schema(implementation = JobResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(
            @Parameter(description = "Job UUID") @PathVariable UUID id) {

        JobResponse job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }
}
