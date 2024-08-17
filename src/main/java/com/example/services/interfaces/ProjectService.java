package com.example.services.interfaces;

import com.example.api.dto.ProjectDto;
import com.example.api.dto.ProjectUsersDto;
import com.example.api.dto.ProjectUsersResponseDto;
import java.util.List;

public interface ProjectService {

  ProjectDto saveProject(ProjectDto project);

  List<ProjectDto> findAllProjects();

  ProjectUsersResponseDto addUsersToProject(ProjectUsersDto projectUsersDto);
}
