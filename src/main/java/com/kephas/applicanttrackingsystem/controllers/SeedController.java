package com.kephas.applicanttrackingsystem.controllers;

import com.kephas.applicanttrackingsystem.dtos.seed.SeedResponse;
import com.kephas.applicanttrackingsystem.services.SeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seed")
@RequiredArgsConstructor
@Tag(name = "Seed", description = "Populate the database with dummy data for development and testing")
public class SeedController {

    private final SeedService seedService;

    @Operation(
            summary = "Seed the database",
            description = "Returns seed data and creates 3 team members, 4 jobs and 5 applications with notes, stage history and scores if not already present.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Seed data returned",
                            content = @Content(schema = @Schema(implementation = SeedResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<SeedResponse> seed() {
        return ResponseEntity.ok(seedService.seed());
    }
}
