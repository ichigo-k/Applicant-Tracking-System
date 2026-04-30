package com.kephas.applicanttrackingsystem.dtos.job;

import com.kephas.applicanttrackingsystem.enums.JobStatus;
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
@Schema(description = "Job posting details")
public class JobResponse {

    @Schema(description = "Unique job identifier")
    private UUID id;

    @Schema(description = "Job title", example = "Senior Backend Engineer")
    private String title;

    @Schema(description = "Full job description")
    private String description;

    @Schema(description = "Job location", example = "New York, NY")
    private String location;

    @Schema(description = "Current status of the job posting")
    private JobStatus status;

    @Schema(description = "Timestamp when the job was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp of the last update")
    private LocalDateTime updatedAt;
}
