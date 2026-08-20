package com.example.demo.service;

import com.example.demo.entity.Promotion;
import com.example.demo.repository.PromotionRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionService {

  private final PromotionRepository promotionRepository;

  public Promotion save(Promotion promotion) {
    if (promotion.getYear() == null) {
      throw new IllegalArgumentException("Promotion year cannot be null");
    }

    if (promotion.getId() == null && promotionRepository.existsByYear(promotion.getYear())) {
      throw new IllegalArgumentException(
          "A promotion already exists for year: " + promotion.getYear());
    }

    return promotionRepository.save(promotion);
  }

  public Promotion findById(UUID id) {
    return promotionRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + id));
  }

  public Promotion findByYear(Integer year) {
    return promotionRepository
        .findByYear(year)
        .orElseThrow(() -> new RuntimeException("Promotion not found for year: " + year));
  }

  public List<Promotion> findAll() {
    return promotionRepository.findAll();
  }

  public boolean existsByYear(Integer year) {
    return promotionRepository.existsByYear(year);
  }

  public Promotion update(UUID id, Promotion updatedPromotion) {
    Promotion existing = findById(id);

    if (updatedPromotion.getYear() == null) {
      throw new IllegalArgumentException("Promotion year cannot be null");
    }

    if (!existing.getYear().equals(updatedPromotion.getYear())
        && promotionRepository.existsByYear(updatedPromotion.getYear())) {
      throw new IllegalArgumentException(
          "A promotion already exists for year: " + updatedPromotion.getYear());
    }

    existing.setYear(updatedPromotion.getYear());
    existing.setGroupPrefix(updatedPromotion.getGroupPrefix());

    return promotionRepository.save(existing);
  }

  public void delete(UUID id) {
    Promotion promotion = findById(id);
    promotionRepository.delete(promotion);
  }
}
