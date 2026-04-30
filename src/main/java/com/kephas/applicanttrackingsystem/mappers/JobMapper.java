package com.kephas.applicanttrackingsystem.mappers;

import com.kephas.applicanttrackingsystem.dtos.job.JobCreateRequest;
import com.kephas.applicanttrackingsystem.dtos.job.JobResponse;
import com.kephas.applicanttrackingsystem.entities.Job;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobMapper {

    Job toEntity(JobCreateRequest request);

    JobResponse toResponse(Job job);
}
