package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.entity.*;
import com.example.demo.repository.CourseAssignmentRepository;
import com.example.demo.repository.GroupCourseRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseAssignmentServiceTest {

  @Mock private CourseAssignmentRepository courseAssignmentRepository;
  @Mock private GroupCourseRepository groupCourseRepository;

  @InjectMocks private CourseAssignmentService courseAssignmentService;

  private Teacher teacher;
  private Course course;
  private Group group;
  private Semester semester;

  @BeforeEach
  void setUp() {
    teacher = new Teacher();
    teacher.setId(UUID.randomUUID());
    teacher.setReference("TCH001");

    course = new Course();
    course.setId(UUID.randomUUID());
    course.setReference("PROG1");

    group = new Group();
    group.setId(UUID.randomUUID());
    group.setReference("K1");

    semester = new Semester();
    semester.setId(UUID.randomUUID());
    semester.setCode("S1");
  }

  @Test
  void assignTeacher_throwsException_whenGroupCourseDoesNotExist() {
    when(groupCourseRepository.existsByGroupIdAndCourseIdAndSemesterId(
            group.getId(), course.getId(), semester.getId()))
        .thenReturn(false);

    assertThatThrownBy(
            () -> courseAssignmentService.assignTeacher(teacher, course, group, semester))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("GroupCourse");

    verify(courseAssignmentRepository, never()).save(any());
  }

  @Test
  void assignTeacher_throwsException_whenAlreadyAssigned() {
    when(groupCourseRepository.existsByGroupIdAndCourseIdAndSemesterId(
            group.getId(), course.getId(), semester.getId()))
        .thenReturn(true);
    when(courseAssignmentRepository.existsByTeacherIdAndCourseIdAndGroupIdAndSemesterId(
            teacher.getId(), course.getId(), group.getId(), semester.getId()))
        .thenReturn(true);

    assertThatThrownBy(
            () -> courseAssignmentService.assignTeacher(teacher, course, group, semester))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("deja affecte");
  }

  @Test
  void assignTeacher_succeeds_whenGroupCourseExistsAndNotAlreadyAssigned() {
    when(groupCourseRepository.existsByGroupIdAndCourseIdAndSemesterId(
            group.getId(), course.getId(), semester.getId()))
        .thenReturn(true);
    when(courseAssignmentRepository.existsByTeacherIdAndCourseIdAndGroupIdAndSemesterId(
            teacher.getId(), course.getId(), group.getId(), semester.getId()))
        .thenReturn(false);
    when(courseAssignmentRepository.save(any(CourseAssignment.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    CourseAssignment result =
        courseAssignmentService.assignTeacher(teacher, course, group, semester);

    assertThat(result.getTeacher()).isEqualTo(teacher);
    assertThat(result.getCourse()).isEqualTo(course);
    assertThat(result.getGroup()).isEqualTo(group);
    assertThat(result.getSemester()).isEqualTo(semester);
  }

  @Test
  void isTeacherAssigned_delegatesToRepository() {
    UUID teacherId = teacher.getId();
    UUID courseId = course.getId();
    UUID groupId = group.getId();
    UUID semesterId = semester.getId();

    when(courseAssignmentRepository.existsByTeacherIdAndCourseIdAndGroupIdAndSemesterId(
            teacherId, courseId, groupId, semesterId))
        .thenReturn(true);

    boolean result =
        courseAssignmentService.isTeacherAssigned(teacherId, courseId, groupId, semesterId);

    assertThat(result).isTrue();
  }
}
