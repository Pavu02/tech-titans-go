package com.examly.springapp.controller;

import com.examly.springapp.model.BookRentalRequest;
import com.examly.springapp.repository.BookRentalRequestRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    
    private final BookRentalRequestRepo rentalRequestRepo;
    private final UserRepo userRepo;

    @Value("${payment.mode:mock}")
    private String paymentMode;

    @Value("${razorpay.key.id:}")
    private String keyId;

    @Value("${razorpay.key.secret:}")
    private String keySecret;

    public PaymentController(BookRentalRequestRepo rentalRequestRepo, UserRepo userRepo) {
        this.rentalRequestRepo = rentalRequestRepo;
        this.userRepo = userRepo;
    }

    @PostMapping("/create-order/{rentalId}")
    public ResponseEntity<?> createOrder(@PathVariable Long rentalId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> loggedInUser = userRepo.findByEmail(email);

        if (loggedInUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not found"));
        }

        Optional<BookRentalRequest> rentalOpt = rentalRequestRepo.findById(rentalId);
        if (rentalOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Rental request not found"));
        }

        BookRentalRequest rental = rentalOpt.get();

        if (!rental.getUser().getUserId().equals(loggedInUser.get().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Not your rental request"));
        }

        if (!"Approved".equalsIgnoreCase(rental.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Rental must be approved before payment"));
        }

        if ("PAID".equalsIgnoreCase(rental.getPaymentStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Already paid"));
        }

        if ("mock".equalsIgnoreCase(paymentMode)) {
            rental.setPaymentStatus("PAID");
            rentalRequestRepo.save(rental);
            return ResponseEntity.ok(Map.of("message", "Payment Successful!", "status", "PAID"));
        } else {
            try {
                RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

                JSONObject orderRequest = new JSONObject();
                double amount = rental.getTotalRentalAmount() != null ? rental.getTotalRentalAmount() : 0.0;
                orderRequest.put("amount", (int)(amount * 100)); // amount in paise
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "txn_" + rentalId);

                Order order = razorpay.orders.create(orderRequest);
                rental.setRazorpayOrderId(order.get("id"));
                rentalRequestRepo.save(rental);

                Map<String, Object> response = new HashMap<>();
                response.put("orderId", order.get("id"));
                response.put("amount", order.get("amount"));
                response.put("currency", order.get("currency"));
                response.put("keyId", keyId);
                return ResponseEntity.ok(response);
            } catch (RazorpayException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
            }
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        String razorpayPaymentId = data.get("razorpay_payment_id");
        String razorpayOrderId = data.get("razorpay_order_id");
        String razorpaySignature = data.get("razorpay_signature");

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_signature", razorpaySignature);

            boolean status = Utils.verifyPaymentSignature(options, keySecret);
            if (status) {
                Optional<BookRentalRequest> rentalOpt = rentalRequestRepo.findByRazorpayOrderId(razorpayOrderId);
                if (rentalOpt.isPresent()) {
                    BookRentalRequest rental = rentalOpt.get();
                    rental.setRazorpayPaymentId(razorpayPaymentId);
                    rental.setPaymentStatus("PAID");
                    rentalRequestRepo.save(rental);
                    return ResponseEntity.ok(Map.of("message", "Payment verified", "status", "PAID"));
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Order not found in DB"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid signature"));
            }
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/mock/{rentalId}")
    public ResponseEntity<?> processMockPayment(@PathVariable Long rentalId) {
        // Kept for backwards compatibility with the mock flow temporarily
        return createOrder(rentalId);
    }
}
