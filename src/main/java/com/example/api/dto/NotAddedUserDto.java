package com.example.api.dto;

import lombok.Builder;

/**
 * Data Transfer Object (DTO) representing a project and users to add.
 */
@Builder
public record NotAddedUserDto(

    String username,

    String reason
) {

}
