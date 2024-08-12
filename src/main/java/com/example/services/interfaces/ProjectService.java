package com.example.services.interfaces;

import com.example.api.dto.ProjectDto;
import java.util.List;

public interface ProjectService {

  ProjectDto saveProject(ProjectDto project);

  List<ProjectDto> findAllProjects();
}
