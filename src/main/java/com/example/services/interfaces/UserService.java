package com.example.services.interfaces;

import com.example.api.dto.ProjectDto;
import com.example.api.dto.UserDto;
import com.example.api.dto.UserResponseDto;
import com.example.persistence.entities.User;
import java.util.List;

public interface UserService {


  UserResponseDto registerUser(UserDto user);

  List<UserResponseDto> findAllUsers();

  List<ProjectDto> findAllCreatorProjects();

  List<ProjectDto> findAllUserProjects();

  User getUser();

}
