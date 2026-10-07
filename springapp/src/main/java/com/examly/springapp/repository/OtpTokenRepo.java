package com.examly.springapp.repository;

import com.examly.springapp.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OtpTokenRepo extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByEmailAndOtp(String email, String otp);
    
    @Transactional
    void deleteByEmail(String email);
}
