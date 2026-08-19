package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

  private final StudentRepository studentRepository;

  public Student save(Student student) {
    return studentRepository.save(student);
  }

  public Student findById(UUID id) {
    return studentRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
  }

  public Student findByStudentReference(String studentReference) {
    return studentRepository
        .findByStudentReference(studentReference)
        .orElseThrow(
            () -> new RuntimeException("Student not found with reference: " + studentReference));
  }

  public Student findByEmail(String email) {
    return studentRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));
  }

  public Student findByUserAccountId(UUID userAccountId) {
    return studentRepository
        .findByUserAccountId(userAccountId)
        .orElseThrow(
            () -> new RuntimeException("Student not found for user account: " + userAccountId));
  }

  public List<Student> findAll() {
    return studentRepository.findAll();
  }

  public List<Student> findByPromotionId(UUID promotionId) {
    return studentRepository.findByEntryPromotionId(promotionId);
  }

  public List<Student> searchByName(String name) {
    return studentRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
        name, name);
  }

  public List<Student> findStudentsWithGradesByPromotion(UUID promotionId) {
    return studentRepository.findStudentsWithGradesByPromotion(promotionId);
  }

  public List<Student> findStudentsWhoPassedCourse(UUID courseId) {
    return studentRepository.findStudentsWhoPassedCourse(courseId);
  }

  public long countByPromotion(UUID promotionId) {
    return studentRepository.countByEntryPromotionId(promotionId);
  }

  public void delete(UUID id) {
    studentRepository.deleteById(id);
  }

  @Transactional
  public Student updateEmail(UUID id, String newEmail) {
    Student student = findById(id);
    student.setEmail(newEmail);
    return studentRepository.save(student);
  }
}
