package com.kephas.applicanttrackingsystem.dtos.stage;

import com.kephas.applicanttrackingsystem.enums.ApplicationStage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A single stage transition in an application's history")
public class StageHistoryResponse {

    @Schema(description = "Unique history entry identifier")
    private UUID id;

    @Schema(description = "Stage the application moved from")
    private ApplicationStage fromStage;

    @Schema(description = "Stage the application moved to")
    private ApplicationStage toStage;

    @Schema(description = "Name of the team member who made the change", example = "Bob Jones")
    private String changedByName;

    @Schema(description = "Timestamp of the stage change")
    private LocalDateTime changedAt;

    @Schema(description = "Reason provided for the stage change", example = "Passed the technical screening")
    private String reason;
}
