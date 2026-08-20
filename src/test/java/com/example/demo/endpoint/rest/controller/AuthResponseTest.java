package com.example.demo.endpoint.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.dto.AuthResponse;
import org.junit.jupiter.api.Test;

class AuthResponseTest {

  @Test
  void constructeurEtGetter_fonctionnentCorrectement() {
    AuthResponse response = new AuthResponse("mon-token-jwt");

    assertThat(response.getToken()).isEqualTo("mon-token-jwt");
  }

  @Test
  void constructeurVideEtSetter_fonctionnentCorrectement() {
    AuthResponse response = new AuthResponse();
    response.setToken("autre-token");

    assertThat(response.getToken()).isEqualTo("autre-token");
  }
}
