package org.example.backend.service;

import org.example.backend.model.WichtelEvent;
import org.example.backend.model.WichtelParticipant;
import org.example.backend.model.WichtelUser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class WichtelPairingServiceTest {

    WichtelPairingService service = new WichtelPairingService();

    boolean isValidPairing(Map<String, WichtelParticipant> pairing, WichtelEvent event) {//"definition" of a valid pairing
        List<WichtelParticipant> participants = new ArrayList<>(event.getParticipants());
        if(!pairing.keySet().equals(//pairing contains exactly the participants of event
                event.getParticipants().stream()
                        .map(participant -> participant.getParticipant().getId())
                        .collect(Collectors.toSet())
        )){
            return false;
        }
        for (WichtelParticipant participant : participants) {
            if(!event.getParticipants().contains(pairing.get(participant.getParticipant().getId()))){//the "target" of each participant is also participating
                return false;
            }
            if(pairing.get(participant.getParticipant().getId()).equals(participant)){//no participant should target him/herself
                return false;
            }
        }
        return true;
    }

    @Test
    void generateSimplePairings() {
        WichtelParticipant p1 = WichtelParticipant.builder()
                .participant(WichtelUser.builder().id("1").name("name").email("email").build()).build();
        WichtelParticipant p2 = WichtelParticipant.builder()
                .participant(WichtelUser.builder().id("2").name("name").email("email").build()).build();
        WichtelParticipant p3 = WichtelParticipant.builder()
                .participant(WichtelUser.builder().id("3").name("name").email("email").build()).build();
        WichtelEvent event = WichtelEvent.builder()
                .participants(
                        List.of(p1,p2,p3)
                ).build();
        Map<String, WichtelParticipant> pairing = service.generateSimplePairings(event);
        assertTrue(isValidPairing(pairing,event));
    }
}