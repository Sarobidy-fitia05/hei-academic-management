package com.example.demo.promotion;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PromotionPlaceholderService {

  public List<PromotionSummaryDTO> listAll() {
    return List.of(
        new PromotionSummaryDTO(1L, 2024, "Promotion 2024"),
        new PromotionSummaryDTO(2L, 2025, "Promotion 2025"),
        new PromotionSummaryDTO(3L, 2026, "Promotion 2026"));
  }
}
