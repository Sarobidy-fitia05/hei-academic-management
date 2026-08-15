package com.example.demo.repository;

import com.example.demo.entity.ExamSession;
import com.example.demo.entity.ExamSessionType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, UUID> {

  // Recherche par cours
  List<ExamSession> findByCourseId(UUID courseId);

  // Recherche par groupe
  List<ExamSession> findByGroupId(UUID groupId);

  // Recherche par semestre
  List<ExamSession> findBySemesterId(UUID semesterId);

  // Recherche par cours et type (NORMAL / RETAKE)
  List<ExamSession> findByCourseIdAndType(UUID courseId, ExamSessionType type);

  // Recherche par semestre et type
  List<ExamSession> findBySemesterIdAndType(UUID semesterId, ExamSessionType type);

  // Recherche par groupe et semestre
  List<ExamSession> findByGroupIdAndSemesterId(UUID groupId, UUID semesterId);

  // Recherche par cours, groupe et semestre
  List<ExamSession> findByCourseIdAndGroupIdAndSemesterId(
      UUID courseId, UUID groupId, UUID semesterId);

  // Vérifier l'existence d'une session pour un cours et un type
  boolean existsByCourseIdAndType(UUID courseId, ExamSessionType type);

  // Recherche par date de début
  List<ExamSession> findByStartDateAfter(LocalDate date);

  // Recherche par date de fin
  List<ExamSession> findByEndDateBefore(LocalDate date);

  // Recherche par période
  List<ExamSession> findByStartDateBetween(LocalDate start, LocalDate end);

  // Recherche par cours et semestre
  List<ExamSession> findByCourseIdAndSemesterId(UUID courseId, UUID semesterId);

  // Recherche par groupe, semestre et type
  List<ExamSession> findByGroupIdAndSemesterIdAndType(
      UUID groupId, UUID semesterId, ExamSessionType type);

  // Recherche par cours, semestre et type
  List<ExamSession> findByCourseIdAndSemesterIdAndType(
      UUID courseId, UUID semesterId, ExamSessionType type);
}
