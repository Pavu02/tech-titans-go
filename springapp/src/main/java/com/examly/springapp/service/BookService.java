package com.examly.springapp.service;

import com.examly.springapp.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book addBook(Book book);
    Optional<Book> getBookById(Long bookId);
    List<Book> getAllBooks();
    Book updateBook(Long bookId, Book updatedBook);
    Book deleteBook(Long bookId);
}
