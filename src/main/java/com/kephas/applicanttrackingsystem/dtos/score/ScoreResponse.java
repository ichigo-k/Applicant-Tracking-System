package com.kephas.applicanttrackingsystem.dtos.score;

import com.kephas.applicanttrackingsystem.enums.ScoreType;
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
@Schema(description = "A score submitted by a team member for an application")
public class ScoreResponse {

    @Schema(description = "Unique score identifier")
    private UUID id;

    @Schema(description = "Type of score")
    private ScoreType type;

    @Schema(description = "Score value (1–5)", example = "4")
    private Integer score;

    @Schema(description = "Justification for the score", example = "Strong communication and technical depth.")
    private String comment;

    @Schema(description = "Name of the team member who submitted the score", example = "Alice Smith")
    private String updatedByName;

    @Schema(description = "Timestamp of the last score update")
    private LocalDateTime updatedAt;
}
