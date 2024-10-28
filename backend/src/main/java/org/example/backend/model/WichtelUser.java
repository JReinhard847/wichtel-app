package org.example.backend.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@Builder
public class WichtelUser {
    @With
    String id;
    @With
    String name;
    @With
    String email;
}
