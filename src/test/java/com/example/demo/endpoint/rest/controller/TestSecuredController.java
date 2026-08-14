package com.example.demo.endpoint.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestSecuredController {

  @GetMapping("/api/admin/ping")
  public String adminPing() {
    return "admin-ok";
  }

  @GetMapping("/api/teacher/ping")
  public String teacherPing() {
    return "teacher-ok";
  }

  @GetMapping("/api/student/ping")
  public String studentPing() {
    return "student-ok";
  }
}
