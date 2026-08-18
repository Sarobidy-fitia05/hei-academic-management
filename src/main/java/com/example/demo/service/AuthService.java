package com.example.demo.service;

import com.example.demo.endpoint.rest.dto.AuthResponse;
import com.example.demo.endpoint.rest.dto.LoginRequest;
import com.example.demo.endpoint.rest.dto.RegisterRequest;
import com.example.demo.entity.UserAccount;
import com.example.demo.jwt.JwtService;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

  private final UserAccountRepository userAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthService(
          UserAccountRepository userAccountRepository,
          PasswordEncoder passwordEncoder,
          JwtService jwtService,
          AuthenticationManager authenticationManager) {
    this.userAccountRepository = userAccountRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  public AuthResponse register(RegisterRequest request) {
    if (userAccountRepository.existsByUsername(request.getUsername())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Nom d'utilisateur deja utilise");
    }

    UserAccount userAccount = new UserAccount();
    userAccount.setUsername(request.getUsername());
    userAccount.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    userAccount.setRole(request.getRole());
    userAccount.setEnabled(true);

    userAccountRepository.save(userAccount);

    return new AuthResponse(jwtService.generateToken(userAccount));
  }

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    UserAccount userAccount = userAccountRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));

    return new AuthResponse(jwtService.generateToken(userAccount));
  }
}