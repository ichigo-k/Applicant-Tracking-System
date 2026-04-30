package com.kephas.applicanttrackingsystem.dtos.stage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Payload for moving an application to a new stage")
public class StageUpdateRequest {

    @NotNull
    @NotBlank
    @Schema(description = "Target stage name", example = "INTERVIEW", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newStage;

    @Schema(description = "Optional reason for the stage change", example = "Passed the technical screening")
    private String reason;
}
