package com.kephas.applicanttrackingsystem.dtos.seed;

import com.kephas.applicanttrackingsystem.dtos.application.ApplicationFullResponse;
import com.kephas.applicanttrackingsystem.dtos.job.JobResponse;
import com.kephas.applicanttrackingsystem.dtos.seed.TeamMemberResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "All data created by the seed operation")
public class SeedResponse {

    @Schema(description = "Team members created")
    private List<TeamMemberResponse> teamMembers;

    @Schema(description = "Jobs created")
    private List<JobResponse> jobs;

    @Schema(description = "Applications created with full details")
    private List<ApplicationFullResponse> applications;
}
