package com.example.demo.repository;

import com.example.demo.entity.StudentGroupHistory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentGroupHistoryRepository extends JpaRepository<StudentGroupHistory, UUID> {
  List<StudentGroupHistory> findByStudentId(UUID studentId);

  List<StudentGroupHistory> findByStudentIdAndSemesterId(UUID studentId, UUID semesterId);

  List<StudentGroupHistory> findByStudentIdOrderBySemester_SemesterNumberAsc(UUID studentId);

  List<StudentGroupHistory> findByGroupId(UUID groupId);

  Optional<StudentGroupHistory> findByStudentIdAndSemesterIdAndGroupId(
      UUID studentId, UUID semesterId, UUID groupId);
}
