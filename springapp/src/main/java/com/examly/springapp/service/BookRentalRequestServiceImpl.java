package com.examly.springapp.service;

import com.examly.springapp.dto.BookRentalRequestDTO;
import com.examly.springapp.model.Book;
import com.examly.springapp.model.BookRentalRequest;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.BookRentalRequestRepo;
import com.examly.springapp.repository.BookRepo;
import com.examly.springapp.repository.UserRepo;
import com.examly.springapp.mapper.BookRentalRequestMapper;
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
    private final BookRentalRequestMapper requestMapper;

    // Constructor Injection
    public BookRentalRequestServiceImpl(BookRentalRequestRepo rentalRequestRepo, UserRepo userRepo, BookRepo bookRepo,
            BookRentalRequestMapper requestMapper) {
        this.rentalRequestRepo = rentalRequestRepo;
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
        this.requestMapper = requestMapper;
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

        if (userId != null && bookId != null) {
            boolean exists = rentalRequestRepo.existsByUserUserIdAndBookBookIdAndStatusIn(
                    userId, bookId, Arrays.asList("Pending", "Approved"));
            if (exists) {
                throw new IllegalArgumentException("A request already exists for the same book by the user");
            }
        }

        BookRentalRequest request = requestMapper.toEntity(requestDTO);

        if (request.getReturnDate() != null && request.getReturnDate().isBefore(request.getRequestDate())) {
            throw new IllegalArgumentException("Return date cannot be in the past");
        }

        return rentalRequestRepo.save(request);
    }

    @Override
    public BookRentalRequest updateBookRentalRequest(Long requestId, BookRentalRequestDTO requestDTO) {
        BookRentalRequest existing = rentalRequestRepo.findById(requestId).orElse(null);
        if (existing == null) {
            return null;
        }

        if (requestDTO.status() != null) {
            if ("Approved".equalsIgnoreCase(requestDTO.status())
                    && !"Approved".equalsIgnoreCase(existing.getStatus())) {
                if (existing.getBook() != null) {
                    boolean alreadyApproved = rentalRequestRepo.existsByBookBookIdAndStatusIn(
                            existing.getBook().getBookId(), Arrays.asList("Approved"));
                    if (alreadyApproved) {
                        throw new IllegalArgumentException("This book has already been approved for another user.");
                    }

                    // Auto-reject all other pending requests for the same book
                    List<BookRentalRequest> pendingRequests = rentalRequestRepo
                            .findByBookBookIdAndStatus(existing.getBook().getBookId(), "Pending");
                    for (BookRentalRequest pending : pendingRequests) {
                        if (!pending.getRentalId().equals(existing.getRentalId())) {
                            pending.setStatus("Rejected");
                            // Add a generic rejection comment for clarity
                            pending.setComments(pending.getComments() != null
                                    ? pending.getComments() + " [Auto-Rejected: Book already approved for another user]"
                                    : "[Auto-Rejected: Book already approved for another user]");
                            rentalRequestRepo.save(pending);
                        }
                    }
                }
            }
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

// "OK": "200",
// "CREATED": "201",
// "NO_CONTENT": "204",
// "BAD_REQUEST": "400",
// "UNAUTHORIZED": "401",
// "FORBIDDEN": "403",
// "NOT_FOUND": "404",
// "CONFLICT": "409",
// "INTERNAL_SERVER_ERROR": "500"