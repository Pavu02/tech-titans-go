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
import java.util.Optional;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    private final BookRepo bookRepo;
    private final ObjectMapper objectMapper;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(BookRepo bookRepo, ObjectMapper objectMapper, UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.bookRepo = bookRepo;
        this.objectMapper = objectMapper;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Seed hardcoded users for testing
            seedUser("admin@gmail.com", "admin", "0000000000", "Admin");
            seedUser("user1@gmail.com", "user1", "1111111111", "User");
            seedUser("user2@gmail.com", "user2", "2222222222", "User");

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

    private void seedUser(String email, String username, String mobile, String role) {
        Optional<User> existingUser = userRepo.findByEmail(email);
        if (existingUser.isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setMobileNumber(mobile);
            user.setUserRole(role);
            user.setPassword(passwordEncoder.encode("Qwerty@123"));
            userRepo.save(user);
            System.out.println("Seeded test user: " + email);
        }
    }
}
