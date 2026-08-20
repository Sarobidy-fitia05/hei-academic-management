package com.example.demo.service;

import com.example.demo.entity.Program;
import com.example.demo.entity.Semester;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentProgramHistory;
import com.example.demo.repository.StudentProgramHistoryRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentProgramHistoryService {

  private final StudentProgramHistoryRepository studentProgramHistoryRepository;

  @Transactional
  public StudentProgramHistory assignProgram(
      Student student,
      Program program,
      Semester semester,
      LocalDate startDate,
      LocalDate changeDeadline) {

    List<StudentProgramHistory> existing =
        studentProgramHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semester.getId());

    if (!existing.isEmpty()) {
      throw new IllegalStateException(
          "Un parcours est deja affecte a l'etudiant "
              + student.getStudentReference()
              + " pour le semestre "
              + semester.getCode()
              + ". Utilisez changeProgramForSemester pour le modifier avant la deadline.");
    }

    StudentProgramHistory history = new StudentProgramHistory();
    history.setStudent(student);
    history.setProgram(program);
    history.setSemester(semester);
    history.setStartDate(startDate);
    history.setChangeDeadline(changeDeadline);
    history.setLocked(false);

    return studentProgramHistoryRepository.save(history);
  }

  @Transactional
  public StudentProgramHistory changeProgramForSemester(
      Student student, Program newProgram, Semester semester, LocalDate changeDate) {

    List<StudentProgramHistory> currentList =
        studentProgramHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semester.getId());

    if (currentList.isEmpty()) {
      throw new IllegalStateException(
          "Aucun parcours n'est encore affecte a l'etudiant "
              + student.getStudentReference()
              + " pour le semestre "
              + semester.getCode()
              + ". Utilisez assignProgram pour la premiere affectation.");
    }

    StudentProgramHistory current = currentList.get(0);

    if (current.isLocked()) {
      throw new IllegalStateException(
          "Le choix de parcours de l'etudiant "
              + student.getStudentReference()
              + " est verrouille pour le semestre "
              + semester.getCode()
              + " et ne peut plus etre modifie.");
    }

    if (current.getChangeDeadline() != null && changeDate.isAfter(current.getChangeDeadline())) {
      throw new IllegalStateException(
          "La date limite de changement de parcours ("
              + current.getChangeDeadline()
              + ") est depassee pour l'etudiant "
              + student.getStudentReference()
              + " sur le semestre "
              + semester.getCode()
              + ".");
    }

    if (current.getProgram().getId().equals(newProgram.getId())) {
      return current;
    }

    current.setEndDate(changeDate);
    studentProgramHistoryRepository.save(current);

    StudentProgramHistory newHistory = new StudentProgramHistory();
    newHistory.setStudent(student);
    newHistory.setProgram(newProgram);
    newHistory.setSemester(semester);
    newHistory.setStartDate(changeDate);
    newHistory.setChangeDeadline(current.getChangeDeadline());
    newHistory.setLocked(false);

    return studentProgramHistoryRepository.save(newHistory);
  }

  @Transactional
  public StudentProgramHistory lockProgramChoice(UUID studentId, UUID semesterId) {
    List<StudentProgramHistory> currentList =
        studentProgramHistoryRepository.findByStudentIdAndSemesterId(studentId, semesterId);

    if (currentList.isEmpty()) {
      throw new IllegalStateException(
          "Aucune affectation de parcours a verrouiller pour l'etudiant "
              + studentId
              + " sur le semestre "
              + semesterId
              + ".");
    }

    StudentProgramHistory current = currentList.get(0);
    current.setLocked(true);
    return studentProgramHistoryRepository.save(current);
  }

  @Transactional(readOnly = true)
  public List<StudentProgramHistory> getHistoryForStudent(UUID studentId) {
    return studentProgramHistoryRepository.findByStudentId(studentId);
  }

  @Transactional(readOnly = true)
  public Optional<StudentProgramHistory> getProgramForStudentAndSemester(
      UUID studentId, UUID semesterId) {
    List<StudentProgramHistory> result =
        studentProgramHistoryRepository.findByStudentIdAndSemesterId(studentId, semesterId);
    return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
  }
}
