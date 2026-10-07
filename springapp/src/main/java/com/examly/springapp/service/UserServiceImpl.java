package com.examly.springapp.service;

import com.examly.springapp.dto.LoginRequestDTO;
import com.examly.springapp.dto.RegisterRequestDTO;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepo;
import com.examly.springapp.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    // Constructor Injection
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public User createUser(RegisterRequestDTO registerDTO) {
        User user = userMapper.toEntity(registerDTO);
        
        if (userRepo.count() == 0) {
            user.setUserRole("Admin");
        } else {
            user.setUserRole("User");
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
