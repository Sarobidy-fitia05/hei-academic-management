package com.example.demo.service;

import com.example.demo.entity.Teacher;
import com.example.demo.repository.TeacherRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

  private final TeacherRepository teacherRepository;

  public Teacher save(Teacher teacher) {
    validateTeacher(teacher);

    if (teacher.getId() == null) {
      if (teacherRepository.findByReference(teacher.getReference()).isPresent()) {
        throw new IllegalArgumentException(
            "A teacher already exists with reference: " + teacher.getReference());
      }

      if (teacherRepository.findByEmail(teacher.getEmail()).isPresent()) {
        throw new IllegalArgumentException(
            "A teacher already exists with email: " + teacher.getEmail());
      }
    }

    return teacherRepository.save(teacher);
  }

  public Teacher findById(UUID id) {
    return teacherRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
  }

  public Teacher findByReference(String reference) {
    return teacherRepository
        .findByReference(reference)
        .orElseThrow(() -> new RuntimeException("Teacher not found with reference: " + reference));
  }

  public Teacher findByEmail(String email) {
    return teacherRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Teacher not found with email: " + email));
  }

  public Teacher findByUserAccountId(UUID userAccountId) {
    return teacherRepository
        .findByUserAccountId(userAccountId)
        .orElseThrow(
            () -> new RuntimeException("Teacher not found for user account: " + userAccountId));
  }

  public List<Teacher> findAll() {
    return teacherRepository.findAll();
  }

  public Teacher update(UUID id, Teacher updatedTeacher) {
    Teacher existing = findById(id);

    validateTeacher(updatedTeacher);

    teacherRepository
        .findByReference(updatedTeacher.getReference())
        .filter(teacher -> !teacher.getId().equals(id))
        .ifPresent(
            teacher -> {
              throw new IllegalArgumentException(
                  "A teacher already exists with reference: " + updatedTeacher.getReference());
            });

    teacherRepository
        .findByEmail(updatedTeacher.getEmail())
        .filter(teacher -> !teacher.getId().equals(id))
        .ifPresent(
            teacher -> {
              throw new IllegalArgumentException(
                  "A teacher already exists with email: " + updatedTeacher.getEmail());
            });

    existing.setReference(updatedTeacher.getReference());
    existing.setLastName(updatedTeacher.getLastName());
    existing.setFirstName(updatedTeacher.getFirstName());
    existing.setEmail(updatedTeacher.getEmail());
    existing.setUserAccount(updatedTeacher.getUserAccount());

    return teacherRepository.save(existing);
  }

  public void delete(UUID id) {
    Teacher teacher = findById(id);
    teacherRepository.delete(teacher);
  }

  private void validateTeacher(Teacher teacher) {
    if (teacher.getReference() == null || teacher.getReference().isBlank()) {
      throw new IllegalArgumentException("Teacher reference cannot be empty");
    }

    if (teacher.getLastName() == null || teacher.getLastName().isBlank()) {
      throw new IllegalArgumentException("Teacher last name cannot be empty");
    }

    if (teacher.getFirstName() == null || teacher.getFirstName().isBlank()) {
      throw new IllegalArgumentException("Teacher first name cannot be empty");
    }

    if (teacher.getEmail() == null || teacher.getEmail().isBlank()) {
      throw new IllegalArgumentException("Teacher email cannot be empty");
    }
  }
}
