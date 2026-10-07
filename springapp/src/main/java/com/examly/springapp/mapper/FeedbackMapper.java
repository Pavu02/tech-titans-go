package com.examly.springapp.mapper;

import com.examly.springapp.dto.FeedbackRequestDTO;
import com.examly.springapp.model.Feedback;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepo;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class FeedbackMapper {

    private final UserRepo userRepo;

    public FeedbackMapper(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Feedback toEntity(FeedbackRequestDTO dto) {
        Feedback feedback = new Feedback();
        feedback.setFeedbackText(dto.feedbackText());
        feedback.setDate(LocalDate.now());

        if (dto.user() != null && dto.user().userId() != null) {
            User user = userRepo.findById(dto.user().userId()).orElse(null);
            feedback.setUser(user);
        }

        return feedback;
    }
}
