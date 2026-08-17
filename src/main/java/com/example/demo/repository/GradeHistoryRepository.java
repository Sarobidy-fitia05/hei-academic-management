package com.example.demo.repository;

import com.example.demo.entity.GradeHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeHistoryRepository extends JpaRepository<GradeHistory, UUID> {
  List<GradeHistory> findByGradeIdOrderByChangedAtDesc(UUID gradeId);

  List<GradeHistory> findByChangedById(UUID changedById);

  List<GradeHistory> findByGradeId(UUID gradeId);
}
