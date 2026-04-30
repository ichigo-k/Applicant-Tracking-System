package com.kephas.applicanttrackingsystem.services.impl;

import com.kephas.applicanttrackingsystem.dtos.job.JobCreateRequest;
import com.kephas.applicanttrackingsystem.dtos.job.JobResponse;
import com.kephas.applicanttrackingsystem.entities.Job;
import com.kephas.applicanttrackingsystem.enums.JobStatus;
import com.kephas.applicanttrackingsystem.mappers.JobMapper;
import com.kephas.applicanttrackingsystem.repositories.JobRepository;
import com.kephas.applicanttrackingsystem.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    @Override
    public JobResponse createJob(JobCreateRequest request) {

        Job savedJob = jobRepository.save(jobMapper.toEntity(request));

        return jobMapper.toResponse(savedJob);
    }

    @Override
    public Page<JobResponse> getAllJobs(String status, Pageable pageable) {

        Page<Job> jobs;

        if (status != null) {
            JobStatus jobStatus = JobStatus.valueOf(status.toUpperCase());
            jobs = jobRepository.findByStatus(jobStatus, pageable);
        } else {
            jobs = jobRepository.findAll(pageable);
        }

        return jobs.map(jobMapper::toResponse);
    }

    @Override
    public JobResponse getJobById(UUID id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Job not found"));

        return jobMapper.toResponse(job);
    }
}