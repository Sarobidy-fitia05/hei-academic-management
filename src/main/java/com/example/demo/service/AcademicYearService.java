package com.example.demo.service;

import com.example.demo.entity.AcademicYear;
import com.example.demo.repository.AcademicYearRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AcademicYearService {

  private final AcademicYearRepository academicYearRepository;

  public AcademicYear save(AcademicYear academicYear) {
    validateDates(academicYear.getStartDate(), academicYear.getEndDate());

    if (academicYear.getPromotion() == null) {
      throw new IllegalArgumentException("Promotion cannot be null");
    }

    return academicYearRepository.save(academicYear);
  }

  public AcademicYear findById(UUID id) {
    return academicYearRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Academic year not found with id: " + id));
  }

  public List<AcademicYear> findAll() {
    return academicYearRepository.findAll();
  }

  public List<AcademicYear> findByPromotionId(UUID promotionId) {
    return academicYearRepository.findByPromotionId(promotionId);
  }

  public List<AcademicYear> findByPromotionIdOrderByStartDateDesc(UUID promotionId) {
    return academicYearRepository.findByPromotionIdOrderByStartDateDesc(promotionId);
  }

  public AcademicYear update(UUID id, AcademicYear updatedAcademicYear) {
    AcademicYear existing = findById(id);

    validateDates(updatedAcademicYear.getStartDate(), updatedAcademicYear.getEndDate());

    if (updatedAcademicYear.getPromotion() == null) {
      throw new IllegalArgumentException("Promotion cannot be null");
    }

    existing.setLabel(updatedAcademicYear.getLabel());
    existing.setStartDate(updatedAcademicYear.getStartDate());
    existing.setEndDate(updatedAcademicYear.getEndDate());
    existing.setPromotion(updatedAcademicYear.getPromotion());

    return academicYearRepository.save(existing);
  }

  public void delete(UUID id) {
    AcademicYear academicYear = findById(id);
    academicYearRepository.delete(academicYear);
  }

  private void validateDates(LocalDate startDate, LocalDate endDate) {
    if (startDate == null || endDate == null) {
      throw new IllegalArgumentException("Start date and end date cannot be null");
    }

    if (!startDate.isBefore(endDate)) {
      throw new IllegalArgumentException("Academic year start date must be before end date");
    }
  }
}
