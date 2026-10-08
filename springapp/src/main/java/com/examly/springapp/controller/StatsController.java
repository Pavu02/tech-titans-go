package com.examly.springapp.controller;

import com.examly.springapp.repository.BookRepo;
import com.examly.springapp.repository.UserRepo;
import com.examly.springapp.repository.BookRentalRequestRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final BookRepo bookRepo;
    private final UserRepo userRepo;
    private final BookRentalRequestRepo bookRentalRequestRepo;

    public StatsController(BookRepo bookRepo, UserRepo userRepo, BookRentalRequestRepo bookRentalRequestRepo) {
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
        this.bookRentalRequestRepo = bookRentalRequestRepo;
    }

    @GetMapping
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalBooks", bookRepo.count());
        stats.put("totalUsers", userRepo.count());
        stats.put("totalRentals", bookRentalRequestRepo.count());
        return ResponseEntity.ok(stats);
    }
}
