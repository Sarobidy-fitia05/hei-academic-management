package com.example.demo.service;

import com.example.demo.entity.Grade;
import com.example.demo.entity.GradeHistory;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.GradeHistoryRepository;
import com.example.demo.repository.GradeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GradeService {

  private final GradeRepository gradeRepository;
  private final GradeHistoryRepository gradeHistoryRepository;

  public Grade saveGrade(Grade grade) {
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
    Grade grade = findById(gradeId);
    Double oldValue = grade.getValue();

    // Create history entry
    GradeHistory history = new GradeHistory();
    history.setGrade(grade);
    history.setOldValue(oldValue);
    history.setNewValue(newValue);
    history.setChangedAt(LocalDateTime.now());
    history.setChangedBy(modifiedBy);
    history.setReason(reason);
    gradeHistoryRepository.save(history);

    // Update grade
    grade.setValue(newValue);
    return gradeRepository.save(grade);
  }

  public void deleteGrade(UUID id) {
    gradeRepository.deleteById(id);
  }

  public List<GradeHistory> getGradeHistory(UUID gradeId) {
    return gradeHistoryRepository.findByGradeIdOrderByChangedAtDesc(gradeId);
  }
}
