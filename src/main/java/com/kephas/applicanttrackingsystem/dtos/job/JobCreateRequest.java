package com.kephas.applicanttrackingsystem.dtos.job;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating a new job posting")
public class JobCreateRequest {

    @NotBlank(message = "Title is required")
    @Schema(description = "Job title", example = "Senior Backend Engineer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Full job description", example = "We are looking for a backend engineer...")
    private String description;

    @Schema(description = "Job location or 'Remote'", example = "New York, NY")
    private String location;
}
