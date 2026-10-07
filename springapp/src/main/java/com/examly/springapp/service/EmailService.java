package com.examly.springapp.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("techtitansgo2026@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Your OTP Code for BookHeaven");
        message.setText("Your OTP code is: " + otp + "\n\nThis code will expire in 5 minutes.");

        mailSender.send(message);
    }
}
