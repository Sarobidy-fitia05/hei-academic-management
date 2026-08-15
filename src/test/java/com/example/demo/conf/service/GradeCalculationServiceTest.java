package com.example.demo.conf.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class GradeCalculationServiceTest {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired private GradeCalculationService gradeCalculationService;

  @Autowired private CourseService courseService;

  @Autowired private StudentService studentService;

  @Autowired private GradeService gradeService;

  @Autowired private ExamService examService;

  @Autowired private CourseAttemptService courseAttemptService;

  private Student student;
  private Course course;
  private Exam exam;

  @BeforeEach
  void setUp() {
    // Créer un étudiant
    student = new Student();
    student.setStudentReference("TEST001");
    student.setLastName("Test");
    student.setFirstName("User");
    student.setEmail("test@example.com");
    student = studentService.save(student);

    // Créer un cours
    course = new Course();
    course.setReference("MATH101");
    course.setTitle("Mathématiques");
    course.setCredits(5);
    course = courseService.save(course);

    // Créer un examen
    ExamSession session = new ExamSession();
    session.setType(ExamSessionType.NORMAL);
    session = examService.createExamSession(session);

    exam = new Exam();
    exam.setExamSession(session);
    exam.setCoefficient(1.0);
    exam.setExamDate(LocalDateTime.now());
    exam = examService.createExam(exam);
  }

  @Test
  void shouldCalculateSemesterAverage() {
    // Given
    Grade grade = new Grade();
    grade.setStudent(student);
    grade.setExam(exam);
    grade.setValue(15.0);
    grade.setRecordedBy(null); // ou un UserAccount
    gradeService.saveGrade(grade);

    Grade grade2 = new Grade();
    grade2.setStudent(student);
    grade2.setExam(exam);
    grade2.setValue(12.0);
    grade2.setRecordedBy(null);
    gradeService.saveGrade(grade2);

    // When
    Double average =
        gradeCalculationService.calculateSemesterAverage(
            student.getId(), exam.getExamSession().getSemester().getId());

    // Then
    assertThat(average).isNotNull();
    // Average should be between 12 and 15
    assertThat(average).isBetween(12.0, 15.0);
  }

  @Test
  void shouldCalculateTotalCredits() {
    // Given
    CourseAttempt attempt = new CourseAttempt();
    attempt.setStudent(student);
    attempt.setCourse(course);
    attempt.setStatus(CourseAttemptStatus.PASSED);
    attempt.setFinalGrade(14.0);
    courseAttemptService.save(attempt);

    // When
    Integer credits = gradeCalculationService.calculateTotalCredits(student.getId());

    // Then
    assertThat(credits).isEqualTo(5);
  }
}
