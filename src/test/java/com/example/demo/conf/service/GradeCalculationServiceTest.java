package com.example.demo.conf.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.entity.*;
import com.example.demo.repository.AcademicYearRepository;
import com.example.demo.repository.PromotionRepository;
import com.example.demo.repository.SemesterRepository;
import com.example.demo.service.*;
import java.time.LocalDate;
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

  @Autowired private PromotionRepository promotionRepository;

  @Autowired private AcademicYearRepository academicYearRepository;

  @Autowired private SemesterRepository semesterRepository;

  private Student student;
  private Course course;
  private Exam exam;
  private Semester semester;

  @BeforeEach
  void setUp() {
    student = new Student();
    student.setStudentReference("TEST001");
    student.setLastName("Test");
    student.setFirstName("User");
    student.setEmail("test@example.com");
    student = studentService.save(student);

    course = new Course();
    course.setReference("MATH101");
    course.setTitle("Mathématiques");
    course.setCredits(5);
    course = courseService.save(course);

    Promotion promotion = new Promotion();
    promotion.setYear(2024);
    promotion.setGroupPrefix("M1");
    promotion = promotionRepository.save(promotion);

    AcademicYear academicYear = new AcademicYear();
    academicYear.setLabel("2024-2025");
    academicYear.setStartDate(LocalDate.of(2024, 9, 1));
    academicYear.setEndDate(LocalDate.of(2025, 6, 30));
    academicYear.setPromotion(promotion);
    academicYear = academicYearRepository.save(academicYear);

    semester = new Semester();
    semester.setCode("S1");
    semester.setSemesterNumber(1);
    semester.setStartDate(LocalDate.of(2024, 9, 1));
    semester.setEndDate(LocalDate.of(2025, 1, 31));
    semester.setAcademicYear(academicYear);
    semester = semesterRepository.save(semester);

    ExamSession session = new ExamSession();
    session.setType(ExamSessionType.NORMAL);
    session.setSemester(semester);
    session = examService.createExamSession(session);

    exam = new Exam();
    exam.setExamSession(session);
    exam.setCoefficient(1.0);
    exam.setExamDate(LocalDateTime.now());
    exam = examService.createExam(exam);
  }

  @Test
  void shouldCalculateSemesterAverage() {

    Grade grade = new Grade();
    grade.setStudent(student);
    grade.setExam(exam);
    grade.setValue(15.0);
    grade.setRecordedBy(null);
    gradeService.saveGrade(grade);

    Grade grade2 = new Grade();
    grade2.setStudent(student);
    grade2.setExam(exam);
    grade2.setValue(12.0);
    grade2.setRecordedBy(null);
    gradeService.saveGrade(grade2);

    Double average =
        gradeCalculationService.calculateSemesterAverage(student.getId(), semester.getId());

    assertThat(average).isNotNull();
    assertThat(average).isBetween(12.0, 15.0);
  }

  @Test
  void shouldCalculateTotalCredits() {

    CourseAttempt attempt = new CourseAttempt();
    attempt.setStudent(student);
    attempt.setCourse(course);
    attempt.setStatus(CourseAttemptStatus.PASSED);
    attempt.setFinalGrade(14.0);
    courseAttemptService.save(attempt);

    Integer credits = gradeCalculationService.calculateTotalCredits(student.getId());

    assertThat(credits).isEqualTo(5);
  }
}
