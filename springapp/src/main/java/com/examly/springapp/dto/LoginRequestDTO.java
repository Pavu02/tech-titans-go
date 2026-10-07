package com.examly.springapp.dto;

public record LoginRequestDTO(
    String email,
    String password
) {}
