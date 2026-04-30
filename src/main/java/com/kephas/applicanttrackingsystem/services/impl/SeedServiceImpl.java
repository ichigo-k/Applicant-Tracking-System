package com.kephas.applicanttrackingsystem.services.impl;

import com.kephas.applicanttrackingsystem.dtos.application.ApplicationFullResponse;
import com.kephas.applicanttrackingsystem.dtos.application.ApplicationNoteResponse;
import com.kephas.applicanttrackingsystem.dtos.job.JobResponse;
import com.kephas.applicanttrackingsystem.dtos.score.ScoreResponse;
import com.kephas.applicanttrackingsystem.dtos.seed.SeedResponse;
import com.kephas.applicanttrackingsystem.dtos.seed.TeamMemberResponse;
import com.kephas.applicanttrackingsystem.dtos.stage.StageHistoryResponse;
import com.kephas.applicanttrackingsystem.entities.*;
import com.kephas.applicanttrackingsystem.enums.*;
import com.kephas.applicanttrackingsystem.repositories.*;
import com.kephas.applicanttrackingsystem.services.SeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeedServiceImpl implements SeedService {

    private final TeamMemberRepository teamMemberRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationNoteRepository noteRepository;
    private final ScoreRepository scoreRepository;
    private final StageHistoryRepository stageHistoryRepository;

    @Override
    @Transactional
    public SeedResponse seed() {

        List<TeamMember> members = teamMemberRepository.saveAll(buildTeamMembers());
        TeamMember recruiter     = members.get(0);
        TeamMember hiringManager = members.get(1);

        List<Job> jobs = jobRepository.saveAll(buildJobs());

        List<Application> applications = buildAndSaveApplications(jobs, recruiter, hiringManager);

        return SeedResponse.builder()
                .teamMembers(members.stream().map(this::toTeamMemberResponse).toList())
                .jobs(jobs.stream().map(this::toJobResponse).toList())
                .applications(applications.stream().map(this::toFullResponse).toList())
                .build();
    }

    private List<TeamMember> buildTeamMembers() {
        TeamMember recruiter = new TeamMember();
        recruiter.setName("Alice Smith");
        recruiter.setEmail("alice.smith@company.com");
        recruiter.setRole(MemberRole.RECRUITER);

        TeamMember hiringManager = new TeamMember();
        hiringManager.setName("Bob Jones");
        hiringManager.setEmail("bob.jones@company.com");
        hiringManager.setRole(MemberRole.HIRING_MANAGER);

        TeamMember recruiter2 = new TeamMember();
        recruiter2.setName("Carol White");
        recruiter2.setEmail("carol.white@company.com");
        recruiter2.setRole(MemberRole.RECRUITER);

        return List.of(recruiter, hiringManager, recruiter2);
    }

    private List<Job> buildJobs() {
        Job job1 = new Job();
        job1.setTitle("Senior Backend Engineer");
        job1.setDescription("Build and maintain scalable REST APIs using Java and Spring Boot.");
        job1.setLocation("New York, NY");
        job1.setStatus(JobStatus.OPEN);

        Job job2 = new Job();
        job2.setTitle("Frontend Developer");
        job2.setDescription("Develop responsive UIs with React and TypeScript.");
        job2.setLocation("Remote");
        job2.setStatus(JobStatus.OPEN);

        Job job3 = new Job();
        job3.setTitle("DevOps Engineer");
        job3.setDescription("Manage CI/CD pipelines, Kubernetes clusters and cloud infrastructure.");
        job3.setLocation("Austin, TX");
        job3.setStatus(JobStatus.OPEN);

        Job job4 = new Job();
        job4.setTitle("Product Manager");
        job4.setDescription("Define product roadmap and work closely with engineering teams.");
        job4.setLocation("San Francisco, CA");
        job4.setStatus(JobStatus.CLOSED);

        return List.of(job1, job2, job3, job4);
    }

    private List<Application> buildAndSaveApplications(List<Job> jobs, TeamMember recruiter, TeamMember hiringManager) {
        Job backendJob  = jobs.get(0);
        Job frontendJob = jobs.get(1);
        Job devopsJob   = jobs.get(2);

        return List.of(
                buildAndSaveApplication(backendJob, "Jane Doe", "jane.doe@example.com",
                        ApplicationStage.INTERVIEW, recruiter, hiringManager,
                        NoteType.INTERVIEW, "Strong system design skills, good communicator.",
                        ScoreType.INTERVIEW, 4, "Solid technical depth."),

                buildAndSaveApplication(backendJob, "Mark Taylor", "mark.taylor@example.com",
                        ApplicationStage.SCREENING, recruiter, hiringManager,
                        NoteType.SCREENING, "Good resume, needs phone screen.",
                        ScoreType.ASSESSMENT, 3, "Average problem-solving speed."),

                buildAndSaveApplication(frontendJob, "Sara Lee", "sara.lee@example.com",
                        ApplicationStage.OFFER, recruiter, hiringManager,
                        NoteType.INTERVIEW, "Excellent React knowledge and portfolio.",
                        ScoreType.CULTURE_FIT, 5, "Great culture fit, very collaborative."),

                buildAndSaveApplication(frontendJob, "Tom Brown", "tom.brown@example.com",
                        ApplicationStage.REJECTED, recruiter, hiringManager,
                        NoteType.RED_FLAG, "Misrepresented experience on resume.",
                        ScoreType.INTERVIEW, 1, "Did not meet minimum requirements."),

                buildAndSaveApplication(devopsJob, "Nina Patel", "nina.patel@example.com",
                        ApplicationStage.APPLIED, recruiter, hiringManager,
                        NoteType.GENERAL, "Referred by an internal employee.",
                        ScoreType.ASSESSMENT, 4, "Strong Kubernetes background.")
        );
    }

    private Application buildAndSaveApplication(
            Job job,
            String name, String email,
            ApplicationStage stage,
            TeamMember recruiter, TeamMember hiringManager,
            NoteType noteType, String noteText,
            ScoreType scoreType, int scoreValue, String scoreComment) {

        Application app = new Application();
        app.setJob(job);
        app.setCandidateName(name);
        app.setCandidateEmail(email);
        app.setCurrentStage(stage);
        Application saved = applicationRepository.save(app);

        ApplicationNote note = new ApplicationNote();
        note.setApplication(saved);
        note.setAuthor(recruiter);
        note.setType(noteType);
        note.setDescription(noteText);
        noteRepository.save(note);

        StageHistory history = new StageHistory();
        history.setApplication(saved);
        history.setFromStage(ApplicationStage.APPLIED);
        history.setToStage(stage);
        history.setChangedBy(hiringManager);
        history.setReason("Initial stage assignment during seed");
        stageHistoryRepository.save(history);

        Score score = new Score();
        score.setApplication(saved);
        score.setType(scoreType);
        score.setScore(scoreValue);
        score.setComment(scoreComment);
        score.setUpdatedBy(hiringManager);
        scoreRepository.save(score);

        return applicationRepository.findById(saved.getId()).orElseThrow();
    }

    private TeamMemberResponse toTeamMemberResponse(TeamMember m) {
        return TeamMemberResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .email(m.getEmail())
                .role(m.getRole())
                .createdAt(m.getCreatedAt())
                .build();
    }

    private JobResponse toJobResponse(Job j) {
        return JobResponse.builder()
                .id(j.getId())
                .title(j.getTitle())
                .description(j.getDescription())
                .location(j.getLocation())
                .status(j.getStatus())
                .createdAt(j.getCreatedAt())
                .updatedAt(j.getUpdatedAt())
                .build();
    }

    private ApplicationFullResponse toFullResponse(Application a) {
        List<ApplicationNoteResponse> notes = a.getNotes().stream()
                .map(n -> ApplicationNoteResponse.builder()
                        .id(n.getId())
                        .type(n.getType())
                        .description(n.getDescription())
                        .authorName(n.getAuthor().getName())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();

        List<StageHistoryResponse> history = a.getStageHistories().stream()
                .map(h -> StageHistoryResponse.builder()
                        .id(h.getId())
                        .fromStage(h.getFromStage())
                        .toStage(h.getToStage())
                        .changedByName(h.getChangedBy().getName())
                        .changedAt(h.getChangedAt())
                        .reason(h.getReason())
                        .build())
                .toList();

        List<ScoreResponse> scores = a.getScores().stream()
                .map(s -> ScoreResponse.builder()
                        .id(s.getId())
                        .type(s.getType())
                        .score(s.getScore())
                        .comment(s.getComment())
                        .updatedByName(s.getUpdatedBy().getName())
                        .updatedAt(s.getUpdatedAt())
                        .build())
                .toList();

        return ApplicationFullResponse.builder()
                .id(a.getId())
                .jobId(a.getJob().getId())
                .candidateName(a.getCandidateName())
                .candidateEmail(a.getCandidateEmail())
                .currentStage(a.getCurrentStage())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .notes(notes)
                .stageHistory(history)
                .scores(scores)
                .build();
    }
}
