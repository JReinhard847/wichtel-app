package org.example.backend.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@With
public class WichtelParticipant {
    WichtelUser participant;
    InvitationStatus invitationStatus;
    String wishList;
    String address;

}
