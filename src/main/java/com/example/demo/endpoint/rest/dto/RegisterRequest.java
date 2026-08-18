package com.example.demo.endpoint.rest.dto;

import com.example.demo.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

  @NotBlank private String username;

  @NotBlank
  @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caracteres")
  private String password;

  @NotNull private Role role;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
