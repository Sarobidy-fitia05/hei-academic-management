package com.example.demo.transcript;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "transcripts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "academic_year"}))
public class Transcript {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "student_id", nullable = false)
  private Long studentId;

  @Column(name = "academic_year", nullable = false)
  private Integer academicYear;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TranscriptStatus status;

  @Column(name = "pdf_s3_key")
  private String pdfS3Key;

  @Column(name = "generated_at")
  private LocalDateTime generatedAt;

  public Transcript() {}

  public Transcript(
      Long studentId, Integer academicYear, TranscriptStatus status, String pdfS3Key) {
    this.studentId = studentId;
    this.academicYear = academicYear;
    this.status = status;
    this.pdfS3Key = pdfS3Key;
    this.generatedAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public Long getStudentId() {
    return studentId;
  }

  public Integer getAcademicYear() {
    return academicYear;
  }

  public TranscriptStatus getStatus() {
    return status;
  }

  public void setStatus(TranscriptStatus status) {
    this.status = status;
  }

  public String getPdfS3Key() {
    return pdfS3Key;
  }

  public void setPdfS3Key(String pdfS3Key) {
    this.pdfS3Key = pdfS3Key;
  }

  public LocalDateTime getGeneratedAt() {
    return generatedAt;
  }

  public void setGeneratedAt(LocalDateTime generatedAt) {
    this.generatedAt = generatedAt;
  }
}
