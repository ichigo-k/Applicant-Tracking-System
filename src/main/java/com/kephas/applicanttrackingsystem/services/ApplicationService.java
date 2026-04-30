package com.kephas.applicanttrackingsystem.services;

import com.kephas.applicanttrackingsystem.dtos.application.*;
import com.kephas.applicanttrackingsystem.dtos.score.ScoreRequest;
import com.kephas.applicanttrackingsystem.dtos.score.ScoreResponse;
import com.kephas.applicanttrackingsystem.dtos.stage.StageUpdateRequest;
import com.kephas.applicanttrackingsystem.enums.ScoreType;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ApplicationService {

    ApplicationResponse createApplication(UUID jobId, ApplicationCreateRequest request);

    List<ApplicationResponse> getApplicationsForJob(UUID jobId, String stage);

    ApplicationFullResponse getApplicationById(UUID id);

    ApplicationResponse updateApplicationStage(
            UUID id,
            UUID teamMemberId,
            StageUpdateRequest request
    ) throws BadRequestException;

    ApplicationNoteResponse addNote(
            UUID id,
            UUID teamMemberId,
            ApplicationNoteRequest request
    );

    List<ApplicationNoteResponse> getApplicationNotes(UUID id);

    ScoreResponse upsertScore
            (UUID id,
             UUID teamMemberId,
             ScoreType scoreType,
             @Valid ScoreRequest request
            );
}