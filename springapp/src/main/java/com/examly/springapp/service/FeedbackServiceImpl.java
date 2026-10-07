package com.examly.springapp.service;

import com.examly.springapp.dto.FeedbackRequestDTO;
import com.examly.springapp.model.Feedback;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.FeedbackRepo;
import com.examly.springapp.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepo feedbackRepo;
    private final UserRepo userRepo;

    // Constructor Injection
    public FeedbackServiceImpl(FeedbackRepo feedbackRepo, UserRepo userRepo) {
        this.feedbackRepo = feedbackRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Feedback createFeedback(FeedbackRequestDTO feedbackDTO) {
        Feedback feedback = new Feedback();
        feedback.setFeedbackText(feedbackDTO.feedbackText());
        feedback.setDate(LocalDate.now());

        if (feedbackDTO.user() != null && feedbackDTO.user().userId() != null) {
            User user = userRepo.findById(feedbackDTO.user().userId()).orElse(null);
            feedback.setUser(user);
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
