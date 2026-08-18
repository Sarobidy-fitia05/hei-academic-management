package com.example.demo.endpoint.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.entity.Role;
import com.example.demo.entity.UserAccount;
import com.example.demo.jwt.JwtService;
import com.example.demo.repository.UserAccountRepository;
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
@ActiveProfiles("test")
class SecurityAuthorizationIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserAccountRepository userAccountRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private JwtService jwtService;

  private String studentToken;
  private String teacherToken;
  private String adminToken;

  @BeforeEach
  void setUp() {
    userAccountRepository.findByUsername("student_test").ifPresent(userAccountRepository::delete);
    userAccountRepository.findByUsername("teacher_test").ifPresent(userAccountRepository::delete);
    userAccountRepository.findByUsername("admin_test").ifPresent(userAccountRepository::delete);
    studentToken = createUserAndGetToken("student_test", Role.STUDENT);
    teacherToken = createUserAndGetToken("teacher_test", Role.TEACHER);
    adminToken = createUserAndGetToken("admin_test", Role.ADMIN);
  }

  private String createUserAndGetToken(String username, Role role) {
    UserAccount userAccount = new UserAccount();
    userAccount.setUsername(username);
    userAccount.setPasswordHash(passwordEncoder.encode("password123"));
    userAccount.setRole(role);
    userAccount.setEnabled(true);
    userAccountRepository.save(userAccount);
    return jwtService.generateToken(userAccount);
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
