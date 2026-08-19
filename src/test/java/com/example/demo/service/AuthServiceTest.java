package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.endpoint.rest.dto.AuthResponse;
import com.example.demo.endpoint.rest.dto.LoginRequest;
import com.example.demo.endpoint.rest.dto.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.UserAccount;
import com.example.demo.jwt.JwtService;
import com.example.demo.repository.UserAccountRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserAccountRepository userAccountRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtService jwtService;

  @Mock private AuthenticationManager authenticationManager;

  private AuthService authService;

  @BeforeEach
  void setUp() {
    authService =
        new AuthService(userAccountRepository, passwordEncoder, jwtService, authenticationManager);
  }

  @Test
  void register_shouldCreateUserAndReturnToken() {

    RegisterRequest request = new RegisterRequest();

    request.setUsername("tsiory");
    request.setPassword("password123");
    request.setRole(Role.STUDENT);

    when(userAccountRepository.existsByUsername("tsiory")).thenReturn(false);

    when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

    when(jwtService.generateToken(any(UserAccount.class))).thenReturn("jwt-token");

    AuthResponse response = authService.register(request);

    assertEquals("jwt-token", response.getToken());

    ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);

    verify(userAccountRepository).save(captor.capture());

    UserAccount savedUser = captor.getValue();

    assertEquals("tsiory", savedUser.getUsername());

    assertEquals("encoded-password", savedUser.getPasswordHash());

    assertEquals(Role.STUDENT, savedUser.getRole());

    assertEquals(true, savedUser.isEnabled());

    verify(userAccountRepository).existsByUsername("tsiory");

    verify(passwordEncoder).encode("password123");

    verify(jwtService).generateToken(savedUser);
  }

  @Test
  void register_shouldRejectExistingUsername() {

    RegisterRequest request = new RegisterRequest();

    request.setUsername("duplicate");
    request.setPassword("password123");
    request.setRole(Role.STUDENT);

    when(userAccountRepository.existsByUsername("duplicate")).thenReturn(true);

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> authService.register(request));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

    verify(userAccountRepository).existsByUsername("duplicate");

    verify(userAccountRepository, never()).save(any(UserAccount.class));

    verify(passwordEncoder, never()).encode(any(String.class));

    verify(jwtService, never()).generateToken(any(UserAccount.class));
  }

  @Test
  void login_shouldAuthenticateAndReturnToken() {

    LoginRequest request = new LoginRequest();

    request.setUsername("loginuser");
    request.setPassword("password123");

    UserAccount user = new UserAccount();

    user.setUsername("loginuser");
    user.setPasswordHash("encoded-password");
    user.setRole(Role.STUDENT);
    user.setEnabled(true);

    when(userAccountRepository.findByUsername("loginuser")).thenReturn(Optional.of(user));

    when(jwtService.generateToken(user)).thenReturn("jwt-token");

    AuthResponse response = authService.login(request);

    assertEquals("jwt-token", response.getToken());

    verify(authenticationManager)
        .authenticate(
            any(
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken
                    .class));

    verify(userAccountRepository).findByUsername("loginuser");

    verify(jwtService).generateToken(user);
  }

  @Test
  void login_shouldRejectUnknownUser() {

    LoginRequest request = new LoginRequest();

    request.setUsername("unknown");
    request.setPassword("password123");

    when(userAccountRepository.findByUsername("unknown")).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> authService.login(request));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

    verify(authenticationManager)
        .authenticate(
            any(
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken
                    .class));

    verify(userAccountRepository).findByUsername("unknown");

    verify(jwtService, never()).generateToken(any(UserAccount.class));
  }
}
