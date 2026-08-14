package com.example.demo.endpoint.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void cleanUp() {
    userRepository.deleteAll();
  }

  @Test
  void register_creesUnUtilisateurEtRetourneUnToken() throws Exception {
    String payload =
        objectMapper.writeValueAsString(
            new RegisterPayload(
                "Tsiory", "Rakoto", "tsiory@example.com", "password123", "STUDENT"));

    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void register_refuseUnEmailDejaUtilise() throws Exception {
    String payload =
        objectMapper.writeValueAsString(
            new RegisterPayload(
                "Tsiory", "Rakoto", "duplicate@example.com", "password123", "STUDENT"));

    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isOk());

    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isConflict());
  }

  @Test
  void login_retourneUnTokenAvecDesIdentifiantsValides() throws Exception {
    String registerPayload =
        objectMapper.writeValueAsString(
            new RegisterPayload("Tsiory", "Rakoto", "login@example.com", "password123", "STUDENT"));
    mockMvc
        .perform(
            post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(registerPayload))
        .andExpect(status().isOk());

    String loginPayload =
        objectMapper.writeValueAsString(new LoginPayload("login@example.com", "password123"));

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void login_refuseUnMauvaisMotDePasse() throws Exception {
    String registerPayload =
        objectMapper.writeValueAsString(
            new RegisterPayload(
                "Tsiory", "Rakoto", "wrongpass@example.com", "password123", "STUDENT"));
    mockMvc
        .perform(
            post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(registerPayload))
        .andExpect(status().isOk());

    String loginPayload =
        objectMapper.writeValueAsString(
            new LoginPayload("wrongpass@example.com", "mauvaisMotDePasse"));

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginPayload))
        .andExpect(status().isUnauthorized());
  }

  private record RegisterPayload(
      String firstName, String lastName, String email, String password, String role) {}

  private record LoginPayload(String email, String password) {}
}
