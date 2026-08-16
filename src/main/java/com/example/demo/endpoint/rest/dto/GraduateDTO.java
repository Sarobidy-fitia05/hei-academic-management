package com.example.demo.endpoint.rest.dto;

import com.example.demo.graduation.Parcours;

public record GraduateDTO(
    Long studentId,
    String firstName,
    String lastName,
    Parcours parcours,
    double generalAverage,
    Integer ranking) {}
