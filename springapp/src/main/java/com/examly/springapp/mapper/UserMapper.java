package com.examly.springapp.mapper;

import com.examly.springapp.dto.RegisterRequestDTO;
import com.examly.springapp.dto.UserResponseDTO;
import com.examly.springapp.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toEntity(RegisterRequestDTO dto) {
        User user = new User();
        user.setEmail(dto.email());
        user.setUsername(dto.username());
        user.setMobileNumber(dto.mobileNumber());
        
        if (dto.password() != null && !dto.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
        return user;
    }

    public UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) return null;
        return new UserResponseDTO(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getMobileNumber(),
                user.getUserRole()
        );
    }
}
