package com.example.demo.conf.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Test d'intégration bout-en-bout du flux Student : service -> repository -> base Postgres réelle
 * (Testcontainers).
 *
 * <p>NOTE : ce test passe par {@link StudentService} et non par un appel REST, car aucun
 * StudentController n'existe encore dans le projet (seuls des controllers "health" sont présents
 * sous endpoint/rest/controller). Dès qu'un StudentController sera ajouté, ce test pourra être
 * complété avec des appels TestRestTemplate sur /api/students.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class StudentIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired private StudentService studentService;

  @Test
  void shouldPersistAndRetrieveStudent() {
    Student student = new Student();
    student.setStudentReference("INT001");
    student.setLastName("Rakoto");
    student.setFirstName("Faniry");
    student.setEmail("faniry.rakoto@example.com");

    Student saved = studentService.save(student);

    assertThat(saved.getId()).isNotNull();

    Student found = studentService.findById(saved.getId());
    assertThat(found.getStudentReference()).isEqualTo("INT001");
  }

  @Test
  void shouldListAllStudents() {
    Student student = new Student();
    student.setStudentReference("INT002");
    student.setLastName("Andria");
    student.setFirstName("Tojo");
    student.setEmail("tojo.andria@example.com");
    studentService.save(student);

    List<Student> students = studentService.findAll();

    assertThat(students).isNotEmpty();
  }
}
