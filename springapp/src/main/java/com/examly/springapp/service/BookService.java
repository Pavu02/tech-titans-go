package com.examly.springapp.service;

import com.examly.springapp.dto.BookRequestDTO;
import com.examly.springapp.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book addBook(BookRequestDTO bookDTO);
    Optional<Book> getBookById(Long bookId);
    List<Book> getAllBooks();
    Book updateBook(Long bookId, BookRequestDTO updatedBookDTO);
    Book deleteBook(Long bookId);
    List<com.examly.springapp.dto.BookRecommendationDTO> getAiRecommendations(Long userId);
}
