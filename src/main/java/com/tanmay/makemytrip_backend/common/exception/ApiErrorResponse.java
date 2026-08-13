package com.tanmay.makemytrip_backend.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard structure for API error responses.
 */
public record ApiErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors) {
}