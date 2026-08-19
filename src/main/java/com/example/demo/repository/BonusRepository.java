package com.example.demo.repository;

import com.example.demo.entity.Bonus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BonusRepository extends JpaRepository<Bonus, UUID> {
  List<Bonus> findByCourseAttemptId(UUID courseAttemptId);

  List<Bonus> findByCourseAttemptStudentId(UUID studentId);

  List<Bonus> findByCourseAttemptCourseId(UUID courseId);
}
