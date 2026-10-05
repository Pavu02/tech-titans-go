package com.examly.springapp.controller;

import com.examly.springapp.config.JwtUtils;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepo;
import com.examly.springapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (user.getEmail() == null || user.getPassword() == null ||
                user.getUsername() == null || user.getMobileNumber() == null || user.getUserRole() == null) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "All fields are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        if (userRepo.existsByEmail(user.getEmail())) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "User already exists with email: " + user.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }

        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginUser) {
        if (loginUser.getEmail() == null || loginUser.getPassword() == null) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "Email and password are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        Optional<User> optUser = userRepo.findByEmail(loginUser.getEmail());
        if (optUser.isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "Invalid Email or Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        User user = userService.loginUser(loginUser);
        if (user == null) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "Invalid Email or Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getUserRole(), user.getUserId());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getUserId());
        response.put("email", user.getEmail());
        response.put("username", user.getUsername());
        response.put("mobileNumber", user.getMobileNumber());
        response.put("userRole", user.getUserRole());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
