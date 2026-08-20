package com.example.demo.repository;

import com.example.demo.entity.Group;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

  // Recherche par promotion
  List<Group> findByPromotionId(UUID promotionId);

  // Recherche par promotion triée par référence
  List<Group> findByPromotionIdOrderByReferenceAsc(UUID promotionId);
}
