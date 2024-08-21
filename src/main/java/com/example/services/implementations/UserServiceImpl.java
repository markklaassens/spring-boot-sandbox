package com.example.services.implementations;

import static com.example.config.ApplicationConstants.ROLE_USER;
import static com.example.mapper.UserMapper.convertUserDtoToUser;

import com.example.api.dto.ProjectDto;
import com.example.api.dto.UserDto;
import com.example.api.dto.UserResponseDto;
import com.example.exceptions.UserNotFoundException;
import com.example.exceptions.UserRoleNotFoundException;
import com.example.exceptions.UsernameAlreadyExistsException;
import com.example.mapper.ProjectMapper;
import com.example.mapper.UserMapper;
import com.example.persistence.entities.User;
import com.example.persistence.entities.UserRole;
import com.example.persistence.repositories.UserRepository;
import com.example.persistence.repositories.UserRoleRepository;
import com.example.services.interfaces.UserService;
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
  private final UserRoleRepository userRoleRepository;

  public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository) {
    this.userRepository = userRepository;
    this.userRoleRepository = userRoleRepository;
  }

  @Override
  public UserResponseDto registerUser(UserDto userDto) {
    if (userRepository.findByUsername(userDto.username()).isPresent()) {
      log.error("User with username '%s' already exists.".formatted(userDto.username()));
      throw new UsernameAlreadyExistsException("User with username '%s' already exists.".formatted(userDto.username()));
    }
    userRepository.save(convertUserDtoToUser(userDto, getStandardUserRole()));
    log.info("Created new user '%s'.".formatted(userDto.username()));
    return UserResponseDto.builder().username(userDto.username()).build();
  }

  @Override
  public List<UserResponseDto> findAllUsers() {
    val userList = userRepository.findAll();
    if (userList.isEmpty()) {
      log.warn("No users found.");
      return Collections.emptyList();
    }
    log.info("Found '%s' users.".formatted(userList.size()));
    return userRepository.findAll().stream().map(UserMapper::convertUserToUserResponseDto).toList();
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

  @Override
  @Transactional
  public List<ProjectDto> findAllUserProjects() {
    val userProjects = getUser().getUserProjects();
    if (userProjects.isEmpty()) {
      log.warn("No user projects found in database for user '%s'.".formatted(getUser().getUsername()));
      return Collections.emptyList();
    }
    log.info("Found '%s' user projects.".formatted(userProjects.size()));
    return userProjects.stream().map(ProjectMapper::convertProjectToProjectDto).toList();
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

  private UserRole getStandardUserRole() {
    val userRole = userRoleRepository.findByUserRoleValue(ROLE_USER);
    if (userRole.isEmpty()) {
      log.error("User role '%s' not found in database.".formatted(ROLE_USER));
      throw new UserRoleNotFoundException("User role '%s' not found in database.".formatted(ROLE_USER));
    }
    return userRole.get();
  }
}
