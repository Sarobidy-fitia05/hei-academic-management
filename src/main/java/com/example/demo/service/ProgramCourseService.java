package com.example.demo.service;

import com.example.demo.entity.ProgramCourse;
import com.example.demo.repository.ProgramCourseRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramCourseService {

  private final ProgramCourseRepository programCourseRepository;

  public ProgramCourse save(ProgramCourse programCourse) {
    return programCourseRepository.save(programCourse);
  }

  public ProgramCourse findById(UUID id) {
    return programCourseRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("ProgramCourse not found with id: " + id));
  }

  public List<ProgramCourse> findByProgramId(UUID programId) {
    return programCourseRepository.findByProgramId(programId);
  }

  public List<ProgramCourse> findByCourseId(UUID courseId) {
    return programCourseRepository.findByCourseId(courseId);
  }

  public List<ProgramCourse> findBySemesterId(UUID semesterId) {
    return programCourseRepository.findBySemesterId(semesterId);
  }

  public List<ProgramCourse> findByProgramAndSemester(UUID programId, UUID semesterId) {
    return programCourseRepository.findByProgramIdAndSemesterId(programId, semesterId);
  }

  public ProgramCourse findByProgramCourseAndSemester(
      UUID programId, UUID courseId, UUID semesterId) {
    return programCourseRepository
        .findByProgramIdAndCourseIdAndSemesterId(programId, courseId, semesterId)
        .orElseThrow(() -> new RuntimeException("ProgramCourse not found"));
  }

  public void delete(UUID id) {
    programCourseRepository.deleteById(id);
  }
}
