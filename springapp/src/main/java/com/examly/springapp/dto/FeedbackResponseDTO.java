package com.examly.springapp.dto;

import java.time.LocalDate;

public record FeedbackResponseDTO(
    Long feedbackId,
    String feedbackText,
    LocalDate date,
    Long userId,
    String username
) {}
