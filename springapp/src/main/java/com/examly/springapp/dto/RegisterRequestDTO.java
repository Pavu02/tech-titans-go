package com.examly.springapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequestDTO(
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,
    
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "Password must be at least 8 characters long and contain a number and special character")
    String password,
    
    @NotBlank(message = "Username is required")
    String username,
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number should be exactly 10 digits")
    String mobileNumber,

    String otp
) {}
