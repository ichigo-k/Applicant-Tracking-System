package com.kephas.applicanttrackingsystem.services;

import com.kephas.applicanttrackingsystem.dtos.job.JobCreateRequest;
import com.kephas.applicanttrackingsystem.dtos.job.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JobService {

    JobResponse createJob(JobCreateRequest request);

    Page<JobResponse> getAllJobs(String status, Pageable pageable);

    JobResponse getJobById(UUID id);
}
