package com.example.demo.service;

import com.example.demo.entity.Exam;
import com.example.demo.entity.Grade;
import com.example.demo.entity.GradeHistory;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.GradeHistoryRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.TeacherRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GradeService {

  private static final double MIN_GRADE = 0.0;
  private static final double MAX_GRADE = 20.0;

  private final GradeRepository gradeRepository;
  private final GradeHistoryRepository gradeHistoryRepository;
  private final TeacherRepository teacherRepository;
  private final CourseAssignmentService courseAssignmentService;

  public Grade saveGrade(Grade grade) {
    validateGradeValue(grade.getValue());
    validateTeacherIsAssigned(grade);

    grade.setRecordedAt(LocalDateTime.now());
    return gradeRepository.save(grade);
  }

  public Grade findById(UUID id) {
    return gradeRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Grade not found with id: " + id));
  }

  public List<Grade> findByStudentId(UUID studentId) {
    return gradeRepository.findByStudentId(studentId);
  }

  public List<Grade> findByExamId(UUID examId) {
    return gradeRepository.findByExamId(examId);
  }

  public Grade findByStudentAndExam(UUID studentId, UUID examId) {
    return gradeRepository
        .findByStudentIdAndExamId(studentId, examId)
        .orElseThrow(
            () ->
                new RuntimeException(
                    "Grade not found for student: " + studentId + " and exam: " + examId));
  }

  public List<Grade> findByStudentAndSemester(UUID studentId, UUID semesterId) {
    return gradeRepository.findByStudentIdAndExamExamSessionSemesterId(studentId, semesterId);
  }

  public Double calculateSemesterAverage(UUID studentId, UUID semesterId) {
    Double average = gradeRepository.calculateAverageByStudentAndSemester(studentId, semesterId);
    return average != null ? average : 0.0;
  }

  public Double calculateTotalCoefficients(UUID studentId, UUID semesterId) {
    Double total = gradeRepository.calculateTotalCoefficients(studentId, semesterId);
    return total != null ? total : 0.0;
  }

  @Transactional
  public Grade updateGrade(UUID gradeId, Double newValue, UserAccount modifiedBy, String reason) {
    validateGradeValue(newValue);

    Grade grade = findById(gradeId);
    validateTeacherIsAssigned(grade, modifiedBy);

    Double oldValue = grade.getValue();

    GradeHistory history = new GradeHistory();
    history.setGrade(grade);
    history.setOldValue(oldValue);
    history.setNewValue(newValue);
    history.setChangedAt(LocalDateTime.now());
    history.setChangedBy(modifiedBy);
    history.setReason(reason);
    gradeHistoryRepository.save(history);

    grade.setValue(newValue);
    return gradeRepository.save(grade);
  }

  public void deleteGrade(UUID id) {
    gradeRepository.deleteById(id);
  }

  public List<GradeHistory> getGradeHistory(UUID gradeId) {
    return gradeHistoryRepository.findByGradeIdOrderByChangedAtDesc(gradeId);
  }

  private void validateGradeValue(Double value) {
    if (value == null) {
      throw new IllegalArgumentException("La note ne peut pas etre nulle.");
    }
    if (value < MIN_GRADE || value > MAX_GRADE) {
      throw new IllegalArgumentException(
          "La note doit etre comprise entre "
              + MIN_GRADE
              + " et "
              + MAX_GRADE
              + ", recu : "
              + value);
    }
  }

  private void validateTeacherIsAssigned(Grade grade) {
    validateTeacherIsAssigned(grade, grade.getRecordedBy());
  }

  private void validateTeacherIsAssigned(Grade grade, UserAccount actingUser) {
    if (actingUser == null) {
      throw new IllegalArgumentException(
          "Impossible de saisir une note sans utilisateur enregistreur (recordedBy).");
    }

    Exam exam = grade.getExam();
    if (exam == null || exam.getExamSession() == null) {
      throw new IllegalArgumentException(
          "La note doit etre rattachee a un examen valide avec une session d'examen.");
    }

    Optional<Teacher> teacherOpt = teacherRepository.findByUserAccountId(actingUser.getId());
    if (teacherOpt.isEmpty()) {
      throw new IllegalStateException(
          "L'utilisateur " + actingUser.getUsername() + " n'est pas un enseignant enregistre.");
    }

    Teacher teacher = teacherOpt.get();
    UUID courseId = exam.getExamSession().getCourse().getId();
    UUID groupId = exam.getExamSession().getGroup().getId();
    UUID semesterId = exam.getExamSession().getSemester().getId();

    boolean assigned =
        courseAssignmentService.isTeacherAssigned(teacher.getId(), courseId, groupId, semesterId);
    if (!assigned) {
      throw new IllegalStateException(
          "L'enseignant "
              + teacher.getReference()
              + " n'est pas affecte a ce cours/groupe/semestre et ne peut pas saisir de note.");
    }
  }
}
