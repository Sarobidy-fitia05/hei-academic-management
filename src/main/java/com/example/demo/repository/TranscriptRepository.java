package com.example.demo.repository;

import com.example.demo.entity.Transcript;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, UUID> {
  List<Transcript> findByStudentId(UUID studentId);

  List<Transcript> findByStudentIdAndType(UUID studentId, String type);

  List<Transcript> findByAcademicYearId(UUID academicYearId);

  List<Transcript> findBySemesterId(UUID semesterId);

  Optional<Transcript> findByStudentIdAndSemesterId(UUID studentId, UUID semesterId);

  List<Transcript> findByStudentIdAndAcademicYearId(UUID studentId, UUID academicYearId);

  List<Transcript> findByStudentIdAndStatus(UUID studentId, String status);
}
