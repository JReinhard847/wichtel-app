package org.example.backend.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WichtelParticipantDTO {
    WichtelUserDTO participant;
    InvitationStatus invitationStatus;
}