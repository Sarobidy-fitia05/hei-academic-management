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
@Table(name = "transcript")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transcript {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "student_id")
  private Student student;

  @ManyToOne
  @JoinColumn(name = "academic_year_id")
  private AcademicYear academicYear;

  @ManyToOne
  @JoinColumn(name = "semester_id")
  private Semester semester;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String status;

  @Column(name = "general_average") // Supprimer precision et scale
  private Double generalAverage;

  @Column(name = "total_credits")
  private Integer totalCredits;

  @Column(name = "generated_at")
  private LocalDateTime generatedAt;

  @OneToMany(mappedBy = "transcript")
  private List<Document> documents = new ArrayList<>();
}
