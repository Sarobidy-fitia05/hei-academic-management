package com.example.demo.graduation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "graduation_exports")
public class GraduationExport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "promotion_id", nullable = false)
  private Long promotionId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Parcours parcours;

  @Column(name = "xlsx_s3_key", nullable = false)
  private String xlsxS3Key;

  @Column(name = "generated_at", nullable = false)
  private LocalDateTime generatedAt;

  public GraduationExport() {}

  public GraduationExport(Long promotionId, Parcours parcours, String xlsxS3Key) {
    this.promotionId = promotionId;
    this.parcours = parcours;
    this.xlsxS3Key = xlsxS3Key;
    this.generatedAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public Long getPromotionId() {
    return promotionId;
  }

  public Parcours getParcours() {
    return parcours;
  }

  public String getXlsxS3Key() {
    return xlsxS3Key;
  }

  public void setXlsxS3Key(String xlsxS3Key) {
    this.xlsxS3Key = xlsxS3Key;
  }

  public LocalDateTime getGeneratedAt() {
    return generatedAt;
  }

  public void setGeneratedAt(LocalDateTime generatedAt) {
    this.generatedAt = generatedAt;
  }
}
