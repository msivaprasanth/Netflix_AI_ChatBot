package com.example.netflixai.repository;

import com.example.netflixai.model.ChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatHistoryRepository extends MongoRepository<ChatHistory, String> {
    List<ChatHistory> findByUserId(String userId);
}
