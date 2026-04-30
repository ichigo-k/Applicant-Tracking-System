package com.kephas.applicanttrackingsystem.dtos.application;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for submitting a job application")
public class ApplicationCreateRequest {

    @NotBlank(message = "Candidate name is required")
    @Schema(description = "Full name of the candidate", example = "Jane Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Candidate email address", example = "jane.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
