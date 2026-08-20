package com.example.demo.service;

import com.example.demo.entity.Semester;
import com.example.demo.repository.SemesterRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SemesterService {

  private final SemesterRepository semesterRepository;

  public Semester save(Semester semester) {
    validateSemester(semester);

    return semesterRepository.save(semester);
  }

  public Semester findById(UUID id) {
    return semesterRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Semester not found with id: " + id));
  }

  public List<Semester> findAll() {
    return semesterRepository.findAll();
  }

  public List<Semester> findByAcademicYearId(UUID academicYearId) {
    return semesterRepository.findByAcademicYearId(academicYearId);
  }

  public List<Semester> findByAcademicYearIdOrderBySemesterNumber(UUID academicYearId) {
    return semesterRepository.findByAcademicYearIdOrderBySemesterNumberAsc(academicYearId);
  }

  public Semester update(UUID id, Semester updatedSemester) {
    Semester existing = findById(id);

    validateSemester(updatedSemester);

    existing.setCode(updatedSemester.getCode());
    existing.setSemesterNumber(updatedSemester.getSemesterNumber());
    existing.setStartDate(updatedSemester.getStartDate());
    existing.setEndDate(updatedSemester.getEndDate());
    existing.setAcademicYear(updatedSemester.getAcademicYear());

    return semesterRepository.save(existing);
  }

  public void delete(UUID id) {
    Semester semester = findById(id);
    semesterRepository.delete(semester);
  }

  private void validateSemester(Semester semester) {
    if (semester.getCode() == null || semester.getCode().isBlank()) {
      throw new IllegalArgumentException("Semester code cannot be empty");
    }

    if (semester.getSemesterNumber() == null) {
      throw new IllegalArgumentException("Semester number cannot be null");
    }

    if (semester.getSemesterNumber() < 1 || semester.getSemesterNumber() > 6) {
      throw new IllegalArgumentException("Semester number must be between 1 and 6");
    }

    if (semester.getStartDate() == null || semester.getEndDate() == null) {
      throw new IllegalArgumentException("Semester start date and end date cannot be null");
    }

    if (!semester.getStartDate().isBefore(semester.getEndDate())) {
      throw new IllegalArgumentException("Semester start date must be before end date");
    }

    if (semester.getAcademicYear() == null) {
      throw new IllegalArgumentException("Academic year cannot be null");
    }

    LocalDate academicYearStart = semester.getAcademicYear().getStartDate();

    LocalDate academicYearEnd = semester.getAcademicYear().getEndDate();

    if (semester.getStartDate().isBefore(academicYearStart)
        || semester.getEndDate().isAfter(academicYearEnd)) {
      throw new IllegalArgumentException("Semester dates must be inside the academic year");
    }
  }
}
