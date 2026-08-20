package com.example.demo.repository;

import com.example.demo.entity.Teacher;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

  // Recherche par référence unique
  Optional<Teacher> findByReference(String reference);

  // Recherche par compte utilisateur
  Optional<Teacher> findByUserAccountId(UUID userAccountId);

  // Recherche par email
  Optional<Teacher> findByEmail(String email);
}
