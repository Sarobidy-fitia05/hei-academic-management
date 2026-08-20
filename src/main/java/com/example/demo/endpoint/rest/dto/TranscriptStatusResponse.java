package com.example.demo.endpoint.rest.dto;

import java.time.LocalDateTime;

public record TranscriptStatusResponse(
    String studentId, String status, LocalDateTime generatedAt) {}
