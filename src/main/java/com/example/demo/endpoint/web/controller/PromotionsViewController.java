package com.example.demo.endpoint.web.controller;

import com.example.demo.repository.PromotionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PromotionsViewController {

  private final PromotionRepository promotionRepository;

  public PromotionsViewController(PromotionRepository promotionRepository) {
    this.promotionRepository = promotionRepository;
  }

  @GetMapping("/promotions")
  public String listPromotions(Model model) {
    model.addAttribute("promotions", promotionRepository.findAll());
    return "promotions";
  }
}
