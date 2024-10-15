package org.example.backend.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
public class WichtelUser {
    String id;
    @With
    String name;
    @With
    String email;
}
