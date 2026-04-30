package com.kephas.applicanttrackingsystem.services.impl;

import com.kephas.applicanttrackingsystem.dtos.application.*;
import com.kephas.applicanttrackingsystem.dtos.score.ScoreRequest;
import com.kephas.applicanttrackingsystem.dtos.score.ScoreResponse;
import com.kephas.applicanttrackingsystem.dtos.stage.StageUpdateRequest;
import com.kephas.applicanttrackingsystem.entities.*;
import com.kephas.applicanttrackingsystem.enums.ApplicationStage;
import com.kephas.applicanttrackingsystem.enums.NoteType;
import com.kephas.applicanttrackingsystem.enums.ScoreType;
import com.kephas.applicanttrackingsystem.mappers.ApplicationMapper;
import com.kephas.applicanttrackingsystem.mappers.ScoreMapper;
import com.kephas.applicanttrackingsystem.repositories.*;
import com.kephas.applicanttrackingsystem.services.ApplicationService;
import com.kephas.applicanttrackingsystem.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ApplicationNoteRepository noteRepository;
    private final ScoreRepository scoreRepository;
    private final StageHistoryRepository stageHistoryRepository;

    private final ApplicationMapper applicationMapper;
    private final ScoreMapper scoreMapper;

    private final NotificationService notificationService;


    @Override
    public ApplicationResponse createApplication(UUID jobId, ApplicationCreateRequest request) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        boolean exists = applicationRepository
                .existsByJobIdAndCandidateEmail(jobId, request.getEmail());

        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You have already applied for this job");
        }

        Application application = applicationMapper.toEntity(request, job);

        Application saved = applicationRepository.save(application);

        return applicationMapper.toResponse(saved);
    }


    @Override
    public List<ApplicationResponse> getApplicationsForJob(UUID jobId, String stage) {

        List<Application> applications;

        if (stage != null) {
            ApplicationStage stageEnum = ApplicationStage.valueOf(stage.toUpperCase());
            applications = applicationRepository.findByJobIdAndCurrentStage(jobId, stageEnum);
        } else {
            applications = applicationRepository.findByJobId(jobId);
        }

        return applications.stream()
                .map(applicationMapper::toResponse)
                .toList();
    }


    @Override
    public ApplicationFullResponse getApplicationById(UUID id) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));

        List<Score> latestScores = scoreRepository.findLatestScoresByApplicationId(id);

        Map<ScoreType, Score> scoreMap = latestScores.stream()
                .collect(Collectors.toMap(Score::getType, Function.identity()));

        List<ScoreResponse> scores = new ArrayList<>();

        for (ScoreType type : ScoreType.values()) {
            Score score = scoreMap.get(type);

            if (score == null) {
                scores.add(ScoreResponse.builder()
                        .type(type)
                        .score(0)
                        .comment(null)
                        .updatedByName(null)
                        .updatedAt(null)
                        .build());
            } else {
                scores.add(scoreMapper.toResponse(score));
            }
        }

        ApplicationFullResponse response = applicationMapper.toFullResponse(application);
        response.setScores(scores);

        return response;
    }


    @Override
    public ApplicationResponse updateApplicationStage(
            UUID id,
            UUID teamMemberId,
            StageUpdateRequest request) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));

        TeamMember member = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Team member not found"));

        ApplicationStage oldStage = application.getCurrentStage();
        ApplicationStage newStage = ApplicationStage.valueOf(request.getNewStage().toUpperCase());

        if (!oldStage.canTransitionTo(newStage)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid transition from " + oldStage + " to " + newStage);
        }

        application.setCurrentStage(newStage);

        Application saved = applicationRepository.save(application);

        StageHistory history = applicationMapper.toEntity(
                saved,
                oldStage,
                newStage,
                member,
                request.getReason()
        );

        stageHistoryRepository.save(history);

        if (newStage == ApplicationStage.HIRED ||
                newStage == ApplicationStage.REJECTED) {

            notificationService.sendNotificationAsync(
                    saved.getId(),
                    saved.getCandidateEmail(),
                    newStage.name()
            );
        }

        return applicationMapper.toResponse(saved);
    }


    @Override
    public ApplicationNoteResponse addNote(
            UUID id,
            UUID teamMemberId,
            ApplicationNoteRequest request) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));

        TeamMember member = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Team member not found"));

        NoteType type;
        try {
            type = NoteType.valueOf(request.getType().toUpperCase());
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid note type: " + request.getType());
        }

        ApplicationNote note = applicationMapper.toEntity(
                request, application, member, type
        );

        ApplicationNote saved = noteRepository.save(note);

        return applicationMapper.toNoteResponse(saved);
    }


    @Override
    public List<ApplicationNoteResponse> getApplicationNotes(UUID id) {

        List<ApplicationNote> notes = noteRepository.findByApplicationId(id);

        return applicationMapper.toNoteResponseList(notes);
    }


    @Override
    public ScoreResponse upsertScore(
            UUID id,
            UUID teamMemberId,
            ScoreType scoreType,
            ScoreRequest request) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));

        TeamMember member = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Team member not found"));

        Score existingScore = scoreRepository
                .findByApplicationIdAndType(id, scoreType)
                .orElse(null);

        Score score;

        if (existingScore != null) {
            existingScore.setScore(request.getScore());
            existingScore.setComment(request.getComment());
            existingScore.setUpdatedBy(member);
            score = existingScore;
        } else {
            score = scoreMapper.toEntity(request, scoreType, member, application);
        }

        Score saved = scoreRepository.save(score);

        return scoreMapper.toResponse(saved);
    }
}