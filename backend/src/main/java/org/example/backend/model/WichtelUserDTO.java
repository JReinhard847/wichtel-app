package org.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class WichtelUserDTO {
    String id;
    String name;
    String oauthName;
    String oauthProvider;
    String email;
}
