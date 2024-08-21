package com.example;

import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static com.example.config.ApplicationConstants.COMPETITIVE;

import com.example.api.dto.NotAddedUserDto;
import com.example.api.dto.ProjectDto;
import com.example.api.dto.ProjectUsersDto;
import com.example.api.dto.ProjectUsersResponseDto;
import com.example.api.dto.UserDto;
import com.example.api.dto.UserResponseDto;
import com.example.persistence.entities.Project;
import com.example.persistence.entities.ProjectType;
import com.example.persistence.entities.User;
import com.example.persistence.entities.UserRole;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;

@SuppressWarnings("java:S1118")
@UtilityClass
public class TestConstants {

  public static final String CREATOR = "CREATOR";
  public static final String USER = "USER";

  public static final ProjectType PROJ_TYPE_COLLABORATIVE = ProjectType.builder()
      .projectTypeId(1)
      .projectTypeValue(COLLABORATIVE)
      .build();

  public static final ProjectType PROJ_TYPE_COMPETITIVE = ProjectType.builder()
      .projectTypeId(2)
      .projectTypeValue(COMPETITIVE)
      .build();

  public static final ProjectDto PROJECT_DTO = ProjectDto.builder()
      .projectName("Ultimate Tic-Tac-Toe")
      .projectDescription("Project for collaborating and developing the game Ultimate Tic-Tac-Toe.")
      .projectType(COLLABORATIVE)
      .build();

  public static final ProjectDto PROJECT_DTO2 = ProjectDto.builder()
      .projectName("Tetris Blocks")
      .projectDescription("Project for competing and solving the puzzle Tetris Blocks.")
      .projectType(COMPETITIVE)
      .build();

  public static final List<ProjectDto> PROJECT_DTO_LIST = List.of(PROJECT_DTO, PROJECT_DTO2);

  public static final UserRole USER_ROLE_CREATOR = UserRole.builder()
      .userRoleId(1)
      .userRoleValue(CREATOR)
      .build();

  public static final User CREATOR_USER = User.builder()
      .userId(1)
      .username("creator")
      .userPassword("test123")
      .userRoles(Set.of(USER_ROLE_CREATOR))
      .creatorProjects(new HashSet<>())
      .userProjects(new HashSet<>())
      .build();

  public static final Project PROJECT = Project.builder()
      .projectId(1)
      .projectName("Ultimate Tic-Tac-Toe")
      .projectDescription("Project for collaborating and developing the game Ultimate Tic-Tac-Toe.")
      .projectType(PROJ_TYPE_COLLABORATIVE)
      .projectCreator(CREATOR_USER)
      .projectUsers(new HashSet<>())
      .build();

  public static final Project PROJECT2 = Project.builder()
      .projectId(2)
      .projectName("Tetris Blocks")
      .projectDescription("Project for competing and solving the puzzle Tetris Blocks.")
      .projectType(PROJ_TYPE_COMPETITIVE)
      .projectCreator(CREATOR_USER)
      .projectUsers(new HashSet<>())
      .build();

  public static final List<Project> PROJECT_LIST = List.of(PROJECT, PROJECT2);

  public static final UserRole USER_ROLE_USER = UserRole.builder()
      .userRoleId(2)
      .userRoleValue(USER)
      .users(new HashSet<>())
      .build();

  public static final User REGULAR_USER = User.builder()
      .userId(2)
      .username("user")
      .userPassword("test123")
      .userRoles(new HashSet<>())
      .creatorProjects(new HashSet<>())
      .userProjects(new HashSet<>())
      .build();

  public static final User NEW_USER = User.builder()
      .userId(3)
      .username("newuser")
      .userPassword("test123")
      .userRoles(new HashSet<>())
      .creatorProjects(new HashSet<>())
      .userProjects(new HashSet<>())
      .build();

  public static final ProjectUsersDto PROJECT_USERS_DTO = ProjectUsersDto.builder()
      .projectName("Ultimate Tic-Tac-Toe")
      .usernames(List.of("user", "user2", "user3", "user4", "user5"))
      .build();

  public static final NotAddedUserDto NOT_ADDED_USER_DTO1 = NotAddedUserDto.builder()
      .username("user4")
      .reason("User with username 'user4' is already added to project.")
      .build();

  public static final NotAddedUserDto NOT_ADDED_USER_DTO2 = NotAddedUserDto.builder()
      .username("user5")
      .reason("Could not find user with username 'user5'.")
      .build();

  public static final ProjectUsersResponseDto PROJECT_USERS_RESPONSE_DTO = ProjectUsersResponseDto.builder()
      .projectName("Ultimate Tic-Tac-Toe")
      .addedUsers(List.of("user", "user2", "user3"))
      .notAddedUsers(List.of(NOT_ADDED_USER_DTO1, NOT_ADDED_USER_DTO2))
      .build();

  public static final UserDto NEW_USER_DTO = UserDto.builder()
      .username("newuser")
      .userPassword("test123")
      .build();

  public static final UserResponseDto NEW_USER_RESPONSE_DTO = UserResponseDto.builder()
      .username("newuser")
      .build();

  public static final UserResponseDto REGULAR_USER_RESPONSE_DTO = UserResponseDto.builder()
      .username("user")
      .build();

  public static final UserResponseDto CREATOR_USER_RESPONSE_DTO = UserResponseDto.builder()
      .username("creator")
      .build();

  public static List<UserResponseDto> USER_RESPONSE_DTO_LIST = List.of(NEW_USER_RESPONSE_DTO,
      REGULAR_USER_RESPONSE_DTO, CREATOR_USER_RESPONSE_DTO);

  public static List<User> USER_LIST = List.of(NEW_USER, REGULAR_USER, CREATOR_USER);
}
