package com.example.demo.repository;

import com.example.demo.entity.Graduation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraduationRepository extends JpaRepository<Graduation, UUID> {
  List<Graduation> findByStudentId(UUID studentId);

  List<Graduation> findByGraduationListId(UUID graduationListId);

  Optional<Graduation> findByStudentIdAndGraduationListId(UUID studentId, UUID graduationListId);

  List<Graduation> findByGraduationListIdOrderByRankAsc(UUID graduationListId);

  List<Graduation> findByStudentIdOrderByGraduationDateDesc(UUID studentId);
}
