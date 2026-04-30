package com.kephas.applicanttrackingsystem.unit;

import com.kephas.applicanttrackingsystem.enums.ApplicationStage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StageTransitionTest {

    @Test
    void applied_canMoveTo_screening() {
        assertTrue(ApplicationStage.APPLIED.canTransitionTo(ApplicationStage.SCREENING));
    }

    @Test
    void applied_canMoveTo_rejected() {
        assertTrue(ApplicationStage.APPLIED.canTransitionTo(ApplicationStage.REJECTED));
    }

    @Test
    void applied_cannotSkipTo_interview() {
        assertFalse(ApplicationStage.APPLIED.canTransitionTo(ApplicationStage.INTERVIEW));
    }

    @Test
    void applied_cannotSkipTo_offer() {
        assertFalse(ApplicationStage.APPLIED.canTransitionTo(ApplicationStage.OFFER));
    }

    @Test
    void applied_cannotSkipTo_hired() {
        assertFalse(ApplicationStage.APPLIED.canTransitionTo(ApplicationStage.HIRED));
    }

    @Test
    void screening_canMoveTo_interview() {
        assertTrue(ApplicationStage.SCREENING.canTransitionTo(ApplicationStage.INTERVIEW));
    }

    @Test
    void screening_canMoveTo_rejected() {
        assertTrue(ApplicationStage.SCREENING.canTransitionTo(ApplicationStage.REJECTED));
    }

    @Test
    void screening_cannotGoBackTo_applied() {
        assertFalse(ApplicationStage.SCREENING.canTransitionTo(ApplicationStage.APPLIED));
    }

    @Test
    void interview_canMoveTo_offer() {
        assertTrue(ApplicationStage.INTERVIEW.canTransitionTo(ApplicationStage.OFFER));
    }

    @Test
    void interview_canMoveTo_rejected() {
        assertTrue(ApplicationStage.INTERVIEW.canTransitionTo(ApplicationStage.REJECTED));
    }

    @Test
    void interview_cannotSkipTo_hired() {
        assertFalse(ApplicationStage.INTERVIEW.canTransitionTo(ApplicationStage.HIRED));
    }

    @Test
    void offer_canMoveTo_hired() {
        assertTrue(ApplicationStage.OFFER.canTransitionTo(ApplicationStage.HIRED));
    }

    @Test
    void offer_canMoveTo_rejected() {
        assertTrue(ApplicationStage.OFFER.canTransitionTo(ApplicationStage.REJECTED));
    }

    @Test
    void hired_isTerminal_cannotTransitionAnywhere() {
        for (ApplicationStage stage : ApplicationStage.values()) {
            assertFalse(ApplicationStage.HIRED.canTransitionTo(stage),
                    "HIRED should not be able to transition to " + stage);
        }
    }

    @Test
    void rejected_isTerminal_cannotTransitionAnywhere() {
        for (ApplicationStage stage : ApplicationStage.values()) {
            assertFalse(ApplicationStage.REJECTED.canTransitionTo(stage),
                    "REJECTED should not be able to transition to " + stage);
        }
    }
}
