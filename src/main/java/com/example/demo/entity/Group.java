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
@Table(name = "`group`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String reference;

  @ManyToOne
  @JoinColumn(name = "promotion_id")
  private Promotion promotion;

  @OneToMany(mappedBy = "group")
  private List<StudentGroupHistory> studentGroupHistories = new ArrayList<>();

  @OneToMany(mappedBy = "group")
  private List<GroupCourse> groupCourses = new ArrayList<>();

  @OneToMany(mappedBy = "group")
  private List<CourseAssignment> courseAssignments = new ArrayList<>();

  @OneToMany(mappedBy = "group")
  private List<ExamSession> examSessions = new ArrayList<>();

  @OneToMany(mappedBy = "group")
  private List<CourseAttempt> courseAttempts = new ArrayList<>();
}
