package com.examly.springapp.controller;

import com.examly.springapp.dto.FeedbackRequestDTO;
import com.examly.springapp.model.Feedback;
import com.examly.springapp.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final com.examly.springapp.repository.UserRepo userRepo;

    // Constructor Injection
    public FeedbackController(FeedbackService feedbackService, com.examly.springapp.repository.UserRepo userRepo) {
        this.feedbackService = feedbackService;
        this.userRepo = userRepo;
    }

    @PostMapping
    public ResponseEntity<?> createFeedback(@Valid @RequestBody FeedbackRequestDTO feedbackDTO) {
        try {
            Feedback created = feedbackService.createFeedback(feedbackDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            // Returns 201
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            // Returns 400
        }
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        List<Feedback> list = feedbackService.getAllFeedbacks();
        return ResponseEntity.status(HttpStatus.OK).body(list);
        // Returns 200
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedbackById(@PathVariable Long id) {
        Optional<Feedback> fb = feedbackService.getFeedbackById(id);
        if (fb.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(fb.get());
            // Returns 200
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not found");
            // Returns 404
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFeedbacksByUserId(@PathVariable Long userId) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
        
        if (!isAdmin) {
            String email = auth.getName();
            java.util.Optional<com.examly.springapp.model.User> loggedInUser = userRepo.findByEmail(email);
            if (loggedInUser.isEmpty() || !loggedInUser.get().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
        }

        List<Feedback> list = feedbackService.getFeedbacksByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
        // Returns 200
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        Feedback deleted = feedbackService.deleteFeedback(id);
        if (deleted != null) {
            return ResponseEntity.status(HttpStatus.OK).body(deleted);
            // Returns 200
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not found");
            // Returns 404
        }
    }
}
