package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_program_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgramHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "student_id")
  private Student student;

  @ManyToOne
  @JoinColumn(name = "program_id")
  private Program program;

  @ManyToOne
  @JoinColumn(name = "semester_id")
  private Semester semester;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @Column(name = "change_deadline")
  private LocalDate changeDeadline;

  @Column(nullable = false)
  private boolean locked = false;
}
