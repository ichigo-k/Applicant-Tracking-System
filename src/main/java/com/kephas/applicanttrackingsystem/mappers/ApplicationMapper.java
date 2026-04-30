package com.kephas.applicanttrackingsystem.mappers;

import com.kephas.applicanttrackingsystem.dtos.application.ApplicationCreateRequest;
import com.kephas.applicanttrackingsystem.dtos.application.ApplicationFullResponse;
import com.kephas.applicanttrackingsystem.dtos.application.ApplicationNoteResponse;
import com.kephas.applicanttrackingsystem.dtos.application.ApplicationResponse;
import com.kephas.applicanttrackingsystem.dtos.stage.StageHistoryResponse;
import com.kephas.applicanttrackingsystem.entities.*;
import com.kephas.applicanttrackingsystem.enums.NoteType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import com.kephas.applicanttrackingsystem.dtos.application.ApplicationNoteRequest;
import com.kephas.applicanttrackingsystem.enums.ApplicationStage;


@Mapper(componentModel = "spring", uses = {ScoreMapper.class})
public interface ApplicationMapper {

    @Mapping(target = "jobId", source = "job.id")
    ApplicationResponse toResponse(Application application);

    @Mapping(target = "candidateName", source = "request.name")
    @Mapping(target = "candidateEmail", source = "request.email")
    @Mapping(target = "job", source = "job")
    @Mapping(target = "currentStage", expression = "java(ApplicationStage.APPLIED)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "stageHistories", ignore = true)
    @Mapping(target = "scores", ignore = true)
    Application toEntity(ApplicationCreateRequest request, Job job);

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "changedAt", ignore = true)
    @Mapping(target = "application", source = "application")
    @Mapping(target = "fromStage", source = "oldStage")
    @Mapping(target = "toStage", source = "newStage")
    @Mapping(target = "changedBy", source = "member")
    @Mapping(target = "reason", source = "reason")
    StageHistory toEntity(Application application, ApplicationStage oldStage, ApplicationStage newStage, TeamMember member, String reason);

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "application", source = "application")
    @Mapping(target = "author", source = "member")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "createdAt", ignore = true)
    ApplicationNote toEntity(ApplicationNoteRequest request, Application application, TeamMember member, NoteType type );

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "stageHistory", source = "stageHistories")
    @Mapping(target = "scores", source = "scores")
    ApplicationFullResponse toFullResponse(Application application);


    @Mapping(target = "authorName", source = "author.name")
    ApplicationNoteResponse toNoteResponse(ApplicationNote note);

    List<ApplicationNoteResponse> toNoteResponseList(List<ApplicationNote> notes);

    @Mapping(target = "changedByName", source = "changedBy.name")
    StageHistoryResponse toStageHistoryResponse(StageHistory stageHistory);

    List<StageHistoryResponse> toStageHistoryResponseList(List<StageHistory> history);

}
