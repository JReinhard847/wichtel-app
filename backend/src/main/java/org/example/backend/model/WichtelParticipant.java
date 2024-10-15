package org.example.backend.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
public class WichtelParticipant {
    WichtelUser participant;
    @With
    InvitationStatus invitationStatus;
    @With
    String wishList;
    @With
    String address;
}
