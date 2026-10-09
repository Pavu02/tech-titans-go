package com.examly.springapp.dto;

import com.examly.springapp.model.Book;

public class BookRecommendationDTO {
    private Book book;
    private String reason;

    public BookRecommendationDTO() {
    }

    public BookRecommendationDTO(Book book, String reason) {
        this.book = book;
        this.reason = reason;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
