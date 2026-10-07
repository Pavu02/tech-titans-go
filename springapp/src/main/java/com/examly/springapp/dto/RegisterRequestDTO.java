package com.examly.springapp.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
    @NotBlank(message = "Email is required")
    String email,
    
    @NotBlank(message = "Password is required")
    String password,
    
    @NotBlank(message = "Username is required")
    String username,
    
    @NotBlank(message = "Mobile number is required")
    String mobileNumber,
    
    @NotBlank(message = "User role is required")
    String userRole
) {}
