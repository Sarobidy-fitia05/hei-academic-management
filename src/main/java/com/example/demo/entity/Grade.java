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
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "grade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "exam_id")
  private Exam exam;

  @ManyToOne
  @JoinColumn(name = "student_id")
  private Student student;

  @ManyToOne
  @JoinColumn(name = "recorded_by")
  private UserAccount recordedBy;

  @Column // Supprimer precision et scale
  private Double value;

  @CreationTimestamp
  @Column(name = "recorded_at")
  private LocalDateTime recordedAt;

  @OneToMany(mappedBy = "grade")
  private List<GradeHistory> gradeHistories = new ArrayList<>();
}
