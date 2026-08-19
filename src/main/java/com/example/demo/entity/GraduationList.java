package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "graduation_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraduationList {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "promotion_id")
  private Promotion promotion;

  @ManyToOne
  @JoinColumn(name = "program_id")
  private Program program;

  @Column(name = "graduation_year")
  private Integer graduationYear;

  @Column(name = "generated_at")
  private LocalDateTime generatedAt;

  @OneToMany(mappedBy = "graduationList")
  private List<Graduation> graduations = new ArrayList<>();

  @OneToMany(mappedBy = "graduationList")
  private List<Document> documents = new ArrayList<>();
}
