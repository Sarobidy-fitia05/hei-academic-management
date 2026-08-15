package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "transcript_id")
  private Transcript transcript;

  @ManyToOne
  @JoinColumn(name = "graduation_list_id")
  private GraduationList graduationList;

  @Column(name = "document_type", nullable = false)
  private String documentType; // PDF / XLSX

  @Column(name = "s3_key")
  private String s3Key;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
