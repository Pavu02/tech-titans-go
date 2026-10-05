package com.examly.springapp.service;

import com.examly.springapp.model.Feedback;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.FeedbackRepo;
import com.examly.springapp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepo feedbackRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public Feedback createFeedback(Feedback feedback) {
        if (feedback.getUser() != null && feedback.getUser().getUserId() != null) {
            User user = userRepo.findById(feedback.getUser().getUserId()).orElse(feedback.getUser());
            feedback.setUser(user);
        }
        if (feedback.getDate() == null) {
            feedback.setDate(LocalDate.now());
        }
        return feedbackRepo.save(feedback);
    }

    @Override
    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepo.findById(id);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepo.findAll();
    }

    @Override
    public Feedback deleteFeedback(Long id) {
        Optional<Feedback> fb = feedbackRepo.findById(id);
        if (fb.isPresent()) {
            feedbackRepo.delete(fb.get());
            return fb.get();
        }
        return null;
    }

    @Override
    public List<Feedback> getFeedbacksByUserId(Long userId) {
        return feedbackRepo.findByUserUserId(userId);
    }
}
