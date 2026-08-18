package com.example.demo.endpoint.web.controller;

import com.example.demo.promotion.PromotionPlaceholderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PromotionsViewController {

  private final PromotionPlaceholderService promotionService;

  public PromotionsViewController(PromotionPlaceholderService promotionService) {
    this.promotionService = promotionService;
  }

  @GetMapping("/promotions")
  public String listPromotions(Model model) {
    model.addAttribute("promotions", promotionService.listAll());
    return "promotions";
  }
}
