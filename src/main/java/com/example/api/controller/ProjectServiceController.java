package com.example.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic REST controller with one endpoint that handles GET requests.
 */
@RestController
@Slf4j
public class ProjectServiceController {

  @GetMapping()
  String showWelcomeMessage() {
    log.info("Showed welcome message");
    return "Welcome to the project service!";
  }
}