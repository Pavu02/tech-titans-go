package com.examly.springapp.config;

import com.examly.springapp.model.Book;
import com.examly.springapp.repository.BookRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    private final BookRepo bookRepo;
    private final ObjectMapper objectMapper;

    public DatabaseSeeder(BookRepo bookRepo, ObjectMapper objectMapper) {
        this.bookRepo = bookRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Check if books table is empty to avoid duplicate seeding
            if (bookRepo.count() == 0) {
                System.out.println("No books found in the database. Seeding from booksDB.json...");
                
                // Read the JSON file from resources/data/booksDB.json
                ClassPathResource resource = new ClassPathResource("data/booksDB.json");
                InputStream inputStream = resource.getInputStream();
                
                List<Book> books = objectMapper.readValue(inputStream, new TypeReference<List<Book>>(){});
                
                if (books != null && !books.isEmpty()) {
                    bookRepo.saveAll(books);
                    System.out.println("Successfully seeded " + books.size() + " books into the database!");
                }
            } else {
                System.out.println("Books already exist in the database. Skipping seed.");
            }
        } catch (Exception e) {
            // Fallback: Catch any errors (missing file, DB error) so the application continues to run normally
            System.err.println("Warning: Failed to seed the database. " + e.getMessage());
            System.err.println("Application will continue to run without seed data.");
        }
    }
}
