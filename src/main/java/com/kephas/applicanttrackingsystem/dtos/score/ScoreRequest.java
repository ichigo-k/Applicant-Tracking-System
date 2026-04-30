package com.kephas.applicanttrackingsystem.dtos.score;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for submitting or updating a score")
public class ScoreRequest {

    @NotNull
    @Min(1) @Max(5)
    @Schema(description = "Score value between 1 and 5", example = "4", minimum = "1", maximum = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer score;

    @NotNull
    @NotBlank
    @Schema(description = "Justification for the score", example = "Strong communication and technical depth.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String comment;
}
