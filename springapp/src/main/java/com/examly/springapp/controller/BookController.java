package com.examly.springapp.controller;

import com.examly.springapp.dto.BookRequestDTO;
import com.examly.springapp.exceptions.BookDeletionException;
import com.examly.springapp.exceptions.BookException;
import com.examly.springapp.model.Book;
import com.examly.springapp.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // Constructor Injection
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<?> addBook(@Valid @RequestBody BookRequestDTO bookDTO) {
        Book savedBook = bookService.addBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        // Returns 201
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookById(@PathVariable Long bookId) {
        Optional<Book> book = bookService.getBookById(bookId);
        if (book.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(book.get());
            // Returns 200
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
            // Returns 404
        }
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.status(HttpStatus.OK).body(books);
        // Returns 200
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<?> updateBook(@PathVariable Long bookId, @Valid @RequestBody BookRequestDTO updatedBookDTO) {
        try {
            Book book = bookService.updateBook(bookId, updatedBookDTO);
            return ResponseEntity.status(HttpStatus.OK).body(book);
            // Returns 200
        } catch (BookException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            // Returns 404
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            // Returns 400
        }
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable Long bookId) {
        try {
            Book deleted = bookService.deleteBook(bookId);
            return ResponseEntity.status(HttpStatus.OK).body(deleted);
            // Returns 200
        } catch (BookException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            // Returns 404
        } catch (BookDeletionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            // Returns 400
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            // Returns 500
        }
    }

    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<com.examly.springapp.dto.BookRecommendationDTO>> getAiRecommendations(@PathVariable Long userId) {
        List<com.examly.springapp.dto.BookRecommendationDTO> recommendations = bookService.getAiRecommendations(userId);
        return ResponseEntity.status(HttpStatus.OK).body(recommendations);
    }
}
