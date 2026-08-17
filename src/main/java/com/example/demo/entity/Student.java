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
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "student_reference", nullable = false, unique = true, length = 20)
  private String studentReference;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String email;

  @ManyToOne
  @JoinColumn(name = "entry_promotion_id")
  private Promotion entryPromotion;

  @OneToOne
  @JoinColumn(name = "user_account_id")
  private UserAccount userAccount;

  @OneToMany(mappedBy = "student")
  private List<StudentProgramHistory> studentProgramHistories = new ArrayList<>();

  @OneToMany(mappedBy = "student")
  private List<StudentGroupHistory> studentGroupHistories = new ArrayList<>();

  @OneToMany(mappedBy = "student")
  private List<Grade> grades = new ArrayList<>();

  @OneToMany(mappedBy = "student")
  private List<CourseAttempt> courseAttempts = new ArrayList<>();

  @OneToMany(mappedBy = "student")
  private List<Transcript> transcripts = new ArrayList<>();

  @OneToMany(mappedBy = "student")
  private List<Graduation> graduations = new ArrayList<>();
}
