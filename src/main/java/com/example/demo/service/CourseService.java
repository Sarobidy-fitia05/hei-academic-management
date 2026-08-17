package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.repository.CourseRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

  private final CourseRepository courseRepository;

  public Course save(Course course) {
    return courseRepository.save(course);
  }

  public Course findById(UUID id) {
    return courseRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
  }

  public Course findByReference(String reference) {
    return courseRepository
        .findByReference(reference)
        .orElseThrow(() -> new RuntimeException("Course not found with reference: " + reference));
  }

  public List<Course> findByTitleContaining(String title) {
    return courseRepository.findByTitleContainingIgnoreCase(title);
  }

  public List<Course> findAll() {
    return courseRepository.findAll();
  }

  public void delete(UUID id) {
    courseRepository.deleteById(id);
  }
}
