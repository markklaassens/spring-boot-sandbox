package com.example.api.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record UserResponseDto(@NonNull String username) {

}
