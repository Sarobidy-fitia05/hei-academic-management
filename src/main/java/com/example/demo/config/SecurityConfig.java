package com.example.demo.config;

import com.example.demo.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthenticationEntryPoint authenticationEntryPoint;
  private final UserDetailsService userDetailsService;

  public SecurityConfig(
      JwtAuthenticationFilter jwtAuthenticationFilter,
      JwtAuthenticationEntryPoint authenticationEntryPoint,
      UserDetailsService userDetailsService) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.userDetailsService = userDetailsService;
  }

  @Bean
  @Order(1)
  public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/login", "/promotions/**", "/graduates/**", "/css/**", "/js/**")
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/login", "/css/**", "/js/**")
                    .permitAll()
                    .anyRequest()
                    .hasRole("ADMIN"))
        .formLogin(form -> form.defaultSuccessUrl("/promotions", true).permitAll())
        .authenticationProvider(authenticationProvider());

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/auth/**", "/hello", "/health/**")
                    .permitAll()
                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/teacher/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers("/api/student/**")
                    .hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                    .requestMatchers("/graduates/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
