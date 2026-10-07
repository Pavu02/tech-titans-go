package com.examly.springapp.dto;

import java.time.LocalDate;

public record BookRentalRequestDTO(
    UserRef user,
    BookRef book,
    LocalDate requestDate,
    LocalDate returnDate,
    String status,
    String comments
) {
    public record UserRef(Long userId) {}
    public record BookRef(Long bookId) {}
}
