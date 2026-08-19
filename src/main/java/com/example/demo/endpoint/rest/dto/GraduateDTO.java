package com.example.demo.endpoint.rest.dto;

import java.util.UUID;

public record GraduateDTO(
    UUID studentId,
    String firstName,
    String lastName,
    String programCode,
    double generalAverage,
    Integer ranking) {}
