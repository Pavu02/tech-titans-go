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

    @Override
    public List<com.examly.springapp.dto.BookRecommendationDTO> getAiRecommendations(Long userId) {
        // Find user data
        List<com.examly.springapp.model.BookRentalRequest> rentals = rentalRequestRepo.findByUserUserId(userId);
        // We need FeedbackRepo, but since it's not injected, let's just use rental history to avoid massive refactoring here.
        String rentalContext = rentals.stream()
                .map(r -> "Rented: '" + r.getBook().getTitle() + "'")
                .collect(java.util.stream.Collectors.joining(", "));

        List<Book> books = bookRepo.findAll();
        String bookContext = books.stream()
                .map(b -> "ID: " + b.getBookId() + ", Title: '" + b.getTitle() + "', Genre: " + b.getGenre())
                .collect(java.util.stream.Collectors.joining("\n"));

        String prompt = "You are a book recommendation engine. Based on the user's rental history: [" + rentalContext + "], "
                + "recommend exactly 3 books from the following catalog:\n" + bookContext + "\n\n"
                + "Return ONLY a raw JSON array of objects, with no markdown formatting or backticks. Format:\n"
                + "[{\"bookId\": 1, \"reason\": \"Because you liked...\"}]";

        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null) {
            try {
                java.nio.file.Path envPath = java.nio.file.Paths.get(".env");
                if (java.nio.file.Files.exists(envPath)) {
                    List<String> lines = java.nio.file.Files.readAllLines(envPath);
                    for (String line : lines) {
                        if (line.startsWith("GEMINI_API_KEY=")) {
                            apiKey = line.substring("GEMINI_API_KEY=".length()).trim();
                            break;
                        }
                    }
                }
            } catch (Exception e) {}
        }

        List<com.examly.springapp.dto.BookRecommendationDTO> result = new java.util.ArrayList<>();
        if (apiKey == null) return result;

        try {
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("contents", java.util.List.of(java.util.Map.of("parts", java.util.List.of(java.util.Map.of("text", prompt)))));

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String jsonBody = mapper.writeValueAsString(payload);

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(response.body());
                com.fasterxml.jackson.databind.JsonNode textNode = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text");
                if (!textNode.isMissingNode()) {
                    String aiText = textNode.asText().trim();
                    if (aiText.startsWith("```json")) aiText = aiText.substring(7);
                    if (aiText.startsWith("```")) aiText = aiText.substring(3);
                    if (aiText.endsWith("```")) aiText = aiText.substring(0, aiText.length() - 3);
                    aiText = aiText.trim();

                    com.fasterxml.jackson.databind.JsonNode arrayNode = mapper.readTree(aiText);
                    if (arrayNode.isArray()) {
                        for (com.fasterxml.jackson.databind.JsonNode node : arrayNode) {
                            Long bId = node.path("bookId").asLong();
                            String reason = node.path("reason").asText();
                            bookRepo.findById(bId).ifPresent(b -> result.add(new com.examly.springapp.dto.BookRecommendationDTO(b, reason)));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
