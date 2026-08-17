package com.example.demo.service;

import com.example.demo.entity.Exam;
import com.example.demo.entity.ExamSession;
import com.example.demo.entity.ExamSessionType;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.ExamSessionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

  private static final double COEFFICIENT_TOLERANCE = 0.001;

  private final ExamRepository examRepository;
  private final ExamSessionRepository examSessionRepository;

  public ExamSession createExamSession(ExamSession session) {
    return examSessionRepository.save(session);
  }

  public ExamSession createExamSession(
      UUID courseId,
      UUID groupId,
      UUID semesterId,
      ExamSessionType type,
      LocalDate startDate,
      LocalDate endDate) {

    ExamSession session = new ExamSession();
    session.setType(type);
    session.setStartDate(startDate);
    session.setEndDate(endDate);
    return examSessionRepository.save(session);
  }

  public ExamSession findExamSessionById(UUID id) {
    return examSessionRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("ExamSession not found with id: " + id));
  }

  public List<ExamSession> findAllExamSessions() {
    return examSessionRepository.findAll();
  }

  public List<ExamSession> findExamSessionsByCourseId(UUID courseId) {
    return examSessionRepository.findByCourseId(courseId);
  }

  public List<ExamSession> findExamSessionsByGroupId(UUID groupId) {
    return examSessionRepository.findByGroupId(groupId);
  }

  public List<ExamSession> findExamSessionsBySemesterId(UUID semesterId) {
    return examSessionRepository.findBySemesterId(semesterId);
  }

  public List<ExamSession> findExamSessionsByCourseAndType(UUID courseId, ExamSessionType type) {
    return examSessionRepository.findByCourseIdAndType(courseId, type);
  }

  public List<ExamSession> findExamSessionsBySemesterAndType(
      UUID semesterId, ExamSessionType type) {
    return examSessionRepository.findBySemesterIdAndType(semesterId, type);
  }

  public List<ExamSession> findExamSessionsByGroupAndSemester(UUID groupId, UUID semesterId) {
    return examSessionRepository.findByGroupIdAndSemesterId(groupId, semesterId);
  }

  public List<ExamSession> findExamSessionsByCourseGroupAndSemester(
      UUID courseId, UUID groupId, UUID semesterId) {
    return examSessionRepository.findByCourseIdAndGroupIdAndSemesterId(
        courseId, groupId, semesterId);
  }

  public List<ExamSession> findExamSessionsByStartDateAfter(LocalDate date) {
    return examSessionRepository.findByStartDateAfter(date);
  }

  public List<ExamSession> findExamSessionsByEndDateBefore(LocalDate date) {
    return examSessionRepository.findByEndDateBefore(date);
  }

  public List<ExamSession> findExamSessionsBetweenDates(LocalDate start, LocalDate end) {
    return examSessionRepository.findByStartDateBetween(start, end);
  }

  public List<ExamSession> findExamSessionsByCourseAndSemester(UUID courseId, UUID semesterId) {
    return examSessionRepository.findByCourseIdAndSemesterId(courseId, semesterId);
  }

  public List<ExamSession> findExamSessionsByGroupSemesterAndType(
      UUID groupId, UUID semesterId, ExamSessionType type) {
    return examSessionRepository.findByGroupIdAndSemesterIdAndType(groupId, semesterId, type);
  }

  public List<ExamSession> findExamSessionsByCourseSemesterAndType(
      UUID courseId, UUID semesterId, ExamSessionType type) {
    return examSessionRepository.findByCourseIdAndSemesterIdAndType(courseId, semesterId, type);
  }

  public boolean existsExamSessionByCourseAndType(UUID courseId, ExamSessionType type) {
    return examSessionRepository.existsByCourseIdAndType(courseId, type);
  }

  public ExamSession updateExamSession(UUID id, ExamSession updatedSession) {
    ExamSession existing = findExamSessionById(id);
    existing.setType(updatedSession.getType());
    existing.setStartDate(updatedSession.getStartDate());
    existing.setEndDate(updatedSession.getEndDate());
    existing.setCourse(updatedSession.getCourse());
    existing.setGroup(updatedSession.getGroup());
    existing.setSemester(updatedSession.getSemester());
    return examSessionRepository.save(existing);
  }

  public ExamSession updateExamSessionDates(UUID id, LocalDate startDate, LocalDate endDate) {
    ExamSession session = findExamSessionById(id);
    session.setStartDate(startDate);
    session.setEndDate(endDate);
    return examSessionRepository.save(session);
  }

  public void deleteExamSession(UUID id) {
    examSessionRepository.deleteById(id);
  }

  public void deleteExamSessionsByCourse(UUID courseId) {
    List<ExamSession> sessions = examSessionRepository.findByCourseId(courseId);
    examSessionRepository.deleteAll(sessions);
  }

  public Exam createExam(Exam exam) {
    return examRepository.save(exam);
  }

  public Exam createExam(UUID examSessionId, LocalDateTime examDate, Double coefficient) {
    ExamSession session = findExamSessionById(examSessionId);

    if (coefficient == null || coefficient <= 0) {
      throw new IllegalArgumentException(
          "Le coefficient d'un examen doit etre strictement positif.");
    }

    List<Exam> existingExams = examRepository.findByExamSessionId(examSessionId);

    if (session.getType() == ExamSessionType.RETAKE) {
      if (!existingExams.isEmpty()) {
        throw new IllegalStateException(
            "Une session RETAKE ne peut contenir qu'un seul examen. Session "
                + examSessionId
                + " en contient deja "
                + existingExams.size()
                + ".");
      }
      if (Math.abs(coefficient - 1.0) > COEFFICIENT_TOLERANCE) {
        throw new IllegalArgumentException(
            "Le coefficient d'un examen RETAKE doit etre egal a 1.00, recu : " + coefficient);
      }
    } else {
      double currentTotal =
          existingExams.stream()
              .mapToDouble(e -> e.getCoefficient() != null ? e.getCoefficient() : 0.0)
              .sum();

      if (currentTotal + coefficient > 1.0 + COEFFICIENT_TOLERANCE) {
        throw new IllegalStateException(
            "La somme des coefficients de la session NORMAL "
                + examSessionId
                + " depasserait 1.00 (actuel : "
                + currentTotal
                + " + "
                + coefficient
                + ").");
      }
    }

    Exam exam = new Exam();
    exam.setExamSession(session);
    exam.setExamDate(examDate);
    exam.setCoefficient(coefficient);

    return examRepository.save(exam);
  }

  public void validateSessionComplete(UUID examSessionId) {
    double total = calculateTotalCoefficients(examSessionId);
    if (Math.abs(total - 1.0) > COEFFICIENT_TOLERANCE) {
      throw new IllegalStateException(
          "La session d'examen "
              + examSessionId
              + " n'est pas complete : somme des coefficients = "
              + total
              + " (attendu 1.00).");
    }
  }

  public Exam findExamById(UUID id) {
    return examRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Exam not found with id: " + id));
  }

  public List<Exam> findAllExams() {
    return examRepository.findAll();
  }

  public List<Exam> findExamsBySessionId(UUID examSessionId) {
    return examRepository.findByExamSessionId(examSessionId);
  }

  public List<Exam> findExamsBySessionIdOrderByDate(UUID examSessionId) {
    return examRepository.findByExamSessionIdOrderByExamDateAsc(examSessionId);
  }

  public Exam updateExam(UUID id, Exam updatedExam) {
    Exam existing = findExamById(id);
    existing.setExamDate(updatedExam.getExamDate());
    existing.setCoefficient(updatedExam.getCoefficient());
    existing.setExamSession(updatedExam.getExamSession());
    return examRepository.save(existing);
  }

  public Exam updateExamDate(UUID id, LocalDateTime examDate) {
    Exam exam = findExamById(id);
    exam.setExamDate(examDate);
    return examRepository.save(exam);
  }

  public Exam updateExamCoefficient(UUID id, Double coefficient) {
    Exam exam = findExamById(id);
    exam.setCoefficient(coefficient);
    return examRepository.save(exam);
  }

  public void deleteExam(UUID id) {
    examRepository.deleteById(id);
  }

  public void deleteExamsBySession(UUID examSessionId) {
    List<Exam> exams = examRepository.findByExamSessionId(examSessionId);
    examRepository.deleteAll(exams);
  }

  public long countExamsBySession(UUID examSessionId) {
    return examRepository.findByExamSessionId(examSessionId).size();
  }

  public double calculateTotalCoefficients(UUID examSessionId) {
    List<Exam> exams = examRepository.findByExamSessionId(examSessionId);
    return exams.stream()
        .mapToDouble(e -> e.getCoefficient() != null ? e.getCoefficient() : 1.0)
        .sum();
  }

  public boolean hasGrades(UUID examId) {
    Exam exam = findExamById(examId);
    return exam.getGrades() != null && !exam.getGrades().isEmpty();
  }

  public Double getExamAverage(UUID examId) {
    Exam exam = findExamById(examId);
    if (exam.getGrades() == null || exam.getGrades().isEmpty()) {
      return null;
    }
    return exam.getGrades().stream()
        .filter(g -> g.getValue() != null)
        .mapToDouble(g -> g.getValue())
        .average()
        .orElse(0.0);
  }
}
