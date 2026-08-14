package com.example.demo.endpoint.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur utilisé uniquement dans les tests d'intégration pour vérifier que les règles
 * d'autorisation de SecurityConfig sont bien appliquées. Les futurs endpoints réels de
 * transcript/graduation/storage suivront les mêmes préfixes /api/admin, /api/teacher, /api/student.
 */
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
