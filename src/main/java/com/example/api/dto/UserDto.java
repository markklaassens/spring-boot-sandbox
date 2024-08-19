package com.example.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserDto(

    @NotEmpty(message = "Username is mandatory.")
    @Size(max = 30, message = "Username can have a maximum of 30 characters.")
    String username,

    @NotEmpty(message = "Password is mandatory.")
    @Size(max = 100, message = "Password can have a maximum of 100 characters.")
    String userPassword
) {

}
