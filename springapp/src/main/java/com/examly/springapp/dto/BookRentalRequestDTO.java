package com.examly.springapp.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BookRentalRequestDTO(
    @NotNull(message = "User reference is required")
    UserRef user,
    
    @NotNull(message = "Book reference is required")
    BookRef book,
    
    @NotNull(message = "Request date is required")
    LocalDate requestDate,
    
    @NotNull(message = "Return date is required")
    LocalDate returnDate,
    
    String status,
    String comments
) {
    public record UserRef(Long userId) {}
    public record BookRef(Long bookId) {}
}
