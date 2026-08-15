package com.example.demo.repository;

import com.example.demo.entity.GraduationList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraduationListRepository extends JpaRepository<GraduationList, UUID> {
  List<GraduationList> findByPromotionId(UUID promotionId);

  List<GraduationList> findByProgramId(UUID programId);

  Optional<GraduationList> findByPromotionIdAndProgramIdAndGraduationYear(
      UUID promotionId, UUID programId, Integer graduationYear);

  List<GraduationList> findByGraduationYear(Integer graduationYear);
}
