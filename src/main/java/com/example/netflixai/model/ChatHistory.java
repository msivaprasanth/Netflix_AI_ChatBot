package com.example.netflixai.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "chat_history")
public class ChatHistory {
    @Id
    private String id;
    private String userId;
    private String userMessage;
    private String aiResponse;
    private LocalDateTime timestamp = LocalDateTime.now();
}
