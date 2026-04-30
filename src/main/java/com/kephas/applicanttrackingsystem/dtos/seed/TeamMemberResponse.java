package com.kephas.applicanttrackingsystem.dtos.seed;

import com.kephas.applicanttrackingsystem.enums.MemberRole;
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
@Schema(description = "A team member")
public class TeamMemberResponse {

    @Schema(description = "Unique team member identifier")
    private UUID id;

    @Schema(description = "Full name", example = "Alice Smith")
    private String name;

    @Schema(description = "Email address", example = "alice@example.com")
    private String email;

    @Schema(description = "Role in the hiring process")
    private MemberRole role;

    @Schema(description = "Timestamp when the member was created")
    private LocalDateTime createdAt;
}
