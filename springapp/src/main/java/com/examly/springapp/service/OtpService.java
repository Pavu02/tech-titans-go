package com.examly.springapp.service;

import com.examly.springapp.model.OtpToken;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.OtpTokenRepo;
import com.examly.springapp.repository.UserRepo;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    private final OtpTokenRepo otpTokenRepo;
    private final EmailService emailService;
    private final UserRepo userRepo;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${otp.mode:mock}")
    private String otpMode;

    @Value("${twilio.account_sid:}")
    private String twilioAccountSid;

    @Value("${twilio.auth_token:}")
    private String twilioAuthToken;

    @Value("${twilio.verify_service_sid:}")
    private String verifyServiceSid;

    public OtpService(
            OtpTokenRepo otpTokenRepo,
            EmailService emailService,
            UserRepo userRepo) {
        this.otpTokenRepo = otpTokenRepo;
        this.emailService = emailService;
        this.userRepo = userRepo;
    }

    @PostConstruct
    public void initTwilio() {
        if ("verify".equalsIgnoreCase(otpMode)) {
            if (twilioAccountSid.isBlank()
                    || twilioAuthToken.isBlank()
                    || verifyServiceSid.isBlank()) {
                throw new IllegalStateException(
                        "Twilio Verify credentials are missing");
            }

            Twilio.init(twilioAccountSid, twilioAuthToken);
        }
    }

    public void generateAndSendOtp(
            String email, String explicitMobileNumber) {

        String mode = otpMode.toLowerCase().trim();

        switch (mode) {
            case "mock" -> {
                String emailOtp = generateOtp();
                String mobileOtp = generateOtp();

                try {
                    emailService.sendOtpEmail(email, emailOtp);
                } catch (Exception e) {
                    System.err.println("Failed to send Email OTP: " + e.getMessage());
                }
                
                saveOtp(email, emailOtp);
                String phone = resolvePhone(explicitMobileNumber, email);

                if (phone != null) {
                    saveOtp(normalizePhone(phone), mobileOtp);
                }

                // LOCAL DEVELOPMENT ONLY. Never expose OTPs in production.
                System.out.println("[MOCK OTP] Email: " + emailOtp);
                if (phone != null) {
                    System.out.println("[MOCK OTP] Mobile: " + mobileOtp);
                }
            }

            case "email" -> {
                String emailOtp = generateOtp();

                // Do not claim success or store a usable OTP if sending fails.
                emailService.sendOtpEmail(email, emailOtp);
                saveOtp(email, emailOtp);
            }

            case "verify" -> {
                // 1. Handle Email OTP (Java Mail Sender)
                String emailOtp = generateOtp();
                try {
                    emailService.sendOtpEmail(email, emailOtp);
                    saveOtp(email, emailOtp);
                    System.out.println("[VERIFY MODE] Generated Email OTP: " + emailOtp); // Print to terminal so you can see it
                } catch (Exception e) {
                    System.err.println("Failed to send OTP email: " + e.getMessage());
                }

                // 2. Handle Mobile OTP (Twilio Verify API)
                String phone = resolvePhone(
                        explicitMobileNumber, email);

                if (phone == null) {
                    throw new IllegalArgumentException(
                            "A mobile number is required for SMS OTP");
                }

                String formattedPhone = normalizePhone(phone);

                // Twilio generates and manages the mobile verification code.
                Verification.creator(
                        verifyServiceSid,
                        formattedPhone,
                        "sms").create();
            }

            default -> throw new IllegalStateException(
                    "Unsupported otp.mode: " + mode);
        }
    }

    private String generateOtp() {
        return String.format("%06d",
                secureRandom.nextInt(1_000_000));
    }

    private void saveOtp(String key, String otp) {
        otpTokenRepo.deleteByEmail(key);
        otpTokenRepo.save(new OtpToken(
                key, otp, LocalDateTime.now().plusMinutes(5)));
    }

    private String resolvePhone(
            String explicitMobileNumber, String email) {

        if (explicitMobileNumber != null
                && !explicitMobileNumber.isBlank()) {
            return explicitMobileNumber.trim();
        }

        Optional<User> user = userRepo.findByEmail(email);

        if (user.isPresent()
                && user.get().getMobileNumber() != null
                && !user.get().getMobileNumber().isBlank()) {
            return user.get().getMobileNumber().trim();
        }

        return null;
    }

    private String normalizePhone(String phone) {
        String value = phone.replaceAll("[\\s()-]", "");

        if (value.matches("[6-9]\\d{9}")) {
            return "+91" + value;
        }

        if (value.matches("\\+91[6-9]\\d{9}")) {
            return value;
        }

        throw new IllegalArgumentException(
                "Enter a valid Indian mobile number");
    }

    public boolean validateMobileOtp(
            String mobileNumber, String otp) {

        if (mobileNumber == null || otp == null
                || !otp.matches("\\d{6}")) {
            return false;
        }

        if ("verify".equalsIgnoreCase(otpMode)) {
            String phone = normalizePhone(mobileNumber);

            VerificationCheck check = VerificationCheck.creator(verifyServiceSid)
                    .setTo(phone)
                    .setCode(otp)
                    .create();

            return "approved".equalsIgnoreCase(check.getStatus());
        }

        if ("mock".equalsIgnoreCase(otpMode)) {
            return validateStoredOtp(
                    normalizePhone(mobileNumber), otp);
        }

        return false;
    }

    public boolean validateOtp(String email, String otp) {
        if (email == null || otp == null
                || !otp.matches("\\d{6}")) {
            return false;
        }

        if ("mock".equalsIgnoreCase(otpMode)
                || "email".equalsIgnoreCase(otpMode)
                || "verify".equalsIgnoreCase(otpMode)) {
            return validateStoredOtp(email, otp);
        }

        return false;
    }

    private boolean validateStoredOtp(String key, String otp) {
        Optional<OtpToken> result = otpTokenRepo.findByEmailAndOtp(key, otp);

        if (result.isEmpty()) {
            return false;
        }

        OtpToken token = result.get();

        if (token.getExpirationTime() == null
                || !token.getExpirationTime()
                        .isAfter(LocalDateTime.now())) {
            otpTokenRepo.deleteByEmail(key);
            return false;
        }

        otpTokenRepo.deleteByEmail(key);
        return true;
    }
}