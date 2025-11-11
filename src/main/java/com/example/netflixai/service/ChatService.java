package com.example.netflixai.service;

import com.example.netflixai.model.ChatHistory;
import com.example.netflixai.model.UserLikes;
import com.example.netflixai.repository.ChatHistoryRepository;
import com.example.netflixai.repository.UserLikesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final ChatHistoryRepository chatHistoryRepository;
    private final UserLikesRepository userLikesRepository;

    public String processMessage(String userId, String message) {
        log.info("Received message from user {}: {}", userId, message);

        String lowerMsg = message.toLowerCase();

        // Detect if user liked a movie
        if (lowerMsg.contains("i liked") || lowerMsg.contains("i love")) {
            String movieName = extractMovieName(message);
            if (movieName != null && !movieName.isBlank()) {
                saveUserLike(userId, movieName);
                log.info("Added liked movie: {}", movieName);
                return "Got it! I've added \"" + movieName + "\" to your liked movies list.";
            }
        }

        // Fetch user's liked movies
        List<String> likedMovies = userLikesRepository.findByUserId(userId)
                .map(UserLikes::getLikedMovies)
                .orElse(List.of());

        try {
            log.info("Sending user message to TinyLlama...");

            // System personality is already applied from application.properties
            String aiResponse = chatClient.prompt()
                    .user(String.format("User previously liked: %s%nUser: %s",
                            String.join(", ", likedMovies), message))
                    .call()
                    .content();

            log.info("TinyLlama responded: {}", aiResponse);

            // Save chat history
            ChatHistory history = new ChatHistory();
            history.setUserId(userId);
            history.setUserMessage(message);
            history.setAiResponse(aiResponse);
            chatHistoryRepository.save(history);

            return aiResponse != null ? aiResponse.trim() : "(No response received)";
        } catch (Exception e) {
            log.error("Error connecting to TinyLlama: {}", e.getMessage(), e);
            return "AI service is currently unavailable. Please try again later.";
        }
    }

    // Save liked movies
    private void saveUserLike(String userId, String movieName) {
        UserLikes userLikes = userLikesRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserLikes ul = new UserLikes();
                    ul.setUserId(userId);
                    return ul;
                });

        if (!userLikes.getLikedMovies().contains(movieName)) {
            userLikes.getLikedMovies().add(movieName);
            userLikesRepository.save(userLikes);
        }
    }

    // Extract liked movie name
    private String extractMovieName(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("i liked")) {
            return message.substring(lower.indexOf("i liked") + 8).trim();
        } else if (lower.contains("i love")) {
            return message.substring(lower.indexOf("i love") + 7).trim();
        }
        return null;
    }

    //  Get chat history by email (for frontend)
    public List<ChatHistory> getChatHistoryByEmail(String email) {
        var userLikes = userLikesRepository.findAll()
                .stream()
                .filter(u -> email.equalsIgnoreCase(u.getUserId()))
                .findFirst();

        return userLikes.map(userLikes1 ->
                chatHistoryRepository.findByUserId(userLikes1.getUserId()))
                .orElse(List.of());
    }

    // Get user's liked movies
    public List<String> getUserLikedMovies(String userId) {
        return userLikesRepository.findByUserId(userId)
                .map(UserLikes::getLikedMovies)
                .orElse(List.of());
    }

    // Generate movie recommendations directly
    public String recommendMovies(String userId) {
        List<String> likedMovies = userLikesRepository.findByUserId(userId)
                .map(UserLikes::getLikedMovies)
                .orElse(List.of());

        if (likedMovies.isEmpty()) {
            return "You haven’t liked any movies yet! Try saying 'I liked RRR' to start personalized recommendations.";
        }

        String prompt = String.format("""
            Based on these liked movies: %s
            Recommend 3–5 similar movies with short explanations.
            Keep it conversational and engaging.
            """, String.join(", ", likedMovies));

        try {
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            return response != null ? response.trim() : "(No recommendations generated)";
        } catch (Exception e) {
            log.error("Error during movie recommendation: {}", e.getMessage(), e);
            return "AI service unavailable. Please try again later.";
        }
    }

    //  Clear chat history for a specific user
    public void clearChatHistory(String userId) {
        chatHistoryRepository.deleteAll(chatHistoryRepository.findByUserId(userId));
        log.info("Cleared chat history for user: {}", userId);
    }
}
