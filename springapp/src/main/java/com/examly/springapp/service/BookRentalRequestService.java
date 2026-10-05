package com.examly.springapp.service;

import com.examly.springapp.model.BookRentalRequest;

import java.util.List;
import java.util.Optional;

public interface BookRentalRequestService {
    List<BookRentalRequest> getAllBookRentalRequests();
    List<BookRentalRequest> getBookRentalRequestsByUserId(Long userId);
    Optional<BookRentalRequest> getBookRentalRequestById(Long requestId);
    BookRentalRequest addBookRentalRequest(BookRentalRequest request);
    BookRentalRequest updateBookRentalRequest(Long requestId, BookRentalRequest request);
    boolean deleteBookRentalRequest(Long requestId);
}
