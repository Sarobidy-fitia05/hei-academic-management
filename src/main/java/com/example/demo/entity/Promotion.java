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
@Table(name = "promotion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private Integer year;

  @Column(name = "group_prefix")
  private String groupPrefix;

  // CORRECTION : mappedBy doit correspondre au champ dans Student
  @OneToMany(mappedBy = "entryPromotion")
  private List<Student> students = new ArrayList<>();

  @OneToMany(mappedBy = "promotion")
  private List<AcademicYear> academicYears = new ArrayList<>();

  @OneToMany(mappedBy = "promotion")
  private List<Group> groups = new ArrayList<>();

  @OneToMany(mappedBy = "promotion")
  private List<GraduationList> graduationLists = new ArrayList<>();
}
