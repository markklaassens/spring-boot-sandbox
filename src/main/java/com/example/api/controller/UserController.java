package com.example.api.controller;

import com.example.api.dto.ProjectDto;
import com.example.api.dto.UserDto;
import com.example.services.interfaces.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  ResponseEntity<String> saveUser(@Valid @RequestBody UserDto user) {
    val registeredUsername = userService.registerUser(user);
    return new ResponseEntity<>(registeredUsername, HttpStatus.CREATED);
  }

  @GetMapping("/creator-projects")
  @PreAuthorize("hasRole('CREATOR')")
  ResponseEntity<List<ProjectDto>> getAllCreatorProjects() {
    val creatorProjectsList = userService.findAllCreatorProjects();
    return ResponseEntity.ok(creatorProjectsList);
  }

  @GetMapping("/user-projects")
  @PreAuthorize("hasRole('USER')")
  ResponseEntity<List<ProjectDto>> getAllUserProjects() {
    val userProjectList = userService.findAllUserProjects();
    return ResponseEntity.ok(userProjectList);
  }
}
