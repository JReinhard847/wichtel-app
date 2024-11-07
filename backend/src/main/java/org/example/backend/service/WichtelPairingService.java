package org.example.backend.service;

import org.example.backend.model.WichtelEvent;
import org.example.backend.model.WichtelParticipant;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class WichtelPairingService {

    public Map<String, WichtelParticipant> generateSimplePairings(WichtelEvent event){
        List<WichtelParticipant> toShuffle = new ArrayList<>(event.getParticipants());
        Collections.shuffle(toShuffle);
        Map<String,WichtelParticipant> pairings = IntStream.range(0, toShuffle.size() - 1)
                .boxed()
                .collect(Collectors.toMap(
                        i -> toShuffle.get(i).getParticipant().getId(),
                        i -> toShuffle.get(i + 1)
                ));
        pairings.put(toShuffle.getLast().getParticipant().getId(),toShuffle.getFirst());
        return pairings;
    }
}
