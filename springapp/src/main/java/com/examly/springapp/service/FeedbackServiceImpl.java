package com.examly.springapp.service;

import com.examly.springapp.dto.FeedbackRequestDTO;
import com.examly.springapp.model.Feedback;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.FeedbackRepo;
import com.examly.springapp.repository.UserRepo;
import com.examly.springapp.repository.BookRentalRequestRepo;
import com.examly.springapp.model.BookRentalRequest;
import com.examly.springapp.mapper.FeedbackMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepo feedbackRepo;
    private final UserRepo userRepo;
    private final BookRentalRequestRepo bookRentalRequestRepo;
    private final FeedbackMapper feedbackMapper;

    // Constructor Injection
    public FeedbackServiceImpl(FeedbackRepo feedbackRepo, UserRepo userRepo, BookRentalRequestRepo bookRentalRequestRepo, FeedbackMapper feedbackMapper) {
        this.feedbackRepo = feedbackRepo;
        this.userRepo = userRepo;
        this.bookRentalRequestRepo = bookRentalRequestRepo;
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    public Feedback createFeedback(FeedbackRequestDTO feedbackDTO) {
        if (feedbackDTO.rentalId() == null) {
            throw new RuntimeException("Rental ID is required to submit feedback");
        }
        
        if (feedbackRepo.existsByBookRentalRequestRentalId(feedbackDTO.rentalId())) {
            throw new RuntimeException("Feedback already exists for this rental");
        }
        
        BookRentalRequest req = bookRentalRequestRepo.findById(feedbackDTO.rentalId())
            .orElseThrow(() -> new RuntimeException("Rental request not found"));
            
        if (!"Returned".equalsIgnoreCase(req.getStatus())) {
            throw new RuntimeException("Cannot submit feedback for a book that is not returned yet");
        }

        Feedback feedback = feedbackMapper.toEntity(feedbackDTO);
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
