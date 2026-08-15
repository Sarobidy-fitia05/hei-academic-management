package com.example.demo.conf.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.entity.Promotion;
import com.example.demo.entity.Student;
import com.example.demo.repository.PromotionRepository;
import com.example.demo.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudentRepositoryTest {

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

  @Autowired private StudentRepository studentRepository;

  @Autowired private PromotionRepository promotionRepository;

  private Promotion promotion;

  @BeforeEach
  void setUp() {
    promotion = new Promotion();
    promotion.setYear(2024);
    promotion.setGroupPrefix("M1");
    promotion = promotionRepository.save(promotion);
  }

  @Test
  void shouldSaveStudent() {
    Student student = new Student();
    student.setStudentReference("STU001");
    student.setLastName("Doe");
    student.setFirstName("John");
    student.setEmail("john.doe@example.com");
    student.setEntryPromotion(promotion);

    Student saved = studentRepository.save(student);

    assertThat(saved).isNotNull();
    assertThat(saved.getId()).isNotNull();
  }
}
