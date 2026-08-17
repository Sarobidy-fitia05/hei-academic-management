package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bonus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bonus {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "course_attempt_id")
  private CourseAttempt courseAttempt;

  @Column // Supprimer precision et scale
  private Double value;

  private String label;

  @Column(name = "awarded_at")
  private LocalDateTime awardedAt;

  @OneToMany(mappedBy = "bonus")
  private List<BonusHistory> bonusHistories = new ArrayList<>();
}
