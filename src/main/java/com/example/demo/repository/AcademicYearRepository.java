package com.example.demo.repository;

import com.example.demo.entity.AcademicYear;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {

  // Recherche par promotion
  List<AcademicYear> findByPromotionId(UUID promotionId);

  // Recherche par promotion triée par date de début (du plus récent au plus ancien)
  List<AcademicYear> findByPromotionIdOrderByStartDateDesc(UUID promotionId);
}
