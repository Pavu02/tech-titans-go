package com.examly.springapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatResponseDTO(
    @NotBlank(message = "Reply is required")
    String reply,

    @NotNull(message = "Matched status is required")
    Boolean matched,

    String matchedQuestion,

    @NotBlank(message = "Category is required")
    String category,

    @NotNull(message = "Confidence score is required")
    Double confidence,

    @NotBlank(message = "Source is required")
    String source,

    @NotBlank(message = "Session ID is required")
    String sessionId,

    String resolvedQuestion
) {}
