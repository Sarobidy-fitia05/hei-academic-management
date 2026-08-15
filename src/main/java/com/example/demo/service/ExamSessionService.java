package com.example.demo.service;

import com.example.demo.entity.ExamSession;
import com.example.demo.entity.ExamSessionType;
import com.example.demo.repository.ExamSessionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamSessionService {

  private final ExamSessionRepository examSessionRepository;

  public ExamSession save(ExamSession examSession) {
    return examSessionRepository.save(examSession);
  }

  public ExamSession findById(UUID id) {
    return examSessionRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("ExamSession not found with id: " + id));
  }

  public List<ExamSession> findAll() {
    return examSessionRepository.findAll();
  }

  public List<ExamSession> findByCourseId(UUID courseId) {
    return examSessionRepository.findByCourseId(courseId);
  }

  public List<ExamSession> findByGroupId(UUID groupId) {
    return examSessionRepository.findByGroupId(groupId);
  }

  public List<ExamSession> findBySemesterId(UUID semesterId) {
    return examSessionRepository.findBySemesterId(semesterId);
  }

  public List<ExamSession> findByCourseIdAndType(UUID courseId, ExamSessionType type) {
    return examSessionRepository.findByCourseIdAndType(courseId, type);
  }

  public List<ExamSession> findBySemesterIdAndType(UUID semesterId, ExamSessionType type) {
    return examSessionRepository.findBySemesterIdAndType(semesterId, type);
  }

  public List<ExamSession> findByGroupIdAndSemesterId(UUID groupId, UUID semesterId) {
    return examSessionRepository.findByGroupIdAndSemesterId(groupId, semesterId);
  }

  public List<ExamSession> findByCourseIdAndGroupIdAndSemesterId(
      UUID courseId, UUID groupId, UUID semesterId) {
    return examSessionRepository.findByCourseIdAndGroupIdAndSemesterId(
        courseId, groupId, semesterId);
  }

  public List<ExamSession> findByStartDateAfter(LocalDate date) {
    return examSessionRepository.findByStartDateAfter(date);
  }

  public List<ExamSession> findByEndDateBefore(LocalDate date) {
    return examSessionRepository.findByEndDateBefore(date);
  }

  public List<ExamSession> findByStartDateBetween(LocalDate start, LocalDate end) {
    return examSessionRepository.findByStartDateBetween(start, end);
  }

  public List<ExamSession> findByCourseIdAndSemesterId(UUID courseId, UUID semesterId) {
    return examSessionRepository.findByCourseIdAndSemesterId(courseId, semesterId);
  }

  public List<ExamSession> findByGroupIdAndSemesterIdAndType(
      UUID groupId, UUID semesterId, ExamSessionType type) {
    return examSessionRepository.findByGroupIdAndSemesterIdAndType(groupId, semesterId, type);
  }

  public List<ExamSession> findByCourseIdAndSemesterIdAndType(
      UUID courseId, UUID semesterId, ExamSessionType type) {
    return examSessionRepository.findByCourseIdAndSemesterIdAndType(courseId, semesterId, type);
  }

  public boolean existsByCourseIdAndType(UUID courseId, ExamSessionType type) {
    return examSessionRepository.existsByCourseIdAndType(courseId, type);
  }

  public void delete(UUID id) {
    examSessionRepository.deleteById(id);
  }

  public void deleteByCourseId(UUID courseId) {
    List<ExamSession> sessions = examSessionRepository.findByCourseId(courseId);
    examSessionRepository.deleteAll(sessions);
  }
}
