package com.example.demo.conf.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Test d'intégration bout-en-bout du flux Student : service -> repository -> base Postgres réelle
 * (Testcontainers).
 *
 * <p>Hérite de {@link FacadeIT} pour récupérer la configuration des propriétés factices AWS/mail
 * (bucket S3, source SES...) nécessaires au démarrage du contexte Spring complet
 * ({@code @SpringBootTest(webEnvironment = RANDOM_PORT)} charge tous les beans, pas seulement la
 * couche JPA). Sans cet héritage, Spring échoue au démarrage avec un "Could not resolve
 * placeholder" car ces propriétés ne sont jamais définies.
 *
 * <p>NOTE : ce test passe par {@link StudentService} et non par un appel REST, car aucun
 * StudentController n'existe encore dans le projet.
 */
@Testcontainers
public class StudentIntegrationTest extends FacadeIT {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void postgresProperties(DynamicPropertyRegistry registry) {
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
