package com.example.demo.config;

import com.example.demo.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    System.out.println(">>> RECHERCHE USER : " + email);

    return userRepository
        .findByEmail(email)
        .orElseThrow(
            () -> {
              System.out.println(">>> USER INTROUVABLE : " + email);
              return new UsernameNotFoundException("Utilisateur introuvable: " + email);
            });
  }
}
