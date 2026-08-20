package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.entity.Exam;
import com.example.demo.entity.ExamSession;
import com.example.demo.entity.ExamSessionType;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.ExamSessionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

  @Mock private ExamRepository examRepository;
  @Mock private ExamSessionRepository examSessionRepository;

  @InjectMocks private ExamService examService;

  private UUID sessionId;
  private ExamSession normalSession;
  private ExamSession retakeSession;

  @BeforeEach
  void setUp() {
    sessionId = UUID.randomUUID();

    normalSession = new ExamSession();
    normalSession.setId(sessionId);
    normalSession.setType(ExamSessionType.NORMAL);

    retakeSession = new ExamSession();
    retakeSession.setId(sessionId);
    retakeSession.setType(ExamSessionType.RETAKE);
  }

  @Test
  void createExam_throwsException_whenCoefficientIsNull() {
    when(examSessionRepository.findById(sessionId)).thenReturn(Optional.of(normalSession));

    assertThatThrownBy(() -> examService.createExam(sessionId, LocalDateTime.now(), null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("positif");
  }

  @Test
  void createExam_throwsException_whenCoefficientIsNegativeOrZero() {
    when(examSessionRepository.findById(sessionId)).thenReturn(Optional.of(normalSession));

    assertThatThrownBy(() -> examService.createExam(sessionId, LocalDateTime.now(), 0.0))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void createExam_retake_throwsException_whenSessionAlreadyHasAnExam() {
    when(examSessionRepository.findById(sessionId)).thenReturn(Optional.of(retakeSession));

    Exam existing = new Exam();
    existing.setCoefficient(1.0);
    when(examRepository.findByExamSessionId(sessionId)).thenReturn(List.of(existing));

    assertThatThrownBy(() -> examService.createExam(sessionId, LocalDateTime.now(), 1.0))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("RETAKE");

    verify(examRepository, never()).save(any());
  }

  @Test
  void createExam_retake_throwsException_whenCoefficientIsNotOne() {
    when(examSessionRepository.findById(sessionId)).thenReturn(Optional.of(retakeSession));
    when(examRepository.findByExamSessionId(sessionId)).thenReturn(List.of());

    assertThatThrownBy(() -> examService.createExam(sessionId, LocalDateTime.now(), 0.8))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("1.00");
  }

  @Test
  void createExam_retake_succeeds_whenValid() {
    when(examSessionRepository.findById(sessionId)).thenReturn(Optional.of(retakeSession));
    when(examRepository.findByExamSessionId(sessionId)).thenReturn(List.of());
    when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));

    Exam result = examService.createExam(sessionId, LocalDateTime.now(), 1.0);

    assertThat(result.getCoefficient()).isEqualTo(1.0);
    verify(examRepository).save(any(Exam.class));
  }

  @Test
  void createExam_normal_throwsException_whenSumWouldExceedOne() {
    when(examSessionRepository.findById(sessionId)).thenReturn(Optional.of(normalSession));

    Exam existing = new Exam();
    existing.setCoefficient(0.7);
    when(examRepository.findByExamSessionId(sessionId)).thenReturn(List.of(existing));

    assertThatThrownBy(() -> examService.createExam(sessionId, LocalDateTime.now(), 0.5))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("depasserait");

    verify(examRepository, never()).save(any());
  }

  @Test
  void createExam_normal_succeeds_whenSumStaysWithinOne() {
    when(examSessionRepository.findById(sessionId)).thenReturn(Optional.of(normalSession));

    Exam existing = new Exam();
    existing.setCoefficient(0.4);
    when(examRepository.findByExamSessionId(sessionId)).thenReturn(List.of(existing));
    when(examRepository.save(any(Exam.class))).thenAnswer(inv -> inv.getArgument(0));

    Exam result = examService.createExam(sessionId, LocalDateTime.now(), 0.6);

    assertThat(result.getCoefficient()).isEqualTo(0.6);
    verify(examRepository).save(any(Exam.class));
  }

  @Test
  void validateSessionComplete_throwsException_whenSumIsNotOne() {
    Exam exam1 = new Exam();
    exam1.setCoefficient(0.4);
    when(examRepository.findByExamSessionId(sessionId)).thenReturn(List.of(exam1));

    assertThatThrownBy(() -> examService.validateSessionComplete(sessionId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("n'est pas complete");
  }

  @Test
  void validateSessionComplete_succeeds_whenSumIsExactlyOne() {
    Exam exam1 = new Exam();
    exam1.setCoefficient(0.4);
    Exam exam2 = new Exam();
    exam2.setCoefficient(0.6);
    when(examRepository.findByExamSessionId(sessionId)).thenReturn(List.of(exam1, exam2));

    assertThatCode(() -> examService.validateSessionComplete(sessionId)).doesNotThrowAnyException();
  }
}
