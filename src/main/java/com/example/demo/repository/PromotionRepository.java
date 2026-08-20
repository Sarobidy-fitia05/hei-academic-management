package com.example.demo.repository;

import com.example.demo.entity.Promotion;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

  // Recherche par année (unique)
  Optional<Promotion> findByYear(Integer year);

  // Vérifie si une promotion existe pour une année donnée
  boolean existsByYear(Integer year);
}
