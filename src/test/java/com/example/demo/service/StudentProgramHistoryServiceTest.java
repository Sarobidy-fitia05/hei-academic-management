package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.entity.Program;
import com.example.demo.entity.ProgramCode;
import com.example.demo.entity.Semester;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentProgramHistory;
import com.example.demo.repository.StudentProgramHistoryRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentProgramHistoryServiceTest {

  @Mock private StudentProgramHistoryRepository studentProgramHistoryRepository;

  @InjectMocks private StudentProgramHistoryService studentProgramHistoryService;

  private Student student;
  private Program commonProgram;
  private Program tnProgram;
  private Program elProgram;
  private Semester semesterS4;

  @BeforeEach
  void setUp() {
    student = new Student();
    student.setId(UUID.randomUUID());
    student.setStudentReference("STD24001");

    commonProgram = new Program();
    commonProgram.setId(UUID.randomUUID());
    commonProgram.setCode(ProgramCode.COMMON);

    tnProgram = new Program();
    tnProgram.setId(UUID.randomUUID());
    tnProgram.setCode(ProgramCode.TN);

    elProgram = new Program();
    elProgram.setId(UUID.randomUUID());
    elProgram.setCode(ProgramCode.EL);

    semesterS4 = new Semester();
    semesterS4.setId(UUID.randomUUID());
    semesterS4.setCode("S4");
  }

  // ============================================================
  // assignProgram
  // ============================================================

  @Test
  void assignProgram_throwsException_whenAlreadyAssignedForSemester() {
    StudentProgramHistory existing = new StudentProgramHistory();
    when(studentProgramHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semesterS4.getId()))
        .thenReturn(List.of(existing));

    assertThatThrownBy(
            () ->
                studentProgramHistoryService.assignProgram(
                    student,
                    tnProgram,
                    semesterS4,
                    LocalDate.of(2026, 5, 1),
                    LocalDate.of(2026, 5, 31)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("deja affecte");

    verify(studentProgramHistoryRepository, never()).save(any());
  }

  @Test
  void assignProgram_succeeds_whenNoExistingHistory() {
    when(studentProgramHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semesterS4.getId()))
        .thenReturn(List.of());
    when(studentProgramHistoryRepository.save(any(StudentProgramHistory.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    StudentProgramHistory result =
        studentProgramHistoryService.assignProgram(
            student, tnProgram, semesterS4, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31));

    assertThat(result.getProgram()).isEqualTo(tnProgram);
    assertThat(result.isLocked()).isFalse();
    assertThat(result.getChangeDeadline()).isEqualTo(LocalDate.of(2026, 5, 31));
  }

  // ============================================================
  // changeProgramForSemester
  // ============================================================

  @Test
  void changeProgram_throwsException_whenNoCurrentAssignment() {
    when(studentProgramHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semesterS4.getId()))
        .thenReturn(List.of());

    assertThatThrownBy(
            () ->
                studentProgramHistoryService.changeProgramForSemester(
                    student, elProgram, semesterS4, LocalDate.of(2026, 5, 15)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Aucun parcours");
  }

  @Test
  void changeProgram_throwsException_whenLocked() {
    StudentProgramHistory current = new StudentProgramHistory();
    current.setProgram(tnProgram);
    current.setLocked(true);
    current.setChangeDeadline(LocalDate.of(2026, 5, 31));

    when(studentProgramHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semesterS4.getId()))
        .thenReturn(List.of(current));

    assertThatThrownBy(
            () ->
                studentProgramHistoryService.changeProgramForSemester(
                    student, elProgram, semesterS4, LocalDate.of(2026, 5, 15)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("verrouille");

    verify(studentProgramHistoryRepository, never()).save(any());
  }

  @Test
  void changeProgram_throwsException_whenPastDeadline() {
    // Exemple exact du sujet : deadline 31 mai, tentative de changement le 1er juin
    StudentProgramHistory current = new StudentProgramHistory();
    current.setProgram(tnProgram);
    current.setLocked(false);
    current.setChangeDeadline(LocalDate.of(2026, 5, 31));

    when(studentProgramHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semesterS4.getId()))
        .thenReturn(List.of(current));

    assertThatThrownBy(
            () ->
                studentProgramHistoryService.changeProgramForSemester(
                    student, elProgram, semesterS4, LocalDate.of(2026, 6, 1)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("depassee");

    verify(studentProgramHistoryRepository, never()).save(any());
  }

  @Test
  void changeProgram_succeeds_whenWithinDeadlineAndNotLocked() {
    // Exemple du sujet : choix TN le 1er mai, changement vers EL pendant le mois de mai
    StudentProgramHistory current = new StudentProgramHistory();
    current.setId(UUID.randomUUID());
    current.setStudent(student);
    current.setProgram(tnProgram);
    current.setSemester(semesterS4);
    current.setStartDate(LocalDate.of(2026, 5, 1));
    current.setChangeDeadline(LocalDate.of(2026, 5, 31));
    current.setLocked(false);

    when(studentProgramHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semesterS4.getId()))
        .thenReturn(List.of(current));
    when(studentProgramHistoryRepository.save(any(StudentProgramHistory.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    LocalDate changeDate = LocalDate.of(2026, 5, 15);
    StudentProgramHistory result =
        studentProgramHistoryService.changeProgramForSemester(
            student, elProgram, semesterS4, changeDate);

    // L'ancienne ligne est cloturee, jamais supprimee
    assertThat(current.getEndDate()).isEqualTo(changeDate);
    verify(studentProgramHistoryRepository, never()).delete(any());
    verify(studentProgramHistoryRepository, never()).deleteById(any());

    // La nouvelle ligne pointe vers EL et conserve la meme deadline
    assertThat(result.getProgram()).isEqualTo(elProgram);
    assertThat(result.getStartDate()).isEqualTo(changeDate);
    assertThat(result.getChangeDeadline()).isEqualTo(LocalDate.of(2026, 5, 31));
    assertThat(result.isLocked()).isFalse();
  }

  @Test
  void changeProgram_returnsExistingUnchanged_whenSameProgramRequested() {
    StudentProgramHistory current = new StudentProgramHistory();
    current.setProgram(tnProgram);
    current.setLocked(false);
    current.setChangeDeadline(LocalDate.of(2026, 5, 31));

    when(studentProgramHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semesterS4.getId()))
        .thenReturn(List.of(current));

    StudentProgramHistory result =
        studentProgramHistoryService.changeProgramForSemester(
            student, tnProgram, semesterS4, LocalDate.of(2026, 5, 10));

    assertThat(result).isSameAs(current);
    verify(studentProgramHistoryRepository, never()).save(any());
  }

  @Test
  void changeProgram_succeeds_whenChangeDateIsExactlyTheDeadline() {
    // Cas limite : le dernier jour autorise (31 mai) doit encore fonctionner
    StudentProgramHistory current = new StudentProgramHistory();
    current.setProgram(tnProgram);
    current.setLocked(false);
    current.setChangeDeadline(LocalDate.of(2026, 5, 31));

    when(studentProgramHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semesterS4.getId()))
        .thenReturn(List.of(current));
    when(studentProgramHistoryRepository.save(any(StudentProgramHistory.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    StudentProgramHistory result =
        studentProgramHistoryService.changeProgramForSemester(
            student, elProgram, semesterS4, LocalDate.of(2026, 5, 31));

    assertThat(result.getProgram()).isEqualTo(elProgram);
  }

  // ============================================================
  // lockProgramChoice
  // ============================================================

  @Test
  void lockProgramChoice_setsLockedTrue() {
    StudentProgramHistory current = new StudentProgramHistory();
    current.setLocked(false);

    when(studentProgramHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semesterS4.getId()))
        .thenReturn(List.of(current));
    when(studentProgramHistoryRepository.save(any(StudentProgramHistory.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    StudentProgramHistory result =
        studentProgramHistoryService.lockProgramChoice(student.getId(), semesterS4.getId());

    assertThat(result.isLocked()).isTrue();
  }

  @Test
  void lockProgramChoice_throwsException_whenNoAssignmentExists() {
    when(studentProgramHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semesterS4.getId()))
        .thenReturn(List.of());

    assertThatThrownBy(
            () ->
                studentProgramHistoryService.lockProgramChoice(student.getId(), semesterS4.getId()))
        .isInstanceOf(IllegalStateException.class);
  }
}
