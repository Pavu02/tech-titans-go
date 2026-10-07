package com.examly.springapp.service;

import com.examly.springapp.dto.LoginRequestDTO;
import com.examly.springapp.dto.RegisterRequestDTO;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(RegisterRequestDTO registerDTO) {
        User user = new User();
        user.setEmail(registerDTO.email());
        user.setUsername(registerDTO.username());
        user.setMobileNumber(registerDTO.mobileNumber());
        user.setUserRole(registerDTO.userRole());

        if (registerDTO.password() != null && !registerDTO.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(registerDTO.password()));
        }
        return userRepo.save(user);
    }

    @Override
    public User loginUser(LoginRequestDTO loginDTO) {
        User existing = userRepo.findByEmail(loginDTO.email())
                .orElse(null);
        if (existing != null && passwordEncoder.matches(loginDTO.password(), existing.getPassword())) {
            return existing;
        }
        return null;
    }
}
