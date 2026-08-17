package com.example.demo.repository;

import com.example.demo.entity.GroupCourse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupCourseRepository extends JpaRepository<GroupCourse, UUID> {
  List<GroupCourse> findByGroupId(UUID groupId);

  List<GroupCourse> findByCourseId(UUID courseId);

  List<GroupCourse> findBySemesterId(UUID semesterId);

  Optional<GroupCourse> findByGroupIdAndCourseIdAndSemesterId(
      UUID groupId, UUID courseId, UUID semesterId);

  List<GroupCourse> findByGroupIdAndSemesterId(UUID groupId, UUID semesterId);

  boolean existsByGroupIdAndCourseIdAndSemesterId(UUID groupId, UUID courseId, UUID semesterId);
}
