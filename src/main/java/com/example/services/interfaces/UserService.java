package com.example.services.interfaces;

import com.example.api.dto.ProjectDto;
import com.example.persistence.entities.User;
import java.util.List;

public interface UserService {

  User getUser();

  List<ProjectDto> findAllCreatorProjects();
}
