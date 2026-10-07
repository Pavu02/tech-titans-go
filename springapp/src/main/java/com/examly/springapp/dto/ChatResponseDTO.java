package com.examly.springapp.dto;

public record ChatResponseDTO(
    String reply,
    Boolean matched,
    String matchedQuestion,
    String category,
    Double confidence,
    String source,
    String sessionId,
    String resolvedQuestion
) {}
