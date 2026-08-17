package com.example.demo.service;

import com.example.demo.entity.Group;
import com.example.demo.entity.Semester;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentGroupHistory;
import com.example.demo.repository.StudentGroupHistoryRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentGroupHistoryService {

  private final StudentGroupHistoryRepository studentGroupHistoryRepository;

  @Transactional
  public StudentGroupHistory assignStudentToGroup(
      Student student, Group group, Semester semester, LocalDate startDate) {
    List<StudentGroupHistory> existing =
        studentGroupHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semester.getId());

    if (!existing.isEmpty()) {
      throw new IllegalStateException(
          "Une affectation de groupe existe deja pour l'etudiant "
              + student.getStudentReference()
              + " sur le semestre "
              + semester.getCode()
              + ". Utilisez changeGroupForSemester pour la remplacer explicitement.");
    }

    StudentGroupHistory history = new StudentGroupHistory();
    history.setStudent(student);
    history.setGroup(group);
    history.setSemester(semester);
    history.setStartDate(startDate);

    return studentGroupHistoryRepository.save(history);
  }

  @Transactional
  public StudentGroupHistory changeGroupForSemester(
      Student student, Group newGroup, Semester semester, LocalDate changeDate) {
    List<StudentGroupHistory> currentList =
        studentGroupHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semester.getId());

    if (currentList.isEmpty()) {
      return assignStudentToGroup(student, newGroup, semester, changeDate);
    }

    StudentGroupHistory current = currentList.get(0);

    if (current.getGroup().getId().equals(newGroup.getId())) {
      return current;
    }

    current.setEndDate(changeDate);
    studentGroupHistoryRepository.save(current);

    StudentGroupHistory newHistory = new StudentGroupHistory();
    newHistory.setStudent(student);
    newHistory.setGroup(newGroup);
    newHistory.setSemester(semester);
    newHistory.setStartDate(changeDate);

    return studentGroupHistoryRepository.save(newHistory);
  }

  @Transactional(readOnly = true)
  public List<StudentGroupHistory> getHistoryForStudent(UUID studentId) {
    return studentGroupHistoryRepository.findByStudentIdOrderBySemester_SemesterNumberAsc(
        studentId);
  }

  @Transactional(readOnly = true)
  public Optional<StudentGroupHistory> getGroupForStudentAndSemester(
      UUID studentId, UUID semesterId) {
    List<StudentGroupHistory> result =
        studentGroupHistoryRepository.findByStudentIdAndSemesterId(studentId, semesterId);
    return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
  }
}
