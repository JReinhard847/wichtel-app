package org.example.backend.util;

import org.example.backend.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.example.backend.util.DTOConverter.fromDTO;
import static org.example.backend.util.DTOConverter.toDTO;
import static org.junit.jupiter.api.Assertions.*;

class DTOConverterTest {

    @Test
    void wichtelUserConverter() {
        WichtelUser expected = new WichtelUser("1","name","email");
        WichtelUserDTO dto = toDTO(expected);
        WichtelUser actual = fromDTO(dto,"1");
        assertEquals(expected,actual);
    }

    @Test
    void wichtelParticipantConverter() {
        WichtelUser user = new WichtelUser("1","name","email");
        WichtelParticipant expected = new WichtelParticipant(user, InvitationStatus.PENDING,"wishList","address");
        WichtelParticipantDTO dto = toDTO(expected);
        WichtelParticipant actual = fromDTO(dto,user.getId(),"wishList","address");
        assertEquals(expected,actual);
    }

    @Test
    void wichtelEventConverter() {
        WichtelUser user = new WichtelUser("1","name","email");
        WichtelEvent expected = new WichtelEvent("id",user,
                "title",
                "description",
                "budget",
                "image",
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of(new WichtelParticipant(user,InvitationStatus.PENDING,"wishList","address")),
                new HashMap<>());
        WichtelEventDTO dto = toDTO(expected);
        WichtelEvent actual = fromDTO(dto,"id",user,List.of(new WichtelParticipant(user,InvitationStatus.PENDING,"wishList","address")),new HashMap<>());
        assertEquals(expected,actual);
    }
}