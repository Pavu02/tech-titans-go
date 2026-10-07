package com.examly.springapp.controller;

import com.examly.springapp.config.JwtUtils;
import com.examly.springapp.dto.AuthResponse;
import com.examly.springapp.dto.LoginRequestDTO;
import com.examly.springapp.dto.RegisterRequestDTO;
import com.examly.springapp.dto.UserResponseDTO;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepo;
import com.examly.springapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;
    private final UserRepo userRepo;
    private final JwtUtils jwtUtils;
    private final com.examly.springapp.service.OtpService otpService;

    // Constructor Injection
    public AuthController(UserService userService, UserRepo userRepo, JwtUtils jwtUtils, com.examly.springapp.service.OtpService otpService) {
        this.userService = userService;
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
        this.otpService = otpService;
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@Valid @RequestBody com.examly.springapp.dto.OtpRequestDTO requestDTO) {
        otpService.generateAndSendOtp(requestDTO.email());
        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent successfully to " + requestDTO.email());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO registerDTO) {
        if (userRepo.existsByEmail(registerDTO.email())) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "User already exists with email: " + registerDTO.email());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
            // Returns 409
        }

        if (userRepo.existsByMobileNumber(registerDTO.mobileNumber())) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "User already exists with mobile number: " + registerDTO.mobileNumber());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
            // Returns 409
        }

        if (registerDTO.otp() != null && !registerDTO.otp().isEmpty()) {
            if (!otpService.validateOtp(registerDTO.email(), registerDTO.otp())) {
                Map<String, String> err = new HashMap<>();
                err.put("message", "Invalid or expired OTP");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
            }
        }

        User created = userService.createUser(registerDTO);
        UserResponseDTO response = new UserResponseDTO(
                created.getUserId(),
                created.getEmail(),
                created.getUsername(),
                created.getMobileNumber(),
                created.getUserRole()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        // Returns 201
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO loginDTO) {
        Optional<User> optUser = userRepo.findByEmail(loginDTO.email());
        if (optUser.isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "Invalid Email or Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
            // Returns 401
        }

        User user = userService.loginUser(loginDTO);
        if (user == null) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "Invalid Email or Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
            // Returns 401
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getUserRole(), user.getUserId());

        AuthResponse response = new AuthResponse(
                token,
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getMobileNumber(),
                user.getUserRole()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
        // Returns 200
    }
}
