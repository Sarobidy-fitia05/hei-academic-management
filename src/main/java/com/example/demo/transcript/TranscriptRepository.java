package com.example.demo.transcript;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptRepository extends JpaRepository<Transcript, Long> {

  Optional<Transcript> findTopByStudentIdOrderByAcademicYearDesc(Long studentId);

  Optional<Transcript> findByStudentIdAndAcademicYear(Long studentId, Integer academicYear);
}
