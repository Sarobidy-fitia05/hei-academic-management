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
@Table(name = "exam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "exam_session_id")
  private ExamSession examSession;

  @Column(name = "exam_date")
  private LocalDateTime examDate;

  @Column // Supprimer precision et scale
  private Double coefficient;

  @OneToMany(mappedBy = "exam")
  private List<Grade> grades = new ArrayList<>();
}
