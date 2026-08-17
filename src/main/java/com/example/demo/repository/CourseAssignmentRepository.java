package com.example.demo.repository;

import com.example.demo.entity.CourseAssignment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, UUID> {
  List<CourseAssignment> findByTeacherId(UUID teacherId);

  List<CourseAssignment> findByCourseId(UUID courseId);

  List<CourseAssignment> findByGroupId(UUID groupId);

  List<CourseAssignment> findBySemesterId(UUID semesterId);

  Optional<CourseAssignment> findByTeacherIdAndCourseIdAndGroupIdAndSemesterId(
      UUID teacherId, UUID courseId, UUID groupId, UUID semesterId);

  List<CourseAssignment> findByTeacherIdAndSemesterId(UUID teacherId, UUID semesterId);

  List<CourseAssignment> findByGroupIdAndSemesterId(UUID groupId, UUID semesterId);

  boolean existsByTeacherIdAndCourseIdAndGroupIdAndSemesterId(
      UUID teacherId, UUID courseId, UUID groupId, UUID semesterId);
}
