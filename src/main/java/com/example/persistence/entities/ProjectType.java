package com.example.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PROJECT_TYPES")
@Builder
public class ProjectType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer projectTypeId;

  @Column(nullable = false)
  private String projectTypeValue;

  @OneToMany(mappedBy = "projectType")
  private Set<Project> projects;
}
