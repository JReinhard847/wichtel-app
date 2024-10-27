package org.example.backend.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@Builder
@With
public class WichtelUser {
    String id;
    String name;
    String email;
    String oauthProvider;
    String oauthId;
}
