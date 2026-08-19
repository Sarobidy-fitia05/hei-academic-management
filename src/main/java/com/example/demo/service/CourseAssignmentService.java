package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.CourseAssignment;
import com.example.demo.entity.Group;
import com.example.demo.entity.Semester;
import com.example.demo.entity.Teacher;
import com.example.demo.repository.CourseAssignmentRepository;
import com.example.demo.repository.GroupCourseRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseAssignmentService {

  private final CourseAssignmentRepository courseAssignmentRepository;
  private final GroupCourseRepository groupCourseRepository;

  @Transactional
  public CourseAssignment assignTeacher(
      Teacher teacher, Course course, Group group, Semester semester) {

    boolean groupCourseExists =
        groupCourseRepository.existsByGroupIdAndCourseIdAndSemesterId(
            group.getId(), course.getId(), semester.getId());

    if (!groupCourseExists) {
      throw new IllegalStateException(
          "Impossible d'affecter l'enseignant : le cours "
              + course.getReference()
              + " n'est pas associe au groupe "
              + group.getReference()
              + " pour le semestre "
              + semester.getCode()
              + ". Ajoutez d'abord l'entree correspondante dans GroupCourse.");
    }

    boolean alreadyAssigned =
        courseAssignmentRepository.existsByTeacherIdAndCourseIdAndGroupIdAndSemesterId(
            teacher.getId(), course.getId(), group.getId(), semester.getId());

    if (alreadyAssigned) {
      throw new IllegalStateException(
          "L'enseignant "
              + teacher.getReference()
              + " est deja affecte a ce cours/groupe/semestre.");
    }

    CourseAssignment assignment = new CourseAssignment();
    assignment.setTeacher(teacher);
    assignment.setCourse(course);
    assignment.setGroup(group);
    assignment.setSemester(semester);

    return courseAssignmentRepository.save(assignment);
  }

  @Transactional(readOnly = true)
  public List<CourseAssignment> getAssignmentsForTeacher(UUID teacherId) {
    return courseAssignmentRepository.findByTeacherId(teacherId);
  }

  @Transactional(readOnly = true)
  public List<CourseAssignment> getAssignmentsForGroupAndSemester(UUID groupId, UUID semesterId) {
    return courseAssignmentRepository.findByGroupIdAndSemesterId(groupId, semesterId);
  }

  @Transactional(readOnly = true)
  public boolean isTeacherAssigned(UUID teacherId, UUID courseId, UUID groupId, UUID semesterId) {
    return courseAssignmentRepository.existsByTeacherIdAndCourseIdAndGroupIdAndSemesterId(
        teacherId, courseId, groupId, semesterId);
  }

  @Transactional
  public void unassignTeacher(UUID courseAssignmentId) {
    courseAssignmentRepository.deleteById(courseAssignmentId);
  }
}
