package com.example.persistence.entities;

import static jakarta.persistence.FetchType.EAGER;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "USERS")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  @EqualsAndHashCode.Include
  private Integer userId;

  @Column(nullable = false, unique = true, length = 50)
  @EqualsAndHashCode.Include
  private String username;

  @Column(nullable = false)
  private String userPassword;

  @ManyToMany(fetch = EAGER)
  @JoinTable(
      name = "USER_USER_ROLE_MAPPING",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "user_role_id")
  )
  private Set<UserRole> userRoles;

  @OneToMany(mappedBy = "projectCreator")
  private Set<Project> creatorProjects;

  @ManyToMany(mappedBy = "projectUsers")
  private Set<Project> userProjects;
}
