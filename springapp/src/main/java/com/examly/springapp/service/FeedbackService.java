package com.examly.springapp.service;

import com.examly.springapp.model.Feedback;

import java.util.List;
import java.util.Optional;

public interface FeedbackService {
    Feedback createFeedback(Feedback feedback);
    Optional<Feedback> getFeedbackById(Long id);
    List<Feedback> getAllFeedbacks();
    Feedback deleteFeedback(Long id);
    List<Feedback> getFeedbacksByUserId(Long userId);
}
