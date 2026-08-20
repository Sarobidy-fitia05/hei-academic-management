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
@Table(name = "semester")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Semester {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String code;

  @Column(name = "semester_number", nullable = false)
  private Integer semesterNumber;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @ManyToOne
  @JoinColumn(name = "academic_year_id")
  private AcademicYear academicYear;

  @OneToMany(mappedBy = "semester")
  private List<StudentProgramHistory> studentProgramHistories = new ArrayList<>();

  @OneToMany(mappedBy = "semester")
  private List<StudentGroupHistory> studentGroupHistories = new ArrayList<>();

  @OneToMany(mappedBy = "semester")
  private List<ProgramCourse> programCourses = new ArrayList<>();

  @OneToMany(mappedBy = "semester")
  private List<GroupCourse> groupCourses = new ArrayList<>();

  @OneToMany(mappedBy = "semester")
  private List<CourseAssignment> courseAssignments = new ArrayList<>();

  @OneToMany(mappedBy = "semester")
  private List<ExamSession> examSessions = new ArrayList<>();

  @OneToMany(mappedBy = "semester")
  private List<Transcript> transcripts = new ArrayList<>();
}
