package com.example.demo.repository;

import com.example.demo.entity.ProgramCourse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramCourseRepository extends JpaRepository<ProgramCourse, UUID> {
  List<ProgramCourse> findByProgramId(UUID programId);

  List<ProgramCourse> findByCourseId(UUID courseId);

  List<ProgramCourse> findBySemesterId(UUID semesterId);

  Optional<ProgramCourse> findByProgramIdAndCourseIdAndSemesterId(
      UUID programId, UUID courseId, UUID semesterId);

  List<ProgramCourse> findByProgramIdAndSemesterId(UUID programId, UUID semesterId);
}
