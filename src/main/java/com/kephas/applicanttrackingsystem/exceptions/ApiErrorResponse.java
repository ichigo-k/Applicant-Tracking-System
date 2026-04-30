package com.kephas.applicanttrackingsystem.exceptions;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Object details
) {}