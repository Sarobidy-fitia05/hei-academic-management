package com.example.demo.repository;

import com.example.demo.entity.Program;
import com.example.demo.entity.ProgramCode;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {

  // Recherche par code (COMMON / EL / TN)
  Optional<Program> findByCode(ProgramCode code);
}
