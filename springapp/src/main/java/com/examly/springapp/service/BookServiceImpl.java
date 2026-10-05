package com.examly.springapp.service;

import com.examly.springapp.exceptions.BookDeletionException;
import com.examly.springapp.exceptions.BookException;
import com.examly.springapp.model.Book;
import com.examly.springapp.repository.BookRentalRequestRepo;
import com.examly.springapp.repository.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private BookRentalRequestRepo rentalRequestRepo;

    @Override
    public Book addBook(Book book) {
        if (book.getIsAvailable() == null) {
            book.setIsAvailable(true);
        }
        return bookRepo.save(book);
    }

    @Override
    public Optional<Book> getBookById(Long bookId) {
        return bookRepo.findById(bookId);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    @Override
    public Book updateBook(Long bookId, Book updatedBook) {
        Book existing = bookRepo.findById(bookId)
                .orElseThrow(() -> new BookException("Book not found with id: " + bookId));

        if (updatedBook.getTitle() != null) {
            existing.setTitle(updatedBook.getTitle());
        }
        if (updatedBook.getAuthor() != null) {
            existing.setAuthor(updatedBook.getAuthor());
        }
        if (updatedBook.getGenre() != null) {
            existing.setGenre(updatedBook.getGenre());
        }
        if (updatedBook.getDescription() != null) {
            existing.setDescription(updatedBook.getDescription());
        }
        if (updatedBook.getRentalFee() != null) {
            existing.setRentalFee(updatedBook.getRentalFee());
        }
        if (updatedBook.getIsAvailable() != null) {
            existing.setIsAvailable(updatedBook.getIsAvailable());
        }
        if (updatedBook.getCoverImage() != null && !updatedBook.getCoverImage().trim().isEmpty()) {
            existing.setCoverImage(updatedBook.getCoverImage());
        }

        return bookRepo.save(existing);
    }

    @Override
    public Book deleteBook(Long bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new BookException("Book not found with id: " + bookId));

        boolean hasActiveRentals = rentalRequestRepo.existsByBookBookIdAndStatusIn(
                bookId, Arrays.asList("Pending", "Approved"));
        if (hasActiveRentals) {
            throw new BookDeletionException("Cannot delete book with active rental requests");
        }

        bookRepo.delete(book);
        return book;
    }
}
