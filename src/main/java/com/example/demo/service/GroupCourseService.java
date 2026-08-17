package com.example.demo.service;

import com.example.demo.entity.GroupCourse;
import com.example.demo.repository.GroupCourseRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupCourseService {

  private final GroupCourseRepository groupCourseRepository;

  public GroupCourse save(GroupCourse groupCourse) {
    return groupCourseRepository.save(groupCourse);
  }

  public GroupCourse findById(UUID id) {
    return groupCourseRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("GroupCourse not found with id: " + id));
  }

  public List<GroupCourse> findByGroupId(UUID groupId) {
    return groupCourseRepository.findByGroupId(groupId);
  }

  public List<GroupCourse> findByCourseId(UUID courseId) {
    return groupCourseRepository.findByCourseId(courseId);
  }

  public List<GroupCourse> findBySemesterId(UUID semesterId) {
    return groupCourseRepository.findBySemesterId(semesterId);
  }

  public List<GroupCourse> findByGroupAndSemester(UUID groupId, UUID semesterId) {
    return groupCourseRepository.findByGroupIdAndSemesterId(groupId, semesterId);
  }

  public GroupCourse findByGroupCourseAndSemester(UUID groupId, UUID courseId, UUID semesterId) {
    return groupCourseRepository
        .findByGroupIdAndCourseIdAndSemesterId(groupId, courseId, semesterId)
        .orElseThrow(() -> new RuntimeException("GroupCourse not found"));
  }

  public void delete(UUID id) {
    groupCourseRepository.deleteById(id);
  }
}
