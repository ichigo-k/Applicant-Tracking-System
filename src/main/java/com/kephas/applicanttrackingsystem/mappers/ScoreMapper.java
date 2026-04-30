package com.kephas.applicanttrackingsystem.mappers;


import com.kephas.applicanttrackingsystem.dtos.score.ScoreRequest;
import com.kephas.applicanttrackingsystem.dtos.score.ScoreResponse;
import com.kephas.applicanttrackingsystem.entities.Application;
import com.kephas.applicanttrackingsystem.entities.Score;
import com.kephas.applicanttrackingsystem.entities.TeamMember;
import com.kephas.applicanttrackingsystem.enums.ScoreType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScoreMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", source = "scoreType")
    @Mapping(target = "updatedBy", source = "member")
    @Mapping(target = "score", source = "request.score")
    @Mapping(target = "comment", source = "request.comment")
    @Mapping(target = "application", source = "application")
    Score toEntity(ScoreRequest request, ScoreType scoreType, TeamMember member, Application application);


    @Mapping(target = "updatedByName", source = "updatedBy.name")
    ScoreResponse toResponse(Score score);
}