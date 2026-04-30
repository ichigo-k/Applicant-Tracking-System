package com.kephas.applicanttrackingsystem.repositories;

import com.kephas.applicanttrackingsystem.entities.Score;
import com.kephas.applicanttrackingsystem.enums.ScoreType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScoreRepository extends JpaRepository<Score, UUID> {

    @Query("""
    SELECT s FROM Score s
    WHERE s.application.id = :applicationId
    AND s.updatedAt = (
        SELECT MAX(s2.updatedAt)
        FROM Score s2
        WHERE s2.application.id = :applicationId
        AND s2.type = s.type
    )
    """)
    List<Score> findLatestScoresByApplicationId(UUID applicationId);

    Optional<Score> findByApplicationIdAndType(UUID applicationId, ScoreType type);
}
