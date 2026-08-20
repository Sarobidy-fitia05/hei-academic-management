package com.example.demo.repository;

import com.example.demo.entity.Semester;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, UUID> {

  // Recherche par année académique
  List<Semester> findByAcademicYearId(UUID academicYearId);

  // Recherche par année académique triée par numéro de semestre
  List<Semester> findByAcademicYearIdOrderBySemesterNumberAsc(UUID academicYearId);
}
