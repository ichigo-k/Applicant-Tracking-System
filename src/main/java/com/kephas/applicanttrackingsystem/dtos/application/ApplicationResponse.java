package com.kephas.applicanttrackingsystem.dtos.application;

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
@Schema(description = "Summary of a job application")
public class ApplicationResponse {

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
}
