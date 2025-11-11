package com.example.netflixai.repository;

import com.example.netflixai.model.UserLikes;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserLikesRepository extends MongoRepository<UserLikes, String> {
    Optional<UserLikes> findByUserId(String userId);
}
