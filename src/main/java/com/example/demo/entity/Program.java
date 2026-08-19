package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "program")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Program {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProgramCode code;

  @Column(nullable = false)
  private String label;

  @OneToMany(mappedBy = "program")
  private List<StudentProgramHistory> studentProgramHistories = new ArrayList<>();

  @OneToMany(mappedBy = "program")
  private List<ProgramCourse> programCourses = new ArrayList<>();

  @OneToMany(mappedBy = "program")
  private List<GraduationList> graduationLists = new ArrayList<>();
}
