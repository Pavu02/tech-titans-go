package com.examly.springapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordDTO(
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,

    @NotBlank(message = "OTP is required")
    String otp,

    @NotBlank(message = "New password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    String newPassword,

    @NotBlank(message = "Confirm password is required")
    String confirmPassword
) {}
