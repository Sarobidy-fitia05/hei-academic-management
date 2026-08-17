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
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true, nullable = false)
  private String reference;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private Integer credits;

  @OneToMany(mappedBy = "course")
  private List<ProgramCourse> programCourses = new ArrayList<>();

  @OneToMany(mappedBy = "course")
  private List<GroupCourse> groupCourses = new ArrayList<>();

  @OneToMany(mappedBy = "course")
  private List<CourseAssignment> courseAssignments = new ArrayList<>();

  @OneToMany(mappedBy = "course")
  private List<ExamSession> examSessions = new ArrayList<>();

  @OneToMany(mappedBy = "course")
  private List<CourseAttempt> courseAttempts = new ArrayList<>();
}
