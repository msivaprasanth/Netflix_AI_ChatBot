package com.example.netflixai.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "user_likes")
public class UserLikes {
    @Id
    private String id;
    private String userId;
    private List<String> likedMovies = new ArrayList<>();
}
