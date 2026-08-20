package com.example.demo.endpoint.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.demo.repository.PromotionRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PromotionsViewControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private PromotionRepository promotionRepository;

  @Test
  @WithMockUser(roles = "ADMIN")
  void listPromotions_retourneLaVueAvecLaListe() throws Exception {
    when(promotionRepository.findAll()).thenReturn(List.of());

    mockMvc
        .perform(get("/promotions"))
        .andExpect(status().isOk())
        .andExpect(view().name("promotions"))
        .andExpect(model().attributeExists("promotions"));
  }
}
