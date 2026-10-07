package com.examly.springapp.controller;

import com.examly.springapp.dto.BookRentalRequestDTO;
import com.examly.springapp.model.BookRentalRequest;
import com.examly.springapp.service.BookRentalRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookrentalrequest")
public class BookRentalRequestController {

    private final BookRentalRequestService rentalRequestService;

    // Constructor Injection
    public BookRentalRequestController(BookRentalRequestService rentalRequestService) {
        this.rentalRequestService = rentalRequestService;
    }

    @PostMapping
    public ResponseEntity<?> addRentalRequest(@RequestBody BookRentalRequestDTO requestDTO) {
        try {
            BookRentalRequest created = rentalRequestService.addBookRentalRequest(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<BookRentalRequest>> getAllRentalRequests() {
        List<BookRentalRequest> list = rentalRequestService.getAllBookRentalRequests();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookRentalRequest>> getRentalRequestsByUserId(@PathVariable Long userId) {
        List<BookRentalRequest> list = rentalRequestService.getBookRentalRequestsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getRentalRequestById(@PathVariable Long requestId) {
        Optional<BookRentalRequest> req = rentalRequestService.getBookRentalRequestById(requestId);
        if (req.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(req.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental request not found");
        }
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<?> updateRentalRequest(@PathVariable Long requestId, @RequestBody BookRentalRequestDTO requestDTO) {
        try {
            BookRentalRequest updated = rentalRequestService.updateBookRentalRequest(requestId, requestDTO);
            if (updated != null) {
                return ResponseEntity.status(HttpStatus.OK).body(updated);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental request not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<?> deleteRentalRequest(@PathVariable Long requestId) {
        boolean deleted = rentalRequestService.deleteBookRentalRequest(requestId);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental request not found");
        }
    }
}
