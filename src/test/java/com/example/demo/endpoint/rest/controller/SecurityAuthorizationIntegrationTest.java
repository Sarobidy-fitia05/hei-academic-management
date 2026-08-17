package com.example.demo.endpoint.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.jwt.JwtService;
import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class SecurityAuthorizationIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private JwtService jwtService;

  private String studentToken;
  private String teacherToken;
  private String adminToken;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    studentToken = createUserAndGetToken("student@example.com", Role.STUDENT);
    teacherToken = createUserAndGetToken("teacher@example.com", Role.TEACHER);
    adminToken = createUserAndGetToken("admin@example.com", Role.ADMIN);
  }

  private String createUserAndGetToken(String email, Role role) {
    User user = new User("Prenom", "Nom", email, passwordEncoder.encode("password123"), role);
    userRepository.save(user);
    return jwtService.generateToken(user);
  }

  @Test
  void endpointAdmin_accessibleUniquementParAdmin() throws Exception {
    mockMvc
        .perform(get("/api/admin/ping").header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/admin/ping").header("Authorization", "Bearer " + teacherToken))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(get("/api/admin/ping").header("Authorization", "Bearer " + studentToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void endpointTeacher_accessibleParTeacherEtAdmin() throws Exception {
    mockMvc
        .perform(get("/api/teacher/ping").header("Authorization", "Bearer " + teacherToken))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/teacher/ping").header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/teacher/ping").header("Authorization", "Bearer " + studentToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void endpointStudent_accessibleParTousLesRolesAuthentifies() throws Exception {
    mockMvc
        .perform(get("/api/student/ping").header("Authorization", "Bearer " + studentToken))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/student/ping").header("Authorization", "Bearer " + teacherToken))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/student/ping").header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk());
  }

  @Test
  void endpointProtege_refuseSansToken() throws Exception {
    mockMvc.perform(get("/api/student/ping")).andExpect(status().isUnauthorized());
  }

  @Test
  void auth_endpoints_restentPublics() throws Exception {
    mockMvc.perform(post("/auth/login")).andExpect(status().is4xxClientError());
  }
}
