package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.entity.Bonus;
import com.example.demo.entity.BonusHistory;
import com.example.demo.entity.CourseAttempt;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.BonusHistoryRepository;
import com.example.demo.repository.BonusRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BonusServiceTest {

  @Mock private BonusRepository bonusRepository;
  @Mock private BonusHistoryRepository bonusHistoryRepository;

  @InjectMocks private BonusService bonusService;

  private CourseAttempt courseAttempt;
  private UserAccount changedBy;

  @BeforeEach
  void setUp() {
    courseAttempt = new CourseAttempt();
    courseAttempt.setId(UUID.randomUUID());

    changedBy = new UserAccount();
    changedBy.setId(UUID.randomUUID());
    changedBy.setUsername("admin");
  }

  @Test
  void addBonus_setsAwardedAtAndSavesWithCorrectValue() {
    when(bonusRepository.save(any(Bonus.class))).thenAnswer(inv -> inv.getArgument(0));

    Bonus result = bonusService.addBonus(courseAttempt, 1.0, "Participation active");

    assertThat(result.getValue()).isEqualTo(1.0);
    assertThat(result.getLabel()).isEqualTo("Participation active");
    assertThat(result.getAwardedAt()).isNotNull();
    assertThat(result.getCourseAttempt()).isEqualTo(courseAttempt);
  }

  @Test
  void updateBonusValue_throwsException_whenBonusNotFound() {
    UUID bonusId = UUID.randomUUID();
    when(bonusRepository.findById(bonusId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bonusService.updateBonusValue(bonusId, 2.0, changedBy, "correction"))
        .isInstanceOf(IllegalArgumentException.class);

    verifyNoInteractions(bonusHistoryRepository);
  }

  @Test
  void updateBonusValue_createsHistoryEntry_beforeOverwritingValue() {
    Bonus bonus = new Bonus();
    bonus.setId(UUID.randomUUID());
    bonus.setValue(1.0);

    when(bonusRepository.findById(bonus.getId())).thenReturn(Optional.of(bonus));
    when(bonusRepository.save(any(Bonus.class))).thenAnswer(inv -> inv.getArgument(0));

    bonusService.updateBonusValue(bonus.getId(), 2.5, changedBy, "correction erreur de saisie");

    ArgumentCaptor<BonusHistory> captor = ArgumentCaptor.forClass(BonusHistory.class);
    verify(bonusHistoryRepository).save(captor.capture());

    BonusHistory history = captor.getValue();
    assertThat(history.getOldValue()).isEqualTo(1.0);
    assertThat(history.getNewValue()).isEqualTo(2.5);
    assertThat(history.getChangedBy()).isEqualTo(changedBy);
    assertThat(bonus.getValue()).isEqualTo(2.5);
  }

  @Test
  void getTotalBonusForAttempt_sumsAllBonusValues() {
    Bonus b1 = new Bonus();
    b1.setValue(1.0);
    Bonus b2 = new Bonus();
    b2.setValue(0.5);

    when(bonusRepository.findByCourseAttemptId(courseAttempt.getId())).thenReturn(List.of(b1, b2));

    Double total = bonusService.getTotalBonusForAttempt(courseAttempt.getId());

    assertThat(total).isEqualTo(1.5);
  }

  @Test
  void getTotalBonusForAttempt_returnsZero_whenNoBonuses() {
    when(bonusRepository.findByCourseAttemptId(courseAttempt.getId())).thenReturn(List.of());

    Double total = bonusService.getTotalBonusForAttempt(courseAttempt.getId());

    assertThat(total).isEqualTo(0.0);
  }
}
