package org.example.backend.util;

import org.example.backend.model.*;

import java.util.List;

public class DTOConverter {

    public static WichtelUserDTO toDTO(WichtelUser user) {
        return new WichtelUserDTO(user.getName());
    }

    public static WichtelUser fromDTO(WichtelUserDTO dto, String id, String email) {
        return new WichtelUser(id, dto.getName(), email);
    }

    public static WichtelParticipantDTO toDTO(WichtelParticipant participant) {
        return new WichtelParticipantDTO(toDTO(participant.getParticipant()), participant.getInvitationStatus());
    }

    public static WichtelParticipant fromDTO(WichtelParticipantDTO dto, String id, String email, String address, String wishList) {
        return new WichtelParticipant(fromDTO(dto.getParticipant(), id, email), dto.getInvitationStatus(), wishList, address);
    }

    public static WichtelEventDTO toDTO(WichtelEvent event) {
        List<WichtelParticipantDTO> participantDTOList = event.getParticipants().stream()
                .map(DTOConverter::toDTO)
                .toList();
        return new WichtelEventDTO(toDTO(event.getOrganizer()), event.getTitle(), event.getDescription(), event.getBudget(), event.getImage(), event.getDrawDate(), event.getGiftExchangeDate(), participantDTOList);
    }
}
