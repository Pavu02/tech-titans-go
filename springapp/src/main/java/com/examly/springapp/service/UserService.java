package com.examly.springapp.service;

import com.examly.springapp.dto.LoginRequestDTO;
import com.examly.springapp.dto.RegisterRequestDTO;
import com.examly.springapp.model.User;

public interface UserService {
    User createUser(RegisterRequestDTO registerDTO);
    User loginUser(LoginRequestDTO loginDTO);
}
