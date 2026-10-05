package com.examly.springapp.controller;

import com.examly.springapp.exceptions.BookDeletionException;
import com.examly.springapp.exceptions.BookException;
import com.examly.springapp.model.Book;
import com.examly.springapp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty() ||
            book.getAuthor() == null || book.getAuthor().trim().isEmpty() ||
            book.getGenre() == null || book.getGenre().trim().isEmpty() ||
            book.getDescription() == null || book.getDescription().trim().isEmpty() ||
            book.getRentalFee() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields are required");
        }

        Book savedBook = bookService.addBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookById(@PathVariable Long bookId) {
        Optional<Book> book = bookService.getBookById(bookId);
        if (book.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(book.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<?> updateBook(@PathVariable Long bookId, @RequestBody Book updatedBook) {
        try {
            Book book = bookService.updateBook(bookId, updatedBook);
            return ResponseEntity.status(HttpStatus.OK).body(book);
        } catch (BookException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable Long bookId) {
        try {
            Book deleted = bookService.deleteBook(bookId);
            return ResponseEntity.status(HttpStatus.OK).body(deleted);
        } catch (BookException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BookDeletionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
