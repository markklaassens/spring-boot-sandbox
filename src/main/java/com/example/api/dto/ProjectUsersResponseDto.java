package com.example.api.dto;

import java.util.List;
import lombok.Builder;

/**
 * Data Transfer Object (DTO) representing a project including the added and not added users.
 */
@Builder
public record ProjectUsersResponseDto(

    String projectName,

    List<String> addedUsers,

    List<NotAddedUserDto> notAddedUsers
) {

}
