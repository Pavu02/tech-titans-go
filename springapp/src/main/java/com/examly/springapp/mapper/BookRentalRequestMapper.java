package com.examly.springapp.mapper;

import com.examly.springapp.dto.BookRentalRequestDTO;
import com.examly.springapp.model.Book;
import com.examly.springapp.model.BookRentalRequest;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.BookRepo;
import com.examly.springapp.repository.UserRepo;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookRentalRequestMapper {

    private final UserRepo userRepo;
    private final BookRepo bookRepo;

    public BookRentalRequestMapper(UserRepo userRepo, BookRepo bookRepo) {
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
    }

    public BookRentalRequest toEntity(BookRentalRequestDTO dto) {
        BookRentalRequest request = new BookRentalRequest();

        Long userId = (dto.user() != null) ? dto.user().userId() : null;
        Long bookId = (dto.book() != null) ? dto.book().bookId() : null;

        if (userId != null) {
            User user = userRepo.findById(userId).orElse(null);
            request.setUser(user);
        }
        if (bookId != null) {
            Book book = bookRepo.findById(bookId).orElse(null);
            request.setBook(book);
        }

        request.setRequestDate(dto.requestDate() != null ? dto.requestDate() : LocalDate.now());
        request.setReturnDate(dto.returnDate());
        request.setStatus(dto.status() == null || dto.status().trim().isEmpty() ? "Pending" : dto.status());
        request.setComments(dto.comments());

        return request;
    }
}
