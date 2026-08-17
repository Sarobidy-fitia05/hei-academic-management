package com.example.demo.repository;

import com.example.demo.entity.CourseAttempt;
import com.example.demo.entity.CourseAttemptStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseAttemptRepository extends JpaRepository<CourseAttempt, UUID> {
  List<CourseAttempt> findByStudentId(UUID studentId);

  List<CourseAttempt> findByCourseId(UUID courseId);

  List<CourseAttempt> findByStudentIdAndCourseId(UUID studentId, UUID courseId);

  List<CourseAttempt> findByStudentIdAndStatus(UUID studentId, CourseAttemptStatus status);

  Optional<CourseAttempt> findByStudentIdAndCourseIdAndAttemptNumber(
      UUID studentId, UUID courseId, Integer attemptNumber);

  List<CourseAttempt> findByExamSessionId(UUID examSessionId);

  List<CourseAttempt> findByStudentIdAndExamSessionSemesterId(UUID studentId, UUID semesterId);

  List<CourseAttempt> findByStudentIdAndCourseIdOrderByAttemptNumberDesc(
      UUID studentId, UUID courseId);

  List<CourseAttempt> findByExamSessionSemesterId(UUID semesterId);
}
