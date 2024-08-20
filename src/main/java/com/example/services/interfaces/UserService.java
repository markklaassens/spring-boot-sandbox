package com.example.services.interfaces;

import com.example.api.dto.ProjectDto;
import com.example.api.dto.UserDto;
import com.example.persistence.entities.User;
import java.util.List;

public interface UserService {

  User getUser();

  List<ProjectDto> findAllCreatorProjects();

  List<ProjectDto> findAllUserProjects();

  String registerUser(UserDto user);
}
