package com.example.demo.repository;

import com.example.demo.entity.StudentProgramHistory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentProgramHistoryRepository
    extends JpaRepository<StudentProgramHistory, UUID> {
  List<StudentProgramHistory> findByStudentId(UUID studentId);

  List<StudentProgramHistory> findByStudentIdAndSemesterId(UUID studentId, UUID semesterId);

  List<StudentProgramHistory> findByProgramId(UUID programId);

  Optional<StudentProgramHistory> findByStudentIdAndSemesterIdAndProgramId(
      UUID studentId, UUID semesterId, UUID programId);

  List<StudentProgramHistory> findByStudentIdAndLocked(UUID studentId, boolean locked);
}
