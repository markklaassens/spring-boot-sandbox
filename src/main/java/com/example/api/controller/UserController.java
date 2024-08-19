package com.example.api.controller;

import com.example.api.dto.ProjectDto;
import com.example.services.interfaces.UserService;
import java.util.List;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/creator-projects")
  @PreAuthorize("hasRole('CREATOR')")
  ResponseEntity<List<ProjectDto>> getAllCreatorProjects() {
    val projectDtoList = userService.findAllCreatorProjects();
    return ResponseEntity.ok(projectDtoList);
  }
}
