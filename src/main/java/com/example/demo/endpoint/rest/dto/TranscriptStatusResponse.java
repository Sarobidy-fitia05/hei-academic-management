package com.example.demo.endpoint.rest.dto;

import java.time.LocalDateTime;

public record TranscriptStatusResponse(Long studentId, String status, LocalDateTime generatedAt) {}
