package com.example.demo.endpoint.event.model;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

public class SendTranscriptEmailRequested extends PojaEvent {

  private UUID studentId;
  private String recipientEmail;

  public SendTranscriptEmailRequested() {
  }

  public SendTranscriptEmailRequested(UUID studentId, String recipientEmail) {
    this.studentId = studentId;
    this.recipientEmail = recipientEmail;
  }

  public UUID getStudentId() {
    return studentId;
  }

  public void setStudentId(UUID studentId) {
    this.studentId = studentId;
  }

  public String getRecipientEmail() {
    return recipientEmail;
  }

  public void setRecipientEmail(String recipientEmail) {
    this.recipientEmail = recipientEmail;
  }

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofSeconds(45);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofSeconds(30);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SendTranscriptEmailRequested that)) {
      return false;
    }
    return Objects.equals(studentId, that.studentId)
            && Objects.equals(recipientEmail, that.recipientEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(studentId, recipientEmail);
  }
}