package com.example.demo.repository;

import com.example.demo.entity.Grade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, UUID> {
  List<Grade> findByStudentId(UUID studentId);

  List<Grade> findByExamId(UUID examId);

  Optional<Grade> findByStudentIdAndExamId(UUID studentId, UUID examId);

  List<Grade> findByStudentIdAndExamExamSessionSemesterId(UUID studentId, UUID semesterId);

  @Query(
      "SELECT AVG(g.value) FROM Grade g WHERE g.student.id = :studentId AND"
          + " g.exam.examSession.semester.id = :semesterId")
  Double calculateAverageByStudentAndSemester(
      @Param("studentId") UUID studentId, @Param("semesterId") UUID semesterId);

  @Query(
      "SELECT SUM(g.exam.coefficient) FROM Grade g WHERE g.student.id = :studentId AND"
          + " g.exam.examSession.semester.id = :semesterId")
  Double calculateTotalCoefficients(
      @Param("studentId") UUID studentId, @Param("semesterId") UUID semesterId);
}
