package com.examly.springapp.service;

import com.examly.springapp.model.Book;
import com.examly.springapp.model.BookRentalRequest;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.BookRentalRequestRepo;
import com.examly.springapp.repository.BookRepo;
import com.examly.springapp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookRentalRequestServiceImpl implements BookRentalRequestService {

    @Autowired
    private BookRentalRequestRepo rentalRequestRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BookRepo bookRepo;

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
    public BookRentalRequest addBookRentalRequest(BookRentalRequest request) {
        Long userId = request.getUser() != null ? request.getUser().getUserId() : null;
        Long bookId = request.getBook() != null ? request.getBook().getBookId() : null;

        if (userId != null) {
            User user = userRepo.findById(userId).orElse(request.getUser());
            request.setUser(user);
        }
        if (bookId != null) {
            Book book = bookRepo.findById(bookId).orElse(request.getBook());
            request.setBook(book);
        }

        if (userId != null && bookId != null) {
            boolean exists = rentalRequestRepo.existsByUserUserIdAndBookBookIdAndStatusIn(
                    userId, bookId, Arrays.asList("Pending", "Approved"));
            if (exists) {
                throw new IllegalArgumentException("A request already exists for the same book by the user");
            }
        }

        if (request.getRequestDate() == null) {
            request.setRequestDate(LocalDate.now());
        }
        if (request.getReturnDate() != null && request.getReturnDate().isBefore(request.getRequestDate())) {
            throw new IllegalArgumentException("Return date cannot be in the past");
        }
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            request.setStatus("Pending");
        }

        return rentalRequestRepo.save(request);
    }

    @Override
    public BookRentalRequest updateBookRentalRequest(Long requestId, BookRentalRequest request) {
        BookRentalRequest existing = rentalRequestRepo.findById(requestId)
                .orElse(null);
        if (existing == null) {
            return null;
        }

        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (request.getReturnDate() != null) {
            existing.setReturnDate(request.getReturnDate());
        }
        if (request.getComments() != null) {
            existing.setComments(request.getComments());
        }
        if (request.getRequestDate() != null) {
            existing.setRequestDate(request.getRequestDate());
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
