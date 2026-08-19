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
@Table(name = "course_attempt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseAttempt {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "student_id")
  private Student student;

  @ManyToOne
  @JoinColumn(name = "course_id")
  private Course course;

  @ManyToOne
  @JoinColumn(name = "exam_session_id")
  private ExamSession examSession;

  @ManyToOne
  @JoinColumn(name = "group_id")
  private Group group;

  @Column(name = "attempt_number")
  private Integer attemptNumber;

  @Column(name = "final_grade") // Supprimer precision et scale
  private Double finalGrade;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CourseAttemptStatus status;

  @OneToMany(mappedBy = "courseAttempt")
  private List<Bonus> bonuses = new ArrayList<>();
}
