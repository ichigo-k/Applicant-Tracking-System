package com.kephas.applicanttrackingsystem.repositories;

import com.kephas.applicanttrackingsystem.entities.Job;
import com.kephas.applicanttrackingsystem.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    Page<Job> findByStatus(JobStatus status, Pageable pageable);
}
