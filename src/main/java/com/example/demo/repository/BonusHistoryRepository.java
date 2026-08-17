package com.example.demo.repository;

import com.example.demo.entity.BonusHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BonusHistoryRepository extends JpaRepository<BonusHistory, UUID> {
  List<BonusHistory> findByBonusIdOrderByChangedAtDesc(UUID bonusId);

  List<BonusHistory> findByChangedById(UUID changedById);

  List<BonusHistory> findByBonusId(UUID bonusId);

  List<BonusHistory> findByBonusIdOrderByChangedAtAsc(UUID bonusId);
}
