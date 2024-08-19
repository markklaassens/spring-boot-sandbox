package com.example.persistence.repositories;

import com.example.persistence.entities.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

  Optional<UserRole> findByUserRoleValue(String value);
}
