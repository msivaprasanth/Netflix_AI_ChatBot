package com.example.netflixai.controller;

import com.example.netflixai.model.User;
import com.example.netflixai.repository.UserRepository;
import com.example.netflixai.service.AuthService;
import com.example.netflixai.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final MailService mailService; // Added missing MailService injection
    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        try {
            String token = authService.login(creds.get("email"), creds.get("password"));
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Forgot Password — sends reset email
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not registered"));
        }

        String resetLink = "http://localhost:3000/reset-password?email=" + email;

        // Send reset link via Gmail
        mailService.sendPasswordResetMail(email, resetLink);

        return ResponseEntity.ok(Map.of("message", "Password reset link sent to your email."));
    }

    // Reset Password — updates password in MongoDB
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found."));
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword)); // Encrypt before saving
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully!"));
    }
}
