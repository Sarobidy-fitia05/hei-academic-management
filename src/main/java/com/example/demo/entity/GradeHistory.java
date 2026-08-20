package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "grade_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GradeHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "grade_id")
  private Grade grade;

  @Column(name = "old_value") // Supprimer precision et scale
  private Double oldValue;

  @Column(name = "new_value") // Supprimer precision et scale
  private Double newValue;

  @Column(name = "changed_at")
  private LocalDateTime changedAt;

  @ManyToOne
  @JoinColumn(name = "changed_by")
  private UserAccount changedBy;

  private String reason;
}
