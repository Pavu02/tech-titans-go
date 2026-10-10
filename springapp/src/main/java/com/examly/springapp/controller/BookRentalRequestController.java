package com.examly.springapp.controller;

import com.examly.springapp.dto.BookRentalRequestDTO;
import com.examly.springapp.model.BookRentalRequest;
import com.examly.springapp.service.BookRentalRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookrentalrequest")
public class BookRentalRequestController {

    private final BookRentalRequestService rentalRequestService;
    private final com.examly.springapp.repository.UserRepo userRepo;

    // Constructor Injection
    public BookRentalRequestController(BookRentalRequestService rentalRequestService, com.examly.springapp.repository.UserRepo userRepo) {
        this.rentalRequestService = rentalRequestService;
        this.userRepo = userRepo;
    }

    @PostMapping
    public ResponseEntity<?> addRentalRequest(@Valid @RequestBody BookRentalRequestDTO requestDTO) {
        try {
            BookRentalRequest created = rentalRequestService.addBookRentalRequest(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            // Returns 201
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            // Returns 400
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            // Returns 500
        }
    }

    @GetMapping
    public ResponseEntity<List<BookRentalRequest>> getAllRentalRequests() {
        List<BookRentalRequest> list = rentalRequestService.getAllBookRentalRequests();
        return ResponseEntity.status(HttpStatus.OK).body(list);
        // Returns 200
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRentalRequestsByUserId(@PathVariable Long userId) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
        
        if (!isAdmin) {
            String email = auth.getName();
            java.util.Optional<com.examly.springapp.model.User> loggedInUser = userRepo.findByEmail(email);
            if (loggedInUser.isEmpty() || !loggedInUser.get().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
        }

        List<BookRentalRequest> list = rentalRequestService.getBookRentalRequestsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
        // Returns 200
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getRentalRequestById(@PathVariable Long requestId) {
        Optional<BookRentalRequest> req = rentalRequestService.getBookRentalRequestById(requestId);
        if (req.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(req.get());
            // Returns 200
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental request not found");
            // Returns 404
        }
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<?> updateRentalRequest(@PathVariable Long requestId, @Valid @RequestBody BookRentalRequestDTO requestDTO) {
        try {
            BookRentalRequest updated = rentalRequestService.updateBookRentalRequest(requestId, requestDTO);
            if (updated != null) {
                return ResponseEntity.status(HttpStatus.OK).body(updated);
                // Returns 200
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental request not found");
                // Returns 404
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            // Returns 500
        }
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<?> deleteRentalRequest(@PathVariable Long requestId) {
        boolean deleted = rentalRequestService.deleteBookRentalRequest(requestId);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
            // Returns 200
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental request not found");
            // Returns 404
        }
    }
}
