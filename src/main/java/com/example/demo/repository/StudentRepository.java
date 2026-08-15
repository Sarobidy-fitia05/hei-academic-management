package com.example.demo.repository;

import com.example.demo.entity.Student;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

  // Recherche par référence unique
  Optional<Student> findByStudentReference(String studentReference);

  // Recherche par utilisateur
  Optional<Student> findByUserAccountId(UUID userAccountId);

  // Recherche par promotion d'entrée
  List<Student> findByEntryPromotionId(UUID promotionId);

  // Recherche par nom ou prénom
  List<Student> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
      String lastName, String firstName);

  // Recherche par email
  Optional<Student> findByEmail(String email);

  // Recherche par promotion et nom
  List<Student> findByEntryPromotionIdAndLastNameContainingIgnoreCase(
      UUID promotionId, String lastName);

  // Recherche les étudiants d'une promotion avec leurs notes
  @Query("SELECT s FROM Student s JOIN FETCH s.grades g WHERE s.entryPromotion.id = :promotionId")
  List<Student> findStudentsWithGradesByPromotion(@Param("promotionId") UUID promotionId);

  // Recherche les étudiants qui ont réussi un cours
  @Query(
      "SELECT DISTINCT s FROM Student s JOIN s.courseAttempts ca WHERE ca.course.id = :courseId AND"
          + " ca.status = 'PASSED'")
  List<Student> findStudentsWhoPassedCourse(@Param("courseId") UUID courseId);

  // Recherche les étudiants avec leur programme actuel
  @Query(
      "SELECT s FROM Student s JOIN FETCH s.studentProgramHistories sph WHERE sph.semester.id ="
          + " :semesterId AND sph.locked = true")
  List<Student> findStudentsWithCurrentProgram(@Param("semesterId") UUID semesterId);

  // Compte les étudiants par promotion
  long countByEntryPromotionId(UUID promotionId);
}
