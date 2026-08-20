package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.demo.config.CustomUserDetailsService;
import com.example.demo.entity.Role;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.UserAccountRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

  @Mock private UserAccountRepository userAccountRepository;

  private CustomUserDetailsService service;

  @BeforeEach
  void setUp() {
    service = new CustomUserDetailsService(userAccountRepository);
  }

  @Test
  void loadUserByUsername_retourneLUtilisateurSiTrouve() {
    UserAccount account = new UserAccount();
    account.setUsername("tsiory");
    account.setPasswordHash("hashed");
    account.setRole(Role.STUDENT);
    account.setEnabled(true);

    when(userAccountRepository.findByUsername("tsiory")).thenReturn(Optional.of(account));

    UserDetails result = service.loadUserByUsername("tsiory");

    assertThat(result.getUsername()).isEqualTo("tsiory");
  }

  @Test
  void loadUserByUsername_leveUneExceptionSiIntrouvable() {
    when(userAccountRepository.findByUsername("inconnu")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.loadUserByUsername("inconnu"))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("inconnu");
  }
}
