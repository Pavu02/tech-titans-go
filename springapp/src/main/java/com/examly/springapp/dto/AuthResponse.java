package com.examly.springapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AuthResponse(
    @NotBlank(message = "Token cannot be blank")
    String token,

    @NotNull(message = "User ID cannot be null")
    Long userId,

    @NotBlank(message = "Email cannot be blank")
    String email,

    @NotBlank(message = "Username cannot be blank")
    String username,

    @NotBlank(message = "Mobile number cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number should be exactly 10 digits")
    String mobileNumber,

    @NotBlank(message = "User role cannot be blank")
    String userRole
) {}
