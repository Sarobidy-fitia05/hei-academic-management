package com.example.demo.graduation;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraduationExportRepository extends JpaRepository<GraduationExport, Long> {

  Optional<GraduationExport> findTopByPromotionIdAndParcoursOrderByGeneratedAtDesc(
      Long promotionId, Parcours parcours);
}
