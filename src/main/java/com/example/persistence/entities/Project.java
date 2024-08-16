package com.example.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PROJECTS")
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column()
  private Integer projectId;

  @Column(nullable = false, unique = true, length = 300)
  private String projectName;

  @Column(nullable = false, columnDefinition = "TEXT", length = 1000)
  private String projectDescription;

  @ManyToOne
  @JoinColumn(nullable = false, name = "project_type_id")
  private ProjectType projectType;

  @ManyToOne
  @JoinColumn(nullable = false, name = "user_id")
  private User projectCreator;
}
