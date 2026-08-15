package com.example.demo.service;

import com.example.demo.entity.CourseAttempt;
import com.example.demo.entity.CourseAttemptStatus;
import com.example.demo.repository.CourseAttemptRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseAttemptService {

  private final CourseAttemptRepository courseAttemptRepository;

  public CourseAttempt save(CourseAttempt courseAttempt) {
    return courseAttemptRepository.save(courseAttempt);
  }

  public CourseAttempt findById(UUID id) {
    return courseAttemptRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("CourseAttempt not found with id: " + id));
  }

  public List<CourseAttempt> findByStudentId(UUID studentId) {
    return courseAttemptRepository.findByStudentId(studentId);
  }

  public List<CourseAttempt> findByCourseId(UUID courseId) {
    return courseAttemptRepository.findByCourseId(courseId);
  }

  public List<CourseAttempt> findByStudentIdAndCourseId(UUID studentId, UUID courseId) {
    return courseAttemptRepository.findByStudentIdAndCourseId(studentId, courseId);
  }

  public CourseAttempt findLatestAttempt(UUID studentId, UUID courseId) {
    List<CourseAttempt> attempts =
        courseAttemptRepository.findByStudentIdAndCourseIdOrderByAttemptNumberDesc(
            studentId, courseId);
    return attempts.isEmpty() ? null : attempts.get(0);
  }

  public List<CourseAttempt> findByStudentIdAndStatus(UUID studentId, CourseAttemptStatus status) {
    return courseAttemptRepository.findByStudentIdAndStatus(studentId, status);
  }

  public CourseAttempt findByStudentAndCourseAndAttemptNumber(
      UUID studentId, UUID courseId, Integer attemptNumber) {
    return courseAttemptRepository
        .findByStudentIdAndCourseIdAndAttemptNumber(studentId, courseId, attemptNumber)
        .orElseThrow(() -> new RuntimeException("CourseAttempt not found"));
  }

  public List<CourseAttempt> findBySemester(UUID semesterId) {
    return courseAttemptRepository.findByStudentIdAndExamSessionSemesterId(null, semesterId);
  }

  public int getAttemptCount(UUID studentId, UUID courseId) {
    return courseAttemptRepository.findByStudentIdAndCourseId(studentId, courseId).size();
  }

  @Transactional
  public CourseAttempt updateStatus(UUID id, CourseAttemptStatus status) {
    CourseAttempt attempt = findById(id);
    attempt.setStatus(status);
    return courseAttemptRepository.save(attempt);
  }

  @Transactional
  public CourseAttempt updateFinalGrade(UUID id, Double finalGrade) {
    CourseAttempt attempt = findById(id);
    attempt.setFinalGrade(finalGrade);
    return courseAttemptRepository.save(attempt);
  }

  public void delete(UUID id) {
    courseAttemptRepository.deleteById(id);
  }
}
