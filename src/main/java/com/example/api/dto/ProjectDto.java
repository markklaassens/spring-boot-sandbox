package com.example.api.dto;

import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static com.example.config.ApplicationConstants.COMPETITIVE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Data Transfer Object (DTO) representing a project.
 */
@Builder
public record ProjectDto(

    @NotEmpty(message = "Project name is mandatory.")
    @Size(max = 300, message = "Project name can have a maximum of 300 characters.")
    String projectName,

    @NotEmpty(message = "Project description is mandatory.")
    @Size(max = 1000, message = "Project description can have a maximum of 1000 characters.")
    String projectDescription,

    @Schema(example = COLLABORATIVE, allowableValues = {
        COLLABORATIVE,
        COMPETITIVE,
    })
    @NotEmpty(message = "Project type is mandatory.")
    String projectType
) {

}
