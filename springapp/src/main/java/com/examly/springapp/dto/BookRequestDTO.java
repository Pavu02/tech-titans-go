package com.examly.springapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequestDTO(
    @NotBlank(message = "Title is required")
    String title,
    
    @NotBlank(message = "Author is required")
    String author,
    
    @NotBlank(message = "Genre is required")
    String genre,
    
    @NotBlank(message = "Description is required")
    String description,
    
    @NotNull(message = "Rental fee is required")
    Double rentalFee,
    
    Boolean isAvailable,
    String coverImage
) {}
