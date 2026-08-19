package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "academic_year")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYear {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String label;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @ManyToOne
  @JoinColumn(name = "promotion_id")
  private Promotion promotion;

  @OneToMany(mappedBy = "academicYear")
  private List<Semester> semesters = new ArrayList<>();

  @OneToMany(mappedBy = "academicYear")
  private List<Transcript> transcripts = new ArrayList<>();
}
