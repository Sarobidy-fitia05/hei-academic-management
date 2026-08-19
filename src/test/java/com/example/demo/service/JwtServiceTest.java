package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

  private JwtService jwtService;

  private UserDetails user;

  @BeforeEach
  void setUp() throws Exception {

    jwtService = new JwtService();

    Field secretField = JwtService.class.getDeclaredField("secret");

    secretField.setAccessible(true);

    secretField.set(jwtService, "my-super-secret-key-that-is-long-enough-123456");

    Field expirationField = JwtService.class.getDeclaredField("expirationMs");

    expirationField.setAccessible(true);

    expirationField.set(jwtService, 3600000L);

    user = User.withUsername("tsiory@test.com").password("password").roles("STUDENT").build();
  }

  @Test
  void generateToken_shouldGenerateToken() {

    String token = jwtService.generateToken(user);

    assertNotNull(token);
    assertFalse(token.isBlank());
  }

  @Test
  void extractUsername_shouldReturnUsername() {

    String token = jwtService.generateToken(user);

    String username = jwtService.extractUsername(token);

    assertEquals("tsiory@test.com", username);
  }

  @Test
  void isTokenValid_shouldReturnTrueForValidToken() {

    String token = jwtService.generateToken(user);

    boolean result = jwtService.isTokenValid(token, user);

    assertTrue(result);
  }

  @Test
  void isTokenValid_shouldReturnFalseForDifferentUser() {

    String token = jwtService.generateToken(user);

    UserDetails otherUser =
        User.withUsername("other@test.com").password("password").roles("STUDENT").build();

    boolean result = jwtService.isTokenValid(token, otherUser);

    assertFalse(result);
  }

  @Test
  void isTokenValid_shouldThrowExceptionForExpiredToken() throws Exception {

    Field expirationField = JwtService.class.getDeclaredField("expirationMs");

    expirationField.setAccessible(true);

    expirationField.set(jwtService, -1000L);

    String token = jwtService.generateToken(user);

    assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, user));
  }
}
