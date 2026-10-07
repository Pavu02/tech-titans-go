package com.examly.springapp.service;

import com.examly.springapp.dto.BookRequestDTO;
import com.examly.springapp.exceptions.BookDeletionException;
import com.examly.springapp.exceptions.BookException;
import com.examly.springapp.model.Book;
import com.examly.springapp.repository.BookRentalRequestRepo;
import com.examly.springapp.repository.BookRepo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;
    private final BookRentalRequestRepo rentalRequestRepo;

    // Constructor Injection
    public BookServiceImpl(BookRepo bookRepo, BookRentalRequestRepo rentalRequestRepo) {
        this.bookRepo = bookRepo;
        this.rentalRequestRepo = rentalRequestRepo;
    }

    @Override
    public Book addBook(BookRequestDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.title());
        book.setAuthor(bookDTO.author());
        book.setGenre(bookDTO.genre());
        book.setDescription(bookDTO.description());
        book.setRentalFee(bookDTO.rentalFee());
        book.setIsAvailable(bookDTO.isAvailable() != null ? bookDTO.isAvailable() : true);
        book.setCoverImage(bookDTO.coverImage());
        
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
    public Book updateBook(Long bookId, BookRequestDTO updatedBookDTO) {
        Book existing = bookRepo.findById(bookId)
                .orElseThrow(() -> new BookException("Book not found with id: " + bookId));

        if (updatedBookDTO.title() != null) {
            existing.setTitle(updatedBookDTO.title());
        }
        if (updatedBookDTO.author() != null) {
            existing.setAuthor(updatedBookDTO.author());
        }
        if (updatedBookDTO.genre() != null) {
            existing.setGenre(updatedBookDTO.genre());
        }
        if (updatedBookDTO.description() != null) {
            existing.setDescription(updatedBookDTO.description());
        }
        if (updatedBookDTO.rentalFee() != null) {
            existing.setRentalFee(updatedBookDTO.rentalFee());
        }
        if (updatedBookDTO.isAvailable() != null) {
            existing.setIsAvailable(updatedBookDTO.isAvailable());
        }
        if (updatedBookDTO.coverImage() != null && !updatedBookDTO.coverImage().trim().isEmpty()) {
            existing.setCoverImage(updatedBookDTO.coverImage());
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
