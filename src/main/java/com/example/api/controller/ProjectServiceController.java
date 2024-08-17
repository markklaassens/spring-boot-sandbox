package com.example.api.controller;

import com.example.api.dto.ProjectDto;
import com.example.api.dto.ProjectUsersDto;
import com.example.api.dto.ProjectUsersResponseDto;
import com.example.services.interfaces.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/projects")
public class ProjectServiceController {

  private final ProjectService projectService;

  public ProjectServiceController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @PostMapping
  @PreAuthorize("hasRole('CREATOR')")
  ResponseEntity<ProjectDto> addProject(@Valid @RequestBody ProjectDto projectDto) {
    val createdProject = projectService.saveProject(projectDto);
    return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
  }

  @GetMapping
  @PreAuthorize("hasRole('USER')")
  ResponseEntity<List<ProjectDto>> getAllProjects() {
    val projectDtoList = projectService.findAllProjects();
    return ResponseEntity.ok(projectDtoList);
  }

  @PutMapping
  @PreAuthorize("hasRole('CREATOR')")
  ResponseEntity<ProjectUsersResponseDto> updateProjectUsers(@Valid @RequestBody ProjectUsersDto projectUsersDto) {
    val resultProjectUsersDto = projectService.addUsersToProject(projectUsersDto);
    return ResponseEntity.ok(resultProjectUsersDto);
  }
}
