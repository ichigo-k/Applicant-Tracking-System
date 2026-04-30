package com.kephas.applicanttrackingsystem.enums;

public enum ApplicationStage {

    APPLIED {
        @Override
        public boolean canTransitionTo(ApplicationStage next) {
            return next == SCREENING || next == REJECTED;
        }
    },
    SCREENING {
        @Override
        public boolean canTransitionTo(ApplicationStage next) {
            return next == INTERVIEW || next == REJECTED;
        }
    },
    INTERVIEW {
        @Override
        public boolean canTransitionTo(ApplicationStage next) {
            return next == OFFER || next == REJECTED;
        }
    },
    OFFER {
        @Override
        public boolean canTransitionTo(ApplicationStage next) {
            return next == HIRED || next == REJECTED;
        }
    },
    HIRED {
        @Override
        public boolean canTransitionTo(ApplicationStage next) {
            return false;
        }
    },
    REJECTED {
        @Override
        public boolean canTransitionTo(ApplicationStage next) {
            return false;
        }
    };

    public abstract boolean canTransitionTo(ApplicationStage next);
}