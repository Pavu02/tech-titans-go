package com.examly.springapp.dto;

public record AuthResponse(
    String token,
    Long userId,
    String email,
    String username,
    String mobileNumber,
    String userRole
) {}
