package com.kephas.applicanttrackingsystem.repositories;

import com.kephas.applicanttrackingsystem.entities.Application;
import com.kephas.applicanttrackingsystem.enums.ApplicationStage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository< Application, UUID> {

    List<Application> findByJobId(UUID jobId);
    List<Application> findByJobIdAndCurrentStage(UUID jobId, ApplicationStage stage);

    boolean existsByJobIdAndCandidateEmail(UUID jobId, @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String attr0);
}
