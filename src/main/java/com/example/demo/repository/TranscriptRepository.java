package com.example.demo.repository;

import com.example.demo.entity.Transcript;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, UUID> {

  // Recherche par étudiant
  List<Transcript> findByStudentId(UUID studentId);

  // Recherche par étudiant et type (YEAR / SEMESTER / FINAL)
  List<Transcript> findByStudentIdAndType(UUID studentId, String type);

  // Recherche par année académique
  List<Transcript> findByAcademicYearId(UUID academicYearId);

  // Recherche par semestre
  List<Transcript> findBySemesterId(UUID semesterId);

  // Recherche par étudiant et semestre
  Optional<Transcript> findByStudentIdAndSemesterId(UUID studentId, UUID semesterId);

  // Recherche par étudiant et année académique
  List<Transcript> findByStudentIdAndAcademicYearId(UUID studentId, UUID academicYearId);

  // Recherche par étudiant et statut (COMPLETE / INCOMPLETE)
  List<Transcript> findByStudentIdAndStatus(UUID studentId, String status);

  // Recherche par étudiant triée par date de génération (du plus récent au plus ancien)
  List<Transcript> findByStudentIdOrderByGeneratedAtDesc(UUID studentId);
}
