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

    // Constructor Injection
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<?> createFeedback(@Valid @RequestBody FeedbackRequestDTO feedbackDTO) {
        Feedback created = feedbackService.createFeedback(feedbackDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        List<Feedback> list = feedbackService.getAllFeedbacks();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedbackById(@PathVariable Long id) {
        Optional<Feedback> fb = feedbackService.getFeedbackById(id);
        if (fb.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(fb.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not found");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFeedbacksByUserId(@PathVariable Long userId) {
        List<Feedback> list = feedbackService.getFeedbacksByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        Feedback deleted = feedbackService.deleteFeedback(id);
        if (deleted != null) {
            return ResponseEntity.status(HttpStatus.OK).body(deleted);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not found");
        }
    }
}
