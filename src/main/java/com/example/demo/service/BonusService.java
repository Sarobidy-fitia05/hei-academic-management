package com.example.demo.service;

import com.example.demo.entity.Bonus;
import com.example.demo.entity.BonusHistory;
import com.example.demo.entity.CourseAttempt;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.BonusHistoryRepository;
import com.example.demo.repository.BonusRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BonusService {

  private final BonusRepository bonusRepository;
  private final BonusHistoryRepository bonusHistoryRepository;

  @Transactional
  public Bonus addBonus(CourseAttempt courseAttempt, Double value, String label) {
    Bonus bonus = new Bonus();
    bonus.setCourseAttempt(courseAttempt);
    bonus.setValue(value);
    bonus.setLabel(label);
    bonus.setAwardedAt(LocalDateTime.now());

    return bonusRepository.save(bonus);
  }

  @Transactional
  public Bonus updateBonusValue(
      UUID bonusId, Double newValue, UserAccount changedBy, String reason) {
    Bonus bonus =
        bonusRepository
            .findById(bonusId)
            .orElseThrow(() -> new IllegalArgumentException("Bonus introuvable : " + bonusId));

    Double oldValue = bonus.getValue();

    BonusHistory history = new BonusHistory();
    history.setBonus(bonus);
    history.setOldValue(oldValue);
    history.setNewValue(newValue);
    history.setChangedAt(LocalDateTime.now());
    history.setChangedBy(changedBy);
    history.setReason(reason);

    bonusHistoryRepository.save(history);

    bonus.setValue(newValue);
    return bonusRepository.save(bonus);
  }

  @Transactional(readOnly = true)
  public List<Bonus> getBonusesForAttempt(UUID courseAttemptId) {
    return bonusRepository.findByCourseAttemptId(courseAttemptId);
  }

  @Transactional(readOnly = true)
  public List<BonusHistory> getHistoryForBonus(UUID bonusId) {
    return bonusHistoryRepository.findByBonusIdOrderByChangedAtAsc(bonusId);
  }

  @Transactional(readOnly = true)
  public Double getTotalBonusForAttempt(UUID courseAttemptId) {
    return bonusRepository.findByCourseAttemptId(courseAttemptId).stream()
        .mapToDouble(Bonus::getValue)
        .sum();
  }
}
