package com.example.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic REST controller with one endpoint that handles GET requests.
 */
@RestController
@Slf4j
@RequestMapping("/projects")
public class ProjectServiceController {

  @GetMapping
  String getAllProjects() {
    log.info("Returning all projects");
    return "All projects";
  }

  @PostMapping
  String addProject() {
    log.info("Added project");
    return "Added project";
  }
}
