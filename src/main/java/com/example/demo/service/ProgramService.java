package com.example.demo.service;

import com.example.demo.entity.Program;
import com.example.demo.entity.ProgramCode;
import com.example.demo.repository.ProgramRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramService {

  private final ProgramRepository programRepository;

  public Program save(Program program) {
    validateProgram(program);

    if (program.getId() == null && programRepository.findByCode(program.getCode()).isPresent()) {
      throw new IllegalArgumentException(
          "A program already exists with code: " + program.getCode());
    }

    return programRepository.save(program);
  }

  public Program findById(UUID id) {
    return programRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Program not found with id: " + id));
  }

  public Program findByCode(ProgramCode code) {
    return programRepository
        .findByCode(code)
        .orElseThrow(() -> new RuntimeException("Program not found with code: " + code));
  }

  public List<Program> findAll() {
    return programRepository.findAll();
  }

  public Program update(UUID id, Program updatedProgram) {
    Program existing = findById(id);

    validateProgram(updatedProgram);

    if (!existing.getCode().equals(updatedProgram.getCode())
        && programRepository.findByCode(updatedProgram.getCode()).isPresent()) {
      throw new IllegalArgumentException(
          "A program already exists with code: " + updatedProgram.getCode());
    }

    existing.setCode(updatedProgram.getCode());
    existing.setLabel(updatedProgram.getLabel());

    return programRepository.save(existing);
  }

  public void delete(UUID id) {
    Program program = findById(id);
    programRepository.delete(program);
  }

  private void validateProgram(Program program) {
    if (program.getCode() == null) {
      throw new IllegalArgumentException("Program code cannot be null");
    }

    if (program.getLabel() == null || program.getLabel().isBlank()) {
      throw new IllegalArgumentException("Program label cannot be empty");
    }
  }
}
