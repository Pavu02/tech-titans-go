package com.examly.springapp.service;

import com.examly.springapp.dto.BookRequestDTO;
import com.examly.springapp.exceptions.BookDeletionException;
import com.examly.springapp.exceptions.BookException;
import com.examly.springapp.model.Book;
import com.examly.springapp.repository.BookRentalRequestRepo;
import com.examly.springapp.repository.BookRepo;
import com.examly.springapp.mapper.BookMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;
    private final BookRentalRequestRepo rentalRequestRepo;
    private final BookMapper bookMapper;

    // Constructor Injection
    public BookServiceImpl(BookRepo bookRepo, BookRentalRequestRepo rentalRequestRepo, BookMapper bookMapper) {
        this.bookRepo = bookRepo;
        this.rentalRequestRepo = rentalRequestRepo;
        this.bookMapper = bookMapper;
    }

    @Override
    public Book addBook(BookRequestDTO bookDTO) {
        Book book = bookMapper.toEntity(bookDTO);
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
    public Book updateBook(Long bookId, BookRequestDTO updatedBookDTO) {
        Book existing = bookRepo.findById(bookId)
                .orElseThrow(() -> new BookException("Book not found with id: " + bookId));

        bookMapper.updateEntityFromDto(updatedBookDTO, existing);

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
