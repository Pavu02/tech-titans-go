package com.examly.springapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @Column(nullable = false, length = 2000)
    private String feedbackText;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "rentalId", nullable = false)
    private BookRentalRequest bookRentalRequest;

    @Column(nullable = false)
    private Integer rating;

    public Feedback() {
    }

    public Feedback(Long feedbackId, String feedbackText, LocalDate date, User user, BookRentalRequest bookRentalRequest, Integer rating) {
        this.feedbackId = feedbackId;
        this.feedbackText = feedbackText;
        this.date = date;
        this.user = user;
        this.bookRentalRequest = bookRentalRequest;
        this.rating = rating;
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BookRentalRequest getBookRentalRequest() {
        return bookRentalRequest;
    }

    public void setBookRentalRequest(BookRentalRequest bookRentalRequest) {
        this.bookRentalRequest = bookRentalRequest;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
