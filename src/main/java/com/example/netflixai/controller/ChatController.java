package com.example.netflixai.controller;

import com.example.netflixai.model.ChatHistory;
import com.example.netflixai.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/message")
    public Map<String, String> processMessage(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String message = request.get("message");

        String response = chatService.processMessage(userId, message);
        return Map.of("response", response);
    }
    // ChatController.java
    @GetMapping("/history")
    public List<ChatHistory> getChatHistory(@RequestParam String email) {
        return chatService.getChatHistoryByEmail(email);
    }
        // ChatController.java
    @GetMapping("/likes/{userId}")
    public List<String> getUserLikedMovies(@PathVariable String userId) {
        return chatService.getUserLikedMovies(userId);
    }
    // ChatController.java
    @GetMapping("/recommend/{userId}")
    public Map<String, String> recommendMovies(@PathVariable String userId) {
        String recommendations = chatService.recommendMovies(userId);
        return Map.of("recommendations", recommendations);
    }
    @DeleteMapping("/clear/{userId}")
    public String clearChatHistory(@PathVariable String userId) {
        chatService.clearChatHistory(userId);
        return "Chat history cleared successfully.";
    }

    

}
