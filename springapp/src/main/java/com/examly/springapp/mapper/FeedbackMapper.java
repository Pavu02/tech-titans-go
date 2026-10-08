package com.examly.springapp.mapper;

import com.examly.springapp.dto.FeedbackRequestDTO;
import com.examly.springapp.model.Feedback;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepo;
import com.examly.springapp.repository.BookRentalRequestRepo;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class FeedbackMapper {

    private final UserRepo userRepo;
    private final BookRentalRequestRepo bookRentalRequestRepo;

    public FeedbackMapper(UserRepo userRepo, BookRentalRequestRepo bookRentalRequestRepo) {
        this.userRepo = userRepo;
        this.bookRentalRequestRepo = bookRentalRequestRepo;
    }

    public Feedback toEntity(FeedbackRequestDTO dto) {
        Feedback feedback = new Feedback();
        feedback.setFeedbackText(dto.feedbackText());
        feedback.setDate(LocalDate.now());

        if (dto.user() != null && dto.user().userId() != null) {
            User user = userRepo.findById(dto.user().userId()).orElse(null);
            feedback.setUser(user);
        }

        if (dto.rentalId() != null) {
            feedback.setBookRentalRequest(bookRentalRequestRepo.findById(dto.rentalId()).orElse(null));
        }
        feedback.setRating(dto.rating());

        return feedback;
    }
}
