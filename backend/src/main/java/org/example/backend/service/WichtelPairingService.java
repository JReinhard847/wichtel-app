package org.example.backend.service;

import org.example.backend.model.WichtelEvent;
import org.example.backend.model.WichtelParticipant;
import org.example.backend.model.WichtelUser;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class WichtelPairingService {

    public Map<String, WichtelParticipant> generateSimplePairings(WichtelEvent event){
        Map<String,WichtelParticipant> pairings = IntStream.range(0, event.getParticipants().size() - 1)
                .boxed()
                .collect(Collectors.toMap(
                        i -> event.getParticipants().get(i).getParticipant().getId(),
                        i -> event.getParticipants().get(i + 1).withParticipant(event.getParticipants().get(i + 1).getParticipant().withId(null))
                ));
        pairings.put(event.getParticipants().getLast().getParticipant().getId(),event.getParticipants().getFirst());
        return pairings;
    }
}
