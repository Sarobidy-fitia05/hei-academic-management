package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.entity.*;
import com.example.demo.repository.GradeHistoryRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.TeacherRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

  @Mock private GradeRepository gradeRepository;
  @Mock private GradeHistoryRepository gradeHistoryRepository;
  @Mock private TeacherRepository teacherRepository;
  @Mock private CourseAssignmentService courseAssignmentService;

  @InjectMocks private GradeService gradeService;

  private UUID courseId;
  private UUID groupId;
  private UUID semesterId;
  private UUID teacherId;
  private Teacher teacher;
  private UserAccount teacherAccount;
  private Grade grade;

  @BeforeEach
  void setUp() {
    courseId = UUID.randomUUID();
    groupId = UUID.randomUUID();
    semesterId = UUID.randomUUID();
    teacherId = UUID.randomUUID();

    Course course = new Course();
    course.setId(courseId);

    Group group = new Group();
    group.setId(groupId);

    Semester semester = new Semester();
    semester.setId(semesterId);

    ExamSession examSession = new ExamSession();
    examSession.setCourse(course);
    examSession.setGroup(group);
    examSession.setSemester(semester);

    Exam exam = new Exam();
    exam.setExamSession(examSession);
    exam.setCoefficient(1.0);

    teacherAccount = new UserAccount();
    teacherAccount.setId(UUID.randomUUID());
    teacherAccount.setUsername("prof.dupont");

    teacher = new Teacher();
    teacher.setId(teacherId);
    teacher.setReference("TCH001");
    teacher.setUserAccount(teacherAccount);

    Student student = new Student();
    student.setId(UUID.randomUUID());

    grade = new Grade();
    grade.setId(UUID.randomUUID());
    grade.setExam(exam);
    grade.setStudent(student);
    grade.setRecordedBy(teacherAccount);
    grade.setValue(15.0);
  }

  @Test
  void saveGrade_throwsException_whenValueBelowZero() {
    grade.setValue(-1.0);

    assertThatThrownBy(() -> gradeService.saveGrade(grade))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("comprise entre");

    verifyNoInteractions(gradeRepository);
  }

  @Test
  void saveGrade_throwsException_whenValueAboveTwenty() {
    grade.setValue(21.0);

    assertThatThrownBy(() -> gradeService.saveGrade(grade))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("comprise entre");
  }

  @Test
  void saveGrade_throwsException_whenValueIsNull() {
    grade.setValue(null);

    assertThatThrownBy(() -> gradeService.saveGrade(grade))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void saveGrade_throwsException_whenRecordedByIsNull() {
    grade.setRecordedBy(null);

    assertThatThrownBy(() -> gradeService.saveGrade(grade))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("recordedBy");
  }

  @Test
  void saveGrade_throwsException_whenUserIsNotATeacher() {
    when(teacherRepository.findByUserAccountId(teacherAccount.getId()))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> gradeService.saveGrade(grade))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("n'est pas un enseignant");
  }

  @Test
  void saveGrade_throwsException_whenTeacherNotAssignedToCourseGroupSemester() {
    when(teacherRepository.findByUserAccountId(teacherAccount.getId()))
        .thenReturn(Optional.of(teacher));
    when(courseAssignmentService.isTeacherAssigned(teacherId, courseId, groupId, semesterId))
        .thenReturn(false);

    assertThatThrownBy(() -> gradeService.saveGrade(grade))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("n'est pas affecte");

    verify(gradeRepository, never()).save(any());
  }

  @Test
  void saveGrade_succeeds_whenValueValidAndTeacherAssigned() {
    when(teacherRepository.findByUserAccountId(teacherAccount.getId()))
        .thenReturn(Optional.of(teacher));
    when(courseAssignmentService.isTeacherAssigned(teacherId, courseId, groupId, semesterId))
        .thenReturn(true);
    when(gradeRepository.save(any(Grade.class))).thenAnswer(inv -> inv.getArgument(0));

    Grade saved = gradeService.saveGrade(grade);

    assertThat(saved.getRecordedAt()).isNotNull();
    verify(gradeRepository).save(grade);
  }

  @Test
  void updateGrade_throwsException_whenNewValueOutOfRange() {
    assertThatThrownBy(
            () -> gradeService.updateGrade(grade.getId(), 25.0, teacherAccount, "erreur"))
        .isInstanceOf(IllegalArgumentException.class);

    verifyNoInteractions(gradeHistoryRepository);
  }

  @Test
  void updateGrade_createsHistoryEntry_beforeOverwritingValue() {
    when(gradeRepository.findById(grade.getId())).thenReturn(Optional.of(grade));
    when(teacherRepository.findByUserAccountId(teacherAccount.getId()))
        .thenReturn(Optional.of(teacher));
    when(courseAssignmentService.isTeacherAssigned(teacherId, courseId, groupId, semesterId))
        .thenReturn(true);
    when(gradeRepository.save(any(Grade.class))).thenAnswer(inv -> inv.getArgument(0));

    Double oldValue = grade.getValue();

    gradeService.updateGrade(grade.getId(), 18.0, teacherAccount, "reclamation");

    ArgumentCaptor<GradeHistory> historyCaptor = ArgumentCaptor.forClass(GradeHistory.class);
    verify(gradeHistoryRepository).save(historyCaptor.capture());

    GradeHistory savedHistory = historyCaptor.getValue();
    assertThat(savedHistory.getOldValue()).isEqualTo(oldValue);
    assertThat(savedHistory.getNewValue()).isEqualTo(18.0);
    assertThat(savedHistory.getReason()).isEqualTo("reclamation");
    assertThat(grade.getValue()).isEqualTo(18.0);
  }
}
