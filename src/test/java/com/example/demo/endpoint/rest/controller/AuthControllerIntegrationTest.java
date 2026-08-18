package com.example.demo.endpoint.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.repository.UserAccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserAccountRepository userAccountRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void cleanUp() {
    userAccountRepository.findByUsername("tsiory").ifPresent(userAccountRepository::delete);
    userAccountRepository.findByUsername("duplicate").ifPresent(userAccountRepository::delete);
    userAccountRepository.findByUsername("loginuser").ifPresent(userAccountRepository::delete);
    userAccountRepository.findByUsername("wrongpassuser").ifPresent(userAccountRepository::delete);
  }

  @Test
  void register_creesUnCompteEtRetourneUnToken() throws Exception {
    String payload =
        objectMapper.writeValueAsString(new RegisterPayload("tsiory", "password123", "STUDENT"));

    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void register_refuseUnUsernameDejaUtilise() throws Exception {
    String payload =
        objectMapper.writeValueAsString(new RegisterPayload("duplicate", "password123", "STUDENT"));

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
        objectMapper.writeValueAsString(new RegisterPayload("loginuser", "password123", "STUDENT"));
    mockMvc
        .perform(
            post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(registerPayload))
        .andExpect(status().isOk());

    String loginPayload =
        objectMapper.writeValueAsString(new LoginPayload("loginuser", "password123"));

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void login_refuseUnMauvaisMotDePasse() throws Exception {
    String registerPayload =
        objectMapper.writeValueAsString(
            new RegisterPayload("wrongpassuser", "password123", "STUDENT"));
    mockMvc
        .perform(
            post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(registerPayload))
        .andExpect(status().isOk());

    String loginPayload =
        objectMapper.writeValueAsString(new LoginPayload("wrongpassuser", "mauvaisMotDePasse"));

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginPayload))
        .andExpect(status().isUnauthorized());
  }

  private record RegisterPayload(String username, String password, String role) {}

  private record LoginPayload(String username, String password) {}
}
