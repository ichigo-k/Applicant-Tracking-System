package com.kephas.applicanttrackingsystem.dtos.application;

import com.kephas.applicanttrackingsystem.enums.NoteType;
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
@Schema(description = "A note attached to an application")
public class ApplicationNoteResponse {

    @Schema(description = "Unique note identifier")
    private UUID id;

    @Schema(description = "Type of note")
    private NoteType type;

    @Schema(description = "Note content", example = "Candidate showed strong system design skills.")
    private String description;

    @Schema(description = "Name of the team member who wrote the note", example = "Alice Smith")
    private String authorName;

    @Schema(description = "Timestamp when the note was created")
    private LocalDateTime createdAt;
}
