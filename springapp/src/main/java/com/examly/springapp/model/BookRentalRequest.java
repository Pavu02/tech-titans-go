package com.examly.springapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "book_rental_requests")
public class BookRentalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bookId")
    private Book book;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate requestDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;

    private String status; // Pending, Approved, Returned, Rejected

    private String comments;

    private Double totalRentalAmount;

    public BookRentalRequest() {
    }

    public BookRentalRequest(Long rentalId, User user, Book book, LocalDate requestDate, LocalDate returnDate, String status, String comments) {
        this.rentalId = rentalId;
        this.user = user;
        this.book = book;
        this.requestDate = requestDate;
        this.returnDate = returnDate;
        this.status = status;
        this.comments = comments;
        this.totalRentalAmount = null;
    }

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Double getTotalRentalAmount() {
        return totalRentalAmount;
    }

    public void setTotalRentalAmount(Double totalRentalAmount) {
        this.totalRentalAmount = totalRentalAmount;
    }
}
