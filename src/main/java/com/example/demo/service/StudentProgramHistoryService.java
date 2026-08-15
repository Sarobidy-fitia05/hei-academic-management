package com.example.demo.service;

import com.example.demo.entity.StudentProgramHistory;
import com.example.demo.repository.StudentProgramHistoryRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentProgramHistoryService {

  private final StudentProgramHistoryRepository repository;

  public StudentProgramHistory save(StudentProgramHistory history) {
    return repository.save(history);
  }

  public StudentProgramHistory findById(UUID id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("StudentProgramHistory not found with id: " + id));
  }

  public List<StudentProgramHistory> findByStudentId(UUID studentId) {
    return repository.findByStudentId(studentId);
  }

  public List<StudentProgramHistory> findByStudentIdAndSemesterId(UUID studentId, UUID semesterId) {
    return repository.findByStudentIdAndSemesterId(studentId, semesterId);
  }

  public List<StudentProgramHistory> findByProgramId(UUID programId) {
    return repository.findByProgramId(programId);
  }

  public void delete(UUID id) {
    repository.deleteById(id);
  }

  @Transactional
  public StudentProgramHistory lockProgramHistory(UUID id) {
    StudentProgramHistory history = findById(id);
    history.setLocked(true);
    return repository.save(history);
  }

  @Transactional
  public StudentProgramHistory updateEndDate(UUID id, LocalDate endDate) {
    StudentProgramHistory history = findById(id);
    history.setEndDate(endDate);
    return repository.save(history);
  }
}
