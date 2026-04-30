package com.kephas.applicanttrackingsystem.entities;

import com.kephas.applicanttrackingsystem.enums.ScoreType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scores", indexes = {
        @Index(name = "idx_scores_application_id", columnList = "application_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    private ScoreType type;

    private Integer score;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember updatedBy;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
