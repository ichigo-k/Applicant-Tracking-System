package com.kephas.applicanttrackingsystem.dtos.application;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Payload for adding a note to an application")
public class ApplicationNoteRequest {

    @NotNull(message = "Note type is required")
    @NotBlank(message = "Note type is required")
    @Schema(description = "Type of note (e.g. GENERAL, INTERVIEW, REJECTION)", example = "INTERVIEW", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @NotBlank(message = "Description is required")
    @Schema(description = "Note content", example = "Candidate showed strong system design skills.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;
}
