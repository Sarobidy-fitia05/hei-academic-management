package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.entity.Course;
import com.example.demo.entity.CourseAttempt;
import com.example.demo.entity.ExamSession;
import com.example.demo.entity.Group;
import com.example.demo.entity.Student;
import com.example.demo.repository.CourseAttemptRepository;
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
class CourseAttemptServiceTest {

  @Mock private CourseAttemptRepository courseAttemptRepository;

  @InjectMocks private CourseAttemptService courseAttemptService;

  private Student student;
  private Course course;
  private ExamSession examSession;
  private Group group;

  @BeforeEach
  void setUp() {
    student = new Student();
    student.setId(UUID.randomUUID());

    course = new Course();
    course.setId(UUID.randomUUID());

    examSession = new ExamSession();
    examSession.setId(UUID.randomUUID());

    group = new Group();
    group.setId(UUID.randomUUID());
  }

  @Test
  void createNextAttempt_startsAtOne_whenNoPreviousAttempts() {
    when(courseAttemptRepository.findByStudentIdAndCourseId(student.getId(), course.getId()))
        .thenReturn(List.of());
    when(courseAttemptRepository.save(any(CourseAttempt.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    CourseAttempt result =
        courseAttemptService.createNextAttempt(student, course, examSession, group);

    assertThat(result.getAttemptNumber()).isEqualTo(1);
  }

  @Test
  void createNextAttempt_incrementsFromExistingMax() {
    // Etudiant a echoue une premiere fois (attempt #1), puis rattrapage (attempt #2)
    CourseAttempt attempt1 = new CourseAttempt();
    attempt1.setAttemptNumber(1);
    CourseAttempt attempt2 = new CourseAttempt();
    attempt2.setAttemptNumber(2);

    when(courseAttemptRepository.findByStudentIdAndCourseId(student.getId(), course.getId()))
        .thenReturn(List.of(attempt1, attempt2));
    when(courseAttemptRepository.save(any(CourseAttempt.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    CourseAttempt result =
        courseAttemptService.createNextAttempt(student, course, examSession, group);

    assertThat(result.getAttemptNumber()).isEqualTo(3);
  }

  @Test
  void createNextAttempt_neverDeletesOrModifiesPreviousAttempts() {
    CourseAttempt attempt1 = new CourseAttempt();
    attempt1.setAttemptNumber(1);

    when(courseAttemptRepository.findByStudentIdAndCourseId(student.getId(), course.getId()))
        .thenReturn(List.of(attempt1));
    when(courseAttemptRepository.save(any(CourseAttempt.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    courseAttemptService.createNextAttempt(student, course, examSession, group);

    verify(courseAttemptRepository, never()).delete(any());
    verify(courseAttemptRepository, never()).deleteById(any());
    // attempt1 lui-meme n'est jamais passe a save()
    ArgumentCaptor<CourseAttempt> captor = ArgumentCaptor.forClass(CourseAttempt.class);
    verify(courseAttemptRepository).save(captor.capture());
    assertThat(captor.getValue()).isNotSameAs(attempt1);
  }

  @Test
  void createNextAttempt_setsAllRelations() {
    when(courseAttemptRepository.findByStudentIdAndCourseId(student.getId(), course.getId()))
        .thenReturn(List.of());
    when(courseAttemptRepository.save(any(CourseAttempt.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    CourseAttempt result =
        courseAttemptService.createNextAttempt(student, course, examSession, group);

    assertThat(result.getStudent()).isEqualTo(student);
    assertThat(result.getCourse()).isEqualTo(course);
    assertThat(result.getExamSession()).isEqualTo(examSession);
    assertThat(result.getGroup()).isEqualTo(group);
  }

  @Test
  void findBySemester_delegatesToExamSessionSemesterQuery() {
    UUID semesterId = UUID.randomUUID();
    CourseAttempt attempt = new CourseAttempt();
    when(courseAttemptRepository.findByExamSessionSemesterId(semesterId))
        .thenReturn(List.of(attempt));

    List<CourseAttempt> result = courseAttemptService.findBySemester(semesterId);

    assertThat(result).hasSize(1);
    verify(courseAttemptRepository).findByExamSessionSemesterId(semesterId);
  }
}
