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

  private final ExamRepository examRepository;
  private final ExamSessionRepository examSessionRepository;

  // ============================================================
  // SESSION D'EXAMEN
  // ============================================================

  /** Créer une nouvelle session d'examen */
  public ExamSession createExamSession(ExamSession session) {
    return examSessionRepository.save(session);
  }

  /** Créer une session d'examen avec tous les paramètres */
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
    // Les relations seront définies par le caller
    return examSessionRepository.save(session);
  }

  /** Récupérer une session d'examen par son ID */
  public ExamSession findExamSessionById(UUID id) {
    return examSessionRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("ExamSession not found with id: " + id));
  }

  /** Récupérer toutes les sessions d'examen */
  public List<ExamSession> findAllExamSessions() {
    return examSessionRepository.findAll();
  }

  /** Récupérer les sessions d'examen par cours */
  public List<ExamSession> findExamSessionsByCourseId(UUID courseId) {
    return examSessionRepository.findByCourseId(courseId);
  }

  /** Récupérer les sessions d'examen par groupe */
  public List<ExamSession> findExamSessionsByGroupId(UUID groupId) {
    return examSessionRepository.findByGroupId(groupId);
  }

  /** Récupérer les sessions d'examen par semestre */
  public List<ExamSession> findExamSessionsBySemesterId(UUID semesterId) {
    return examSessionRepository.findBySemesterId(semesterId);
  }

  /** Récupérer les sessions d'examen par cours et type */
  public List<ExamSession> findExamSessionsByCourseAndType(UUID courseId, ExamSessionType type) {
    return examSessionRepository.findByCourseIdAndType(courseId, type);
  }

  /** Récupérer les sessions d'examen par semestre et type */
  public List<ExamSession> findExamSessionsBySemesterAndType(
      UUID semesterId, ExamSessionType type) {
    return examSessionRepository.findBySemesterIdAndType(semesterId, type);
  }

  /** Récupérer les sessions d'examen par groupe et semestre */
  public List<ExamSession> findExamSessionsByGroupAndSemester(UUID groupId, UUID semesterId) {
    return examSessionRepository.findByGroupIdAndSemesterId(groupId, semesterId);
  }

  /** Récupérer les sessions d'examen par cours, groupe et semestre */
  public List<ExamSession> findExamSessionsByCourseGroupAndSemester(
      UUID courseId, UUID groupId, UUID semesterId) {
    return examSessionRepository.findByCourseIdAndGroupIdAndSemesterId(
        courseId, groupId, semesterId);
  }

  /** Récupérer les sessions d'examen par période (date de début) */
  public List<ExamSession> findExamSessionsByStartDateAfter(LocalDate date) {
    return examSessionRepository.findByStartDateAfter(date);
  }

  /** Récupérer les sessions d'examen par période (date de fin) */
  public List<ExamSession> findExamSessionsByEndDateBefore(LocalDate date) {
    return examSessionRepository.findByEndDateBefore(date);
  }

  /** Récupérer les sessions d'examen entre deux dates */
  public List<ExamSession> findExamSessionsBetweenDates(LocalDate start, LocalDate end) {
    return examSessionRepository.findByStartDateBetween(start, end);
  }

  /** Récupérer les sessions d'examen par cours et semestre */
  public List<ExamSession> findExamSessionsByCourseAndSemester(UUID courseId, UUID semesterId) {
    return examSessionRepository.findByCourseIdAndSemesterId(courseId, semesterId);
  }

  /** Récupérer les sessions d'examen par groupe, semestre et type */
  public List<ExamSession> findExamSessionsByGroupSemesterAndType(
      UUID groupId, UUID semesterId, ExamSessionType type) {
    return examSessionRepository.findByGroupIdAndSemesterIdAndType(groupId, semesterId, type);
  }

  /** Récupérer les sessions d'examen par cours, semestre et type */
  public List<ExamSession> findExamSessionsByCourseSemesterAndType(
      UUID courseId, UUID semesterId, ExamSessionType type) {
    return examSessionRepository.findByCourseIdAndSemesterIdAndType(courseId, semesterId, type);
  }

  /** Vérifier si une session d'examen existe pour un cours et un type */
  public boolean existsExamSessionByCourseAndType(UUID courseId, ExamSessionType type) {
    return examSessionRepository.existsByCourseIdAndType(courseId, type);
  }

  /** Mettre à jour une session d'examen */
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

  /** Mettre à jour les dates d'une session d'examen */
  public ExamSession updateExamSessionDates(UUID id, LocalDate startDate, LocalDate endDate) {
    ExamSession session = findExamSessionById(id);
    session.setStartDate(startDate);
    session.setEndDate(endDate);
    return examSessionRepository.save(session);
  }

  /** Supprimer une session d'examen */
  public void deleteExamSession(UUID id) {
    examSessionRepository.deleteById(id);
  }

  /** Supprimer toutes les sessions d'examen d'un cours */
  public void deleteExamSessionsByCourse(UUID courseId) {
    List<ExamSession> sessions = examSessionRepository.findByCourseId(courseId);
    examSessionRepository.deleteAll(sessions);
  }

  // ============================================================
  // EXAMEN
  // ============================================================

  /** Créer un examen */
  public Exam createExam(Exam exam) {
    return examRepository.save(exam);
  }

  /** Créer un examen avec tous les paramètres */
  public Exam createExam(UUID examSessionId, LocalDateTime examDate, Double coefficient) {
    ExamSession session = findExamSessionById(examSessionId);

    Exam exam = new Exam();
    exam.setExamSession(session);
    exam.setExamDate(examDate);
    exam.setCoefficient(coefficient);

    return examRepository.save(exam);
  }

  /** Récupérer un examen par son ID */
  public Exam findExamById(UUID id) {
    return examRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Exam not found with id: " + id));
  }

  /** Récupérer tous les examens */
  public List<Exam> findAllExams() {
    return examRepository.findAll();
  }

  /** Récupérer les examens par session d'examen */
  public List<Exam> findExamsBySessionId(UUID examSessionId) {
    return examRepository.findByExamSessionId(examSessionId);
  }

  /** Récupérer les examens par session d'examen triés par date */
  public List<Exam> findExamsBySessionIdOrderByDate(UUID examSessionId) {
    return examRepository.findByExamSessionIdOrderByExamDateAsc(examSessionId);
  }

  /** Mettre à jour un examen */
  public Exam updateExam(UUID id, Exam updatedExam) {
    Exam existing = findExamById(id);
    existing.setExamDate(updatedExam.getExamDate());
    existing.setCoefficient(updatedExam.getCoefficient());
    existing.setExamSession(updatedExam.getExamSession());
    return examRepository.save(existing);
  }

  /** Mettre à jour la date d'un examen */
  public Exam updateExamDate(UUID id, LocalDateTime examDate) {
    Exam exam = findExamById(id);
    exam.setExamDate(examDate);
    return examRepository.save(exam);
  }

  /** Mettre à jour le coefficient d'un examen */
  public Exam updateExamCoefficient(UUID id, Double coefficient) {
    Exam exam = findExamById(id);
    exam.setCoefficient(coefficient);
    return examRepository.save(exam);
  }

  /** Supprimer un examen */
  public void deleteExam(UUID id) {
    examRepository.deleteById(id);
  }

  /** Supprimer tous les examens d'une session */
  public void deleteExamsBySession(UUID examSessionId) {
    List<Exam> exams = examRepository.findByExamSessionId(examSessionId);
    examRepository.deleteAll(exams);
  }

  // ============================================================
  // MÉTHODES UTILITAIRES
  // ============================================================

  /** Compter le nombre d'examens dans une session */
  public long countExamsBySession(UUID examSessionId) {
    return examRepository.findByExamSessionId(examSessionId).size();
  }

  /** Calculer le coefficient total d'une session d'examen */
  public double calculateTotalCoefficients(UUID examSessionId) {
    List<Exam> exams = examRepository.findByExamSessionId(examSessionId);
    return exams.stream()
        .mapToDouble(e -> e.getCoefficient() != null ? e.getCoefficient() : 1.0)
        .sum();
  }

  /** Vérifier si un examen a des notes */
  public boolean hasGrades(UUID examId) {
    Exam exam = findExamById(examId);
    return exam.getGrades() != null && !exam.getGrades().isEmpty();
  }

  /** Récupérer la moyenne des notes d'un examen */
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
