package com.example.services.implementations;

import com.example.api.dto.ProjectDto;
import com.example.exceptions.UserNotFoundException;
import com.example.persistence.entities.User;
import com.example.persistence.repositories.UserRepository;
import com.example.services.interfaces.UserService;
import com.example.utilities.ProjectMapper;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User getUser() {
    val username = SecurityContextHolder.getContext().getAuthentication().getName();
    val optionalUser = userRepository.findByUsername(username);
    if (optionalUser.isEmpty()) {
      log.error("User with username '%s' not found in database.".formatted(username));
      throw new UserNotFoundException("User with username '%s' not found in database.".formatted(username));
    }
    return optionalUser.get();
  }

  @Override
  @Transactional
  public List<ProjectDto> findAllCreatorProjects() {
    val creatorProjects = getUser().getCreatorProjects();
    if (creatorProjects.isEmpty()) {
      log.warn("No creator projects found in database for user '%s'.".formatted(getUser().getUsername()));
      return Collections.emptyList();
    }
    log.info("Found '%s' creator projects.".formatted(creatorProjects.size()));
    return creatorProjects.stream().map(ProjectMapper::convertProjectToProjectDto).toList();
  }
}
