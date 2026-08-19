package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.entity.Group;
import com.example.demo.entity.Semester;
import com.example.demo.entity.Student;
import com.example.demo.entity.StudentGroupHistory;
import com.example.demo.repository.StudentGroupHistoryRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentGroupHistoryServiceTest {

  @Mock private StudentGroupHistoryRepository studentGroupHistoryRepository;

  @InjectMocks private StudentGroupHistoryService studentGroupHistoryService;

  private Student student;
  private Group groupK1;
  private Group groupK3;
  private Semester semester;

  @BeforeEach
  void setUp() {
    student = new Student();
    student.setId(UUID.randomUUID());
    student.setStudentReference("STD24001");

    groupK1 = new Group();
    groupK1.setId(UUID.randomUUID());
    groupK1.setReference("K1");

    groupK3 = new Group();
    groupK3.setId(UUID.randomUUID());
    groupK3.setReference("K3");

    semester = new Semester();
    semester.setId(UUID.randomUUID());
    semester.setCode("S3");
  }

  @Test
  void assignStudentToGroup_throwsException_whenHistoryAlreadyExistsForSemester() {
    StudentGroupHistory existing = new StudentGroupHistory();
    when(studentGroupHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semester.getId()))
        .thenReturn(List.of(existing));

    assertThatThrownBy(
            () ->
                studentGroupHistoryService.assignStudentToGroup(
                    student, groupK1, semester, LocalDate.of(2026, 1, 1)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("existe deja");

    verify(studentGroupHistoryRepository, never()).save(any());
  }

  @Test
  void assignStudentToGroup_succeeds_whenNoExistingHistory() {
    when(studentGroupHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semester.getId()))
        .thenReturn(List.of());
    when(studentGroupHistoryRepository.save(any(StudentGroupHistory.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    StudentGroupHistory result =
        studentGroupHistoryService.assignStudentToGroup(
            student, groupK1, semester, LocalDate.of(2026, 1, 1));

    assertThat(result.getGroup()).isEqualTo(groupK1);
    assertThat(result.getEndDate()).isNull();
  }

  @Test
  void changeGroupForSemester_closesOldHistory_andCreatesNewOne_withoutDeletingOld() {
    StudentGroupHistory current = new StudentGroupHistory();
    current.setId(UUID.randomUUID());
    current.setStudent(student);
    current.setGroup(groupK1);
    current.setSemester(semester);
    current.setStartDate(LocalDate.of(2026, 1, 1));

    when(studentGroupHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semester.getId()))
        .thenReturn(List.of(current));
    when(studentGroupHistoryRepository.save(any(StudentGroupHistory.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    LocalDate changeDate = LocalDate.of(2026, 3, 1);
    StudentGroupHistory result =
        studentGroupHistoryService.changeGroupForSemester(student, groupK3, semester, changeDate);

    // L'ancienne ligne doit etre cloturee, jamais supprimee
    assertThat(current.getEndDate()).isEqualTo(changeDate);
    verify(studentGroupHistoryRepository, never()).delete(any());
    verify(studentGroupHistoryRepository, never()).deleteById(any());

    // La nouvelle ligne pointe vers le nouveau groupe
    assertThat(result.getGroup()).isEqualTo(groupK3);
    assertThat(result.getStartDate()).isEqualTo(changeDate);

    ArgumentCaptor<StudentGroupHistory> captor = ArgumentCaptor.forClass(StudentGroupHistory.class);
    verify(studentGroupHistoryRepository, times(2)).save(captor.capture());
  }

  @Test
  void changeGroupForSemester_returnsExistingUnchanged_whenGroupIsTheSame() {
    StudentGroupHistory current = new StudentGroupHistory();
    current.setStudent(student);
    current.setGroup(groupK1);
    current.setSemester(semester);

    when(studentGroupHistoryRepository.findByStudentIdAndSemesterId(
            student.getId(), semester.getId()))
        .thenReturn(List.of(current));

    StudentGroupHistory result =
        studentGroupHistoryService.changeGroupForSemester(
            student, groupK1, semester, LocalDate.of(2026, 3, 1));

    assertThat(result).isSameAs(current);
    verify(studentGroupHistoryRepository, never()).save(any());
  }
}
