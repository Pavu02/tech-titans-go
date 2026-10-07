package com.examly.springapp.service;

import com.examly.springapp.dto.BookRentalRequestDTO;
import com.examly.springapp.model.Book;
import com.examly.springapp.model.BookRentalRequest;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.BookRentalRequestRepo;
import com.examly.springapp.repository.BookRepo;
import com.examly.springapp.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookRentalRequestServiceImpl implements BookRentalRequestService {

    private final BookRentalRequestRepo rentalRequestRepo;
    private final UserRepo userRepo;
    private final BookRepo bookRepo;

    // Constructor Injection
    public BookRentalRequestServiceImpl(BookRentalRequestRepo rentalRequestRepo, UserRepo userRepo, BookRepo bookRepo) {
        this.rentalRequestRepo = rentalRequestRepo;
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
    }

    @Override
    public List<BookRentalRequest> getAllBookRentalRequests() {
        return rentalRequestRepo.findAll();
    }

    @Override
    public List<BookRentalRequest> getBookRentalRequestsByUserId(Long userId) {
        return rentalRequestRepo.findByUserUserId(userId);
    }

    @Override
    public Optional<BookRentalRequest> getBookRentalRequestById(Long requestId) {
        return rentalRequestRepo.findById(requestId);
    }

    @Override
    public BookRentalRequest addBookRentalRequest(BookRentalRequestDTO requestDTO) {
        Long userId = (requestDTO.user() != null) ? requestDTO.user().userId() : null;
        Long bookId = (requestDTO.book() != null) ? requestDTO.book().bookId() : null;

        BookRentalRequest request = new BookRentalRequest();

        if (userId != null) {
            User user = userRepo.findById(userId).orElse(null);
            request.setUser(user);
        }
        if (bookId != null) {
            Book book = bookRepo.findById(bookId).orElse(null);
            request.setBook(book);
        }

        if (userId != null && bookId != null) {
            boolean exists = rentalRequestRepo.existsByUserUserIdAndBookBookIdAndStatusIn(
                    userId, bookId, Arrays.asList("Pending", "Approved"));
            if (exists) {
                throw new IllegalArgumentException("A request already exists for the same book by the user");
            }
        }

        request.setRequestDate(requestDTO.requestDate() != null ? requestDTO.requestDate() : LocalDate.now());
        
        if (requestDTO.returnDate() != null && requestDTO.returnDate().isBefore(request.getRequestDate())) {
            throw new IllegalArgumentException("Return date cannot be in the past");
        }
        request.setReturnDate(requestDTO.returnDate());
        
        request.setStatus(requestDTO.status() == null || requestDTO.status().trim().isEmpty() ? "Pending" : requestDTO.status());
        request.setComments(requestDTO.comments());

        return rentalRequestRepo.save(request);
    }

    @Override
    public BookRentalRequest updateBookRentalRequest(Long requestId, BookRentalRequestDTO requestDTO) {
        BookRentalRequest existing = rentalRequestRepo.findById(requestId).orElse(null);
        if (existing == null) {
            return null;
        }

        if (requestDTO.status() != null) {
            existing.setStatus(requestDTO.status());
        }
        if (requestDTO.returnDate() != null) {
            existing.setReturnDate(requestDTO.returnDate());
        }
        if (requestDTO.comments() != null) {
            existing.setComments(requestDTO.comments());
        }
        if (requestDTO.requestDate() != null) {
            existing.setRequestDate(requestDTO.requestDate());
        }

        return rentalRequestRepo.save(existing);
    }

    @Override
    public boolean deleteBookRentalRequest(Long requestId) {
        if (rentalRequestRepo.existsById(requestId)) {
            rentalRequestRepo.deleteById(requestId);
            return true;
        }
        return false;
    }
}
