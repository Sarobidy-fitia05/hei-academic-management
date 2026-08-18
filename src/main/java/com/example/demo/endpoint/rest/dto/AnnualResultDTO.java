package com.example.demo.endpoint.rest.dto;

import java.util.List;

public record AnnualResultDTO(
    Long studentId,
    String studentFirstName,
    String studentLastName,
    int academicYear,
    List<GradeDTO> grades,
    double annualAverage,
    int totalCredits) {}
