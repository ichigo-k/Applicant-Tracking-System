package com.kephas.applicanttrackingsystem.controllers;

import com.kephas.applicanttrackingsystem.dtos.application.*;
import com.kephas.applicanttrackingsystem.dtos.score.ScoreRequest;
import com.kephas.applicanttrackingsystem.dtos.score.ScoreResponse;
import com.kephas.applicanttrackingsystem.dtos.stage.StageUpdateRequest;
import com.kephas.applicanttrackingsystem.enums.ScoreType;
import com.kephas.applicanttrackingsystem.services.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Manage job applications, notes, stage transitions and scores")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(
            summary = "Submit an application for a job",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Application created",
                            content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
            }
    )
    @PostMapping("/jobs/{jobId}/applications")
    public ResponseEntity<ApplicationResponse> createApplication(
            @Parameter(description = "Job UUID") @PathVariable UUID jobId,
            @Valid @RequestBody ApplicationCreateRequest request) {

        ApplicationResponse response = applicationService.createApplication(jobId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "List applications for a job",
            description = "Returns all applications for the given job, optionally filtered by stage",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of applications"),
                    @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
            }
    )
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsForJob(
            @Parameter(description = "Job UUID") @PathVariable UUID jobId,
            @Parameter(description = "Filter by application stage") @RequestParam(required = false) String stage) {

        List<ApplicationResponse> applications = applicationService.getApplicationsForJob(jobId, stage);
        return ResponseEntity.ok(applications);
    }

    @Operation(
            summary = "Get full application details",
            description = "Returns the application along with its notes, stage history and scores",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Application found",
                            content = @Content(schema = @Schema(implementation = ApplicationFullResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Application not found", content = @Content)
            }
    )
    @GetMapping("/applications/{id}")
    public ResponseEntity<ApplicationFullResponse> getApplicationById(
            @Parameter(description = "Application UUID") @PathVariable UUID id) {

        ApplicationFullResponse response = applicationService.getApplicationById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Move application to a new stage",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stage updated",
                            content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid stage transition", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Application or team member not found", content = @Content)
            }
    )
    @PatchMapping("/applications/{id}/stage")
    public ResponseEntity<ApplicationResponse> updateApplicationStage(
            @Parameter(description = "Application UUID") @PathVariable UUID id,
            @Parameter(description = "Team member performing the action") @RequestHeader("X-Team-Member-Id") UUID teamMemberId,
            @Valid @RequestBody StageUpdateRequest request) throws BadRequestException {

        ApplicationResponse response = applicationService.updateApplicationStage(id, teamMemberId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Add a note to an application",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Note added",
                            content = @Content(schema = @Schema(implementation = ApplicationNoteResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Application or team member not found", content = @Content)
            }
    )
    @PostMapping("/applications/{id}/notes")
    public ResponseEntity<ApplicationNoteResponse> addNote(
            @Parameter(description = "Application UUID") @PathVariable UUID id,
            @Parameter(description = "Team member adding the note") @RequestHeader("X-Team-Member-Id") UUID teamMemberId,
            @Valid @RequestBody ApplicationNoteRequest request) {

        ApplicationNoteResponse response = applicationService.addNote(id, teamMemberId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all notes for an application",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of notes"),
                    @ApiResponse(responseCode = "404", description = "Application not found", content = @Content)
            }
    )
    @GetMapping("/applications/{id}/notes")
    public ResponseEntity<List<ApplicationNoteResponse>> getApplicationNotes(
            @Parameter(description = "Application UUID") @PathVariable UUID id) {

        List<ApplicationNoteResponse> notes = applicationService.getApplicationNotes(id);
        return ResponseEntity.ok(notes);
    }

    @Operation(
            summary = "Submit or update a culture-fit score",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Score saved",
                            content = @Content(schema = @Schema(implementation = ScoreResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Application or team member not found", content = @Content)
            }
    )
    @PutMapping("/applications/{id}/scores/culture-fit")
    public ResponseEntity<ScoreResponse> cultureFit(
            @Parameter(description = "Application UUID") @PathVariable UUID id,
            @Parameter(description = "Team member submitting the score") @RequestHeader("X-Team-Member-Id") UUID teamMemberId,
            @RequestBody @Valid ScoreRequest request) {

        ScoreResponse scoreResponse = applicationService.upsertScore(id, teamMemberId, ScoreType.CULTURE_FIT, request);
        return ResponseEntity.ok(scoreResponse);
    }

    @Operation(
            summary = "Submit or update an interview score",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Score saved",
                            content = @Content(schema = @Schema(implementation = ScoreResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Application or team member not found", content = @Content)
            }
    )
    @PutMapping("/applications/{id}/scores/interview")
    public ResponseEntity<ScoreResponse> interview(
            @Parameter(description = "Application UUID") @PathVariable UUID id,
            @Parameter(description = "Team member submitting the score") @RequestHeader("X-Team-Member-Id") UUID teamMemberId,
            @RequestBody @Valid ScoreRequest request) {

        ScoreResponse scoreResponse = applicationService.upsertScore(id, teamMemberId, ScoreType.INTERVIEW, request);
        return ResponseEntity.ok(scoreResponse);
    }

    @Operation(
            summary = "Submit or update an assessment score",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Score saved",
                            content = @Content(schema = @Schema(implementation = ScoreResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Application or team member not found", content = @Content)
            }
    )
    @PutMapping("/applications/{id}/scores/assessment")
    public ResponseEntity<ScoreResponse> assessment(
            @Parameter(description = "Application UUID") @PathVariable UUID id,
            @Parameter(description = "Team member submitting the score") @RequestHeader("X-Team-Member-Id") UUID teamMemberId,
            @RequestBody @Valid ScoreRequest request) {

        ScoreResponse scoreResponse = applicationService.upsertScore(id, teamMemberId, ScoreType.ASSESSMENT, request);
        return ResponseEntity.ok(scoreResponse);
    }
}
