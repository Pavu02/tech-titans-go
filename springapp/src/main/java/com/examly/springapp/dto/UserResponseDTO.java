package com.examly.springapp.dto;

public record UserResponseDTO(
    Long userId,
    String email,
    String username,
    String mobileNumber,
    String userRole
) {}
