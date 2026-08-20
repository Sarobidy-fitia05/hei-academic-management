package com.example.demo.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.jwt.JwtAuthenticationFilter;
import com.example.demo.jwt.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock private JwtService jwtService;

  @Mock private UserDetailsService userDetailsService;

  @Mock private FilterChain filterChain;

  private JwtAuthenticationFilter filter;

  private UserDetails userDetails;

  @BeforeEach
  void setUp() {

    filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

    userDetails =
        User.withUsername("tsiory@test.com").password("password").roles("STUDENT").build();

    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldContinueWhenAuthorizationHeaderIsMissing() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();

    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);

    verify(jwtService, never()).extractUsername(anyString());

    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void shouldContinueWhenAuthorizationIsNotBearer() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();

    request.addHeader("Authorization", "Basic abc123");

    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);

    verify(jwtService, never()).extractUsername(anyString());
  }

  @Test
  void shouldAuthenticateWhenTokenIsValid() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();

    request.addHeader("Authorization", "Bearer valid-token");

    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtService.extractUsername("valid-token")).thenReturn("tsiory@test.com");

    when(userDetailsService.loadUserByUsername("tsiory@test.com")).thenReturn(userDetails);

    when(jwtService.isTokenValid("valid-token", userDetails)).thenReturn(true);

    filter.doFilter(request, response, filterChain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());

    verify(userDetailsService).loadUserByUsername("tsiory@test.com");

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotAuthenticateWhenTokenIsInvalid() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();

    request.addHeader("Authorization", "Bearer invalid-token");

    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtService.extractUsername("invalid-token")).thenReturn("tsiory@test.com");

    when(userDetailsService.loadUserByUsername("tsiory@test.com")).thenReturn(userDetails);

    when(jwtService.isTokenValid("invalid-token", userDetails)).thenReturn(false);

    filter.doFilter(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotReplaceExistingAuthentication() throws Exception {

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authentication);

    MockHttpServletRequest request = new MockHttpServletRequest();

    request.addHeader("Authorization", "Bearer valid-token");

    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtService.extractUsername("valid-token")).thenReturn("tsiory@test.com");

    filter.doFilter(request, response, filterChain);

    verify(userDetailsService, never()).loadUserByUsername(anyString());

    verify(filterChain).doFilter(request, response);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
