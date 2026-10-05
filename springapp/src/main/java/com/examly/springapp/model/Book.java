package com.examly.springapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String genre;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double rentalFee;

    @Column(nullable = false)
    private Boolean isAvailable = true;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String coverImage;

    public Book() {
    }

    public Book(Long bookId, String title, String author, String genre, String description, Double rentalFee, Boolean isAvailable, String coverImage) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.rentalFee = rentalFee;
        this.isAvailable = isAvailable;
        this.coverImage = coverImage;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(Double rentalFee) {
        this.rentalFee = rentalFee;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}
