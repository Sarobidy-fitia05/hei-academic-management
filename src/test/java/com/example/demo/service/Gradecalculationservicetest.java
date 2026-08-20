package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.entity.*;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GradeCalculationServiceTest {

  @Mock private GradeService gradeService;
  @Mock private CourseAttemptService courseAttemptService;
  @Mock private CourseService courseService;
  @Mock private BonusService bonusService;

  @InjectMocks private GradeCalculationService gradeCalculationService;

  private UUID studentId;
  private UUID examSessionId;
  private UUID courseAttemptId;
  private Student student;
  private ExamSession examSession;
  private CourseAttempt attempt;

  @BeforeEach
  void setUp() {
    studentId = UUID.randomUUID();
    examSessionId = UUID.randomUUID();
    courseAttemptId = UUID.randomUUID();

    student = new Student();
    student.setId(studentId);

    examSession = new ExamSession();
    examSession.setId(examSessionId);

    attempt = new CourseAttempt();
    attempt.setId(courseAttemptId);
    attempt.setStudent(student);
    attempt.setExamSession(examSession);
  }

  private Grade gradeOf(double value, double coefficient) {
    Exam exam = new Exam();
    exam.setExamSession(examSession);
    exam.setCoefficient(coefficient);

    Grade grade = new Grade();
    grade.setValue(value);
    grade.setExam(exam);
    return grade;
  }

  @Test
  void calculateAndUpdateFinalGrade_addsBonus_andCapsAtTwenty() {
    // 19.5 pondere (coef total = 1.0) + bonus 1.0 -> devrait etre plafonne a 20, pas 20.5
    List<Grade> grades = List.of(gradeOf(19.5, 1.0));

    when(courseAttemptService.findById(courseAttemptId)).thenReturn(attempt);
    when(gradeService.findByStudentId(studentId)).thenReturn(grades);
    when(bonusService.getTotalBonusForAttempt(courseAttemptId)).thenReturn(1.0);
    when(courseAttemptService.save(any(CourseAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

    CourseAttempt result = gradeCalculationService.calculateAndUpdateFinalGrade(courseAttemptId);

    assertThat(result.getFinalGrade()).isEqualTo(20.0);
    assertThat(result.getStatus()).isEqualTo(CourseAttemptStatus.PASSED);
  }

  @Test
  void calculateAndUpdateFinalGrade_setsFailed_whenBelowTen() {
    List<Grade> grades = List.of(gradeOf(8.0, 1.0));

    when(courseAttemptService.findById(courseAttemptId)).thenReturn(attempt);
    when(gradeService.findByStudentId(studentId)).thenReturn(grades);
    when(bonusService.getTotalBonusForAttempt(courseAttemptId)).thenReturn(0.0);
    when(courseAttemptService.save(any(CourseAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

    CourseAttempt result = gradeCalculationService.calculateAndUpdateFinalGrade(courseAttemptId);

    assertThat(result.getFinalGrade()).isEqualTo(8.0);
    assertThat(result.getStatus()).isEqualTo(CourseAttemptStatus.FAILED);
  }

  @Test
  void calculateAndUpdateFinalGrade_setsPassed_whenExactlyTen() {
    List<Grade> grades = List.of(gradeOf(10.0, 1.0));

    when(courseAttemptService.findById(courseAttemptId)).thenReturn(attempt);
    when(gradeService.findByStudentId(studentId)).thenReturn(grades);
    when(bonusService.getTotalBonusForAttempt(courseAttemptId)).thenReturn(0.0);
    when(courseAttemptService.save(any(CourseAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

    CourseAttempt result = gradeCalculationService.calculateAndUpdateFinalGrade(courseAttemptId);

    assertThat(result.getStatus()).isEqualTo(CourseAttemptStatus.PASSED);
  }

  @Test
  void calculateAndUpdateFinalGrade_combinesTwoExamsWithCoefficients() {
    // Exemple du sujet : 12*0.4 + 16*0.6 = 14.4, + bonus 0.5 = 14.9
    List<Grade> grades = List.of(gradeOf(12.0, 0.4), gradeOf(16.0, 0.6));

    when(courseAttemptService.findById(courseAttemptId)).thenReturn(attempt);
    when(gradeService.findByStudentId(studentId)).thenReturn(grades);
    when(bonusService.getTotalBonusForAttempt(courseAttemptId)).thenReturn(0.5);
    when(courseAttemptService.save(any(CourseAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

    CourseAttempt result = gradeCalculationService.calculateAndUpdateFinalGrade(courseAttemptId);

    assertThat(result.getFinalGrade()).isEqualTo(14.9, org.assertj.core.data.Offset.offset(0.0001));
  }

  @Test
  void calculateAndUpdateFinalGrade_doesNotUpdate_whenNoGradesForSession() {
    when(courseAttemptService.findById(courseAttemptId)).thenReturn(attempt);
    when(gradeService.findByStudentId(studentId)).thenReturn(List.of());

    CourseAttempt result = gradeCalculationService.calculateAndUpdateFinalGrade(courseAttemptId);

    assertThat(result.getFinalGrade()).isNull();
    verify(courseAttemptService, never()).save(any());
    verifyNoInteractions(bonusService);
  }
}
