package com.example.demo.endpoint.event.model;

import java.time.Duration;
import java.util.Objects;

public class SendTranscriptEmailRequested extends PojaEvent {

  private Long studentId;
  private String recipientEmail;

  public SendTranscriptEmailRequested() {}

  public SendTranscriptEmailRequested(Long studentId, String recipientEmail) {
    this.studentId = studentId;
    this.recipientEmail = recipientEmail;
  }

  public Long getStudentId() {
    return studentId;
  }

  public void setStudentId(Long studentId) {
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

  @Override
  public String toString() {
    return "SendTranscriptEmailRequested{studentId="
        + studentId
        + ", recipientEmail="
        + recipientEmail
        + "}";
  }
}
