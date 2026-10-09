package com.examly.springapp.service;

import com.examly.springapp.dto.ChatResponseDTO;
import com.examly.springapp.model.Book;
import com.examly.springapp.model.ChatMessage;
import com.examly.springapp.model.FaqEntity;
import com.examly.springapp.repository.BookRepo;
import com.examly.springapp.repository.ChatMessageRepo;
import com.examly.springapp.repository.FaqRepo;
import com.examly.springapp.repository.FeedbackRepo;
import com.examly.springapp.repository.BookRentalRequestRepo;
import com.examly.springapp.model.Feedback;
import com.examly.springapp.model.BookRentalRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final FaqRepo faqRepo;
    private final BookRepo bookRepo;
    private final ChatMessageRepo chatMessageRepo;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final FeedbackRepo feedbackRepo;
    private final BookRentalRequestRepo rentalRequestRepo;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final Map<String, List<Map<String, String>>> sessionMemory = new ConcurrentHashMap<>();

    // Constructor Injection
    public ChatService(FaqRepo faqRepo, BookRepo bookRepo, ChatMessageRepo chatMessageRepo,
            ResourceLoader resourceLoader, FeedbackRepo feedbackRepo, BookRentalRequestRepo rentalRequestRepo) {
        this.faqRepo = faqRepo;
        this.bookRepo = bookRepo;
        this.chatMessageRepo = chatMessageRepo;
        this.resourceLoader = resourceLoader;
        this.feedbackRepo = feedbackRepo;
        this.rentalRequestRepo = rentalRequestRepo;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void initFaqs() {
        try {
            if (faqRepo.count() == 0) {
                Resource resource = resourceLoader.getResource("classpath:faqs.json");
                if (resource.exists()) {
                    try (InputStream is = resource.getInputStream()) {
                        List<Map<String, Object>> list = objectMapper.readValue(is, new TypeReference<>() {
                        });
                        for (Map<String, Object> map : list) {
                            Long id = ((Number) map.get("id")).longValue();
                            String category = (String) map.get("category");
                            String question = (String) map.get("question");
                            String answer = (String) map.get("answer");
                            faqRepo.save(new FaqEntity(id, category, question, answer));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Could not seed faqs: " + e.getMessage());
        }
    }

    public List<FaqEntity> getAllFaqs() {
        return faqRepo.findAll();
    }

    public List<ChatMessage> getChatHistory(String sessionId) {
        return chatMessageRepo.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    public void clearMemory(String sessionId) {
        sessionMemory.remove(sessionId);
    }

    private String getGeminiKey() {
        if (geminiApiKey != null && !geminiApiKey.trim().isEmpty())
            return geminiApiKey;
        String envKey = System.getenv("GEMINI_API_KEY");
        if (envKey != null && !envKey.trim().isEmpty())
            return envKey;
        try {
            Path envPath = Paths.get(".env");
            if (Files.exists(envPath)) {
                List<String> lines = Files.readAllLines(envPath);
                for (String line : lines) {
                    if (line.startsWith("GEMINI_API_KEY=")) {
                        return line.substring("GEMINI_API_KEY=".length()).trim();
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public ChatResponseDTO processChat(String message, String sessionId, Long userId) {
        String trimmed = (message != null) ? message.trim() : "";
        String sid = (sessionId != null && !sessionId.isEmpty()) ? sessionId : "default-session";
        String reply = "I'm having trouble connecting to my AI brain right now. Please try again later!";
        boolean matched = true;

        String apiKey = getGeminiKey();

        if (apiKey == null) {
            reply = "I cannot answer because the GEMINI_API_KEY is not configured in the backend.";
            matched = false;
        } else {
            try {
                // 1. Gather Context
                List<FaqEntity> faqs = faqRepo.findAll();
                String faqContext = faqs.stream()
                        .map(f -> "Q: " + f.getQuestion() + " A: " + f.getAnswer())
                        .collect(Collectors.joining("\n"));

                List<Book> books = bookRepo.findAll();
                String bookContext = books.stream()
                        .map(b -> b.getTitle() + " by " + b.getAuthor() + " (Genre: " + b.getGenre() + ") - "
                                + b.getDescription())
                        .collect(Collectors.joining("\n"));
                        
                String userContext = "No user logged in.";
                if (userId != null) {
                    List<BookRentalRequest> rentals = rentalRequestRepo.findByUserUserId(userId);
                    String rentalContext = rentals.stream()
                            .map(r -> "Rented: '" + r.getBook().getTitle() + "' Status: " + r.getStatus())
                            .collect(Collectors.joining(", "));
                            
                    List<Feedback> feedbacks = feedbackRepo.findByUserUserId(userId);
                    String feedbackContext = feedbacks.stream()
                            .map(f -> "Rated '" + f.getBookRentalRequest().getBook().getTitle() + "' " + f.getRating() + "/5 stars: " + f.getFeedbackText())
                            .collect(Collectors.joining("; "));
                            
                    userContext = "The current user has the following rental history: [" + rentalContext + "] and has left the following feedback on past books: [" + feedbackContext + "]. "
                                + "Use this user history to provide highly personalized book recommendations when asked.";
                }

                String systemPrompt = "You are a helpful, professional AI assistant for 'BookHeaven', a library management application. "
                        + "Your ONLY purpose is to assist users with questions related to books, book rentals, authors, and our library services. "
                        + "DO NOT answer general knowledge questions outside of the book domain. If the user asks something unrelated, gently steer them back to books.\n\n"
                        + "Here are our official FAQs to help you answer platform questions:\n" + faqContext + "\n\n"
                        + "Here is the list of books currently in our database:\n" + bookContext + "\n\n"
                        + "User Context:\n" + userContext + "\n\n"
                        + "Provide concise, friendly answers and recommendations based strictly on the books we actually have.";

                // 2. Build Gemini API Payload
                Map<String, Object> payload = new HashMap<>();
                payload.put("system_instruction", Map.of("parts", Map.of("text", systemPrompt)));
                payload.put("contents", List.of(Map.of("parts", List.of(Map.of("text", trimmed)))));

                String jsonBody = objectMapper.writeValueAsString(payload);

                // 3. Make HTTP POST
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(
                                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key="
                                        + apiKey))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonNode rootNode = objectMapper.readTree(response.body());
                    JsonNode textNode = rootNode.path("candidates").get(0).path("content").path("parts").get(0)
                            .path("text");
                    if (!textNode.isMissingNode()) {
                        reply = textNode.asText().trim();
                    }
                } else {
                    reply = "Gemini API Error: " + response.statusCode() + " - " + response.body();
                    matched = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                reply = "An internal error occurred while communicating with the AI. " + e.getMessage();
                matched = false;
            }
        }

        ChatMessage cm = new ChatMessage(
                sid,
                trimmed,
                reply,
                null,
                1.0,
                LocalDateTime.now());
        chatMessageRepo.save(cm);

        return new ChatResponseDTO(
                reply,
                matched,
                null,
                null,
                1.0,
                "gemini-ai",
                sid,
                trimmed);
    }
}
