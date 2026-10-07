package com.examly.springapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserResponseDTO(
    @NotNull(message = "User ID cannot be null")
    Long userId,

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,

    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number should be exactly 10 digits")
    String mobileNumber,

    @NotBlank(message = "User role is required")
    String userRole
) {}
