package com.examly.springapp.controller;

import com.examly.springapp.model.BookRentalRequest;
import com.examly.springapp.service.BookRentalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookrentalrequest")
public class BookRentalRequestController {

    @Autowired
    private BookRentalRequestService rentalRequestService;

    @PostMapping
    public ResponseEntity<?> addRentalRequest(@RequestBody BookRentalRequest request) {
        try {
            BookRentalRequest created = rentalRequestService.addBookRentalRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<BookRentalRequest>> getAllRentalRequests() {
        List<BookRentalRequest> list = rentalRequestService.getAllBookRentalRequests();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRentalRequestsByUserId(@PathVariable Long userId) {
        List<BookRentalRequest> list = rentalRequestService.getBookRentalRequestsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRentalRequestById(@PathVariable Long id) {
        Optional<BookRentalRequest> req = rentalRequestService.getBookRentalRequestById(id);
        if (req.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(req.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental request not found");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRentalRequest(@PathVariable Long id, @RequestBody BookRentalRequest request) {
        BookRentalRequest updated = rentalRequestService.updateBookRentalRequest(id, request);
        if (updated != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental request not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRentalRequest(@PathVariable Long id) {
        Optional<BookRentalRequest> existing = rentalRequestService.getBookRentalRequestById(id);
        if (existing.isPresent()) {
            rentalRequestService.deleteBookRentalRequest(id);
            return ResponseEntity.status(HttpStatus.OK).body(existing.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental request not found");
        }
    }
}
