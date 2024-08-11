package com.example;

import static com.example.config.ApplicationConstants.COLLABORATIVE;

import com.example.persistence.entities.ProjectType;
import lombok.experimental.UtilityClass;

@SuppressWarnings("java:S1118")
@UtilityClass
public class TestConstants {

  public static final ProjectType PROJ_TYPE_COLLABORATIVE = ProjectType.builder()
      .projectTypeId(1)
      .projectTypeValue(COLLABORATIVE)
      .build();
}
