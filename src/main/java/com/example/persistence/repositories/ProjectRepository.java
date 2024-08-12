package com.example.persistence.repositories;

import com.example.persistence.entities.Project;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

  Optional<Project> findByProjectName(String projectName);
}
