package org.example.backend.util;

import org.example.backend.model.*;

import java.util.List;
import java.util.Map;


public class DTOConverter {

    private DTOConverter(){
        throw new UnsupportedOperationException();
    }

    public static WichtelUserDTO toDTO(WichtelUser user) {
        return WichtelUserDTO.builder().oauthName(user.getOauthName()).name(user.getName()).oauthProvider(user.getOauthProvider()).email(user.getEmail()).id(user.getId()).build();
    }

    public static WichtelUser fromDTO(WichtelUserDTO dto, String id) {
        return WichtelUser.builder().id(id).name(dto.getName()).email(dto.getEmail()).build();
    }

    public static WichtelParticipantDTO toDTO(WichtelParticipant participant) {
        return new WichtelParticipantDTO(toDTO(participant.getParticipant()), participant.getInvitationStatus());
    }

    public static WichtelParticipant fromDTO(WichtelParticipantDTO dto, String id, String address, String wishList) {
        return new WichtelParticipant(fromDTO(dto.getParticipant(), id), dto.getInvitationStatus(), address, wishList);
    }

    public static WichtelEventDTO toDTO(WichtelEvent event) {
        List<WichtelParticipantDTO> participantDTOList = event.getParticipants().stream()
                .map(DTOConverter::toDTO)
                .toList();
        return new WichtelEventDTO(event.getId(),toDTO(event.getOrganizer()), event.getTitle(), event.getDescription(), event.getBudget(), event.getImage(), event.getDrawDate(), event.getGiftExchangeDate(), participantDTOList,event.getPairings()==null||!event.getPairings().isEmpty());
    }

    public static WichtelEvent fromDTO(WichtelEventDTO dto, String id, WichtelUser organizer, List<WichtelParticipant> participants, Map<String,WichtelParticipant> pairing){
        return new WichtelEvent(id,organizer,dto.getTitle(),dto.getDescription(),dto.getBudget(),dto.getImage(),dto.getDrawDate(),dto.getGiftExchangeDate(),participants,pairing);
    }
}
