package com.examly.springapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FeedbackRequestDTO(
    @NotBlank(message = "Feedback text is required")
    String feedbackText,
    
    @NotNull(message = "User object is required")
    UserRef user
) {
    public record UserRef(Long userId) {}
}
