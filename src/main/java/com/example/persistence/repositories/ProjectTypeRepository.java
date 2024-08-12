package com.example.persistence.repositories;

import com.example.persistence.entities.ProjectType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTypeRepository extends JpaRepository<ProjectType, Integer> {

  Optional<ProjectType> findByProjectTypeValue(String value);
}
