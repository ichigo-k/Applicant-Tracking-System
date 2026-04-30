package com.kephas.applicanttrackingsystem.dtos.application;

import com.kephas.applicanttrackingsystem.dtos.score.ScoreResponse;
import com.kephas.applicanttrackingsystem.dtos.stage.StageHistoryResponse;
import com.kephas.applicanttrackingsystem.enums.ApplicationStage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Full application details including notes, stage history and scores")
public class ApplicationFullResponse {

    @Schema(description = "Unique application identifier")
    private UUID id;

    @Schema(description = "ID of the job this application belongs to")
    private UUID jobId;

    @Schema(description = "Full name of the candidate", example = "Jane Doe")
    private String candidateName;

    @Schema(description = "Email address of the candidate", example = "jane.doe@example.com")
    private String candidateEmail;

    @Schema(description = "Current stage of the application")
    private ApplicationStage currentStage;

    @Schema(description = "Timestamp when the application was submitted")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp of the last update")
    private LocalDateTime updatedAt;

    @Schema(description = "All notes added to this application")
    private List<ApplicationNoteResponse> notes;

    @Schema(description = "History of stage transitions")
    private List<StageHistoryResponse> stageHistory;

    @Schema(description = "Scores submitted by team members")
    private List<ScoreResponse> scores;
}
