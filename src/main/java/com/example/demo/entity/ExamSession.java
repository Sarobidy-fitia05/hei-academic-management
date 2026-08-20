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
@Table(name = "exam_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamSession {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "course_id")
  private Course course;

  @ManyToOne
  @JoinColumn(name = "group_id")
  private Group group;

  @ManyToOne
  @JoinColumn(name = "semester_id")
  private Semester semester;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ExamSessionType type;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @OneToMany(mappedBy = "examSession")
  private List<Exam> exams = new ArrayList<>();

  @OneToMany(mappedBy = "examSession")
  private List<CourseAttempt> courseAttempts = new ArrayList<>();
}
