package com.examly.springapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record FeedbackResponseDTO(
    @NotNull(message = "Feedback ID is required")
    Long feedbackId,

    @NotBlank(message = "Feedback text cannot be empty")
    String feedbackText,

    @NotNull(message = "Date is required")
    LocalDate date,

    @NotNull(message = "User ID is required")
    Long userId,

    @NotBlank(message = "Username is required")
    String username
) {}
