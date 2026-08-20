package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "graduation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Graduation {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "student_id")
  private Student student;

  @ManyToOne
  @JoinColumn(name = "graduation_list_id")
  private GraduationList graduationList;

  @Column(name = "general_average") // Supprimer precision et scale
  private Double generalAverage;

  private Integer rank;

  @Column(name = "graduation_date")
  private LocalDate graduationDate;
}
