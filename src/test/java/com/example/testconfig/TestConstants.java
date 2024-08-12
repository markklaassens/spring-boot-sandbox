package com.example.testconfig;

import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static com.example.config.ApplicationConstants.COMPETITIVE;

import com.example.api.dto.ProjectDto;
import com.example.persistence.entities.Project;
import com.example.persistence.entities.ProjectType;
import java.util.List;
import lombok.experimental.UtilityClass;

@SuppressWarnings("java:S1118")
@UtilityClass
public class TestConstants {

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

  public static final Project PROJECT = Project.builder()
      .projectId(1)
      .projectName("Ultimate Tic-Tac-Toe")
      .projectDescription("Project for collaborating and developing the game Ultimate Tic-Tac-Toe.")
      .projectType(PROJ_TYPE_COLLABORATIVE)
      .build();

  public static final Project PROJECT2 = Project.builder()
      .projectId(2)
      .projectName("Tetris Blocks")
      .projectDescription("Project for competing and solving the puzzle Tetris BLocks.")
      .projectType(PROJ_TYPE_COMPETITIVE)
      .build();

  public static final List<Project> PROJECT_LIST = List.of(PROJECT, PROJECT2);
}
