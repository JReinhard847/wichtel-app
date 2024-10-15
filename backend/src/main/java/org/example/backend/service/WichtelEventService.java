package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.*;
import org.example.backend.repo.WichtelEventRepo;
import org.example.backend.util.DTOConverter;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.example.backend.util.DTOConverter.fromDTO;
import static org.example.backend.util.DTOConverter.toDTO;

@Service
@RequiredArgsConstructor
public class WichtelEventService {

    private final WichtelEventRepo repo;
    private final IdService idService;
    private final WichtelUserService userService;

    public String createEmptyEvent(String organizerId) {
        WichtelUser organizer = userService.findById(organizerId);
        WichtelEvent event = new WichtelEvent(idService.generateId(), organizer, "", "", "", "", null, null, Collections.emptyList(), new HashMap<>());
        repo.save(event);
        return event.getId();
    }

    public WichtelEventDTO findById(String id) {
        return toDTO(repo.findById(id).orElseThrow(NoSuchElementException::new));
    }

    public List<WichtelEventDTO> findAll() {
        return repo.findAll().stream()
                .map(DTOConverter::toDTO)
                .toList();
    }

    public WichtelEventDTO update(WichtelEventDTO updatedDto, String id) {
        WichtelEvent event = repo.findById(id).orElseThrow(NoSuchElementException::new);
        return toDTO(repo.save(fromDTO(updatedDto, id, event.getOrganizer(), event.getParticipants(), event.getPairings())));
    }

    public void deleteById(String id){
        repo.deleteById(id);
    }

    public void addParticipant(String eventId,String participantId){
        WichtelEvent event = repo.findById(eventId).orElseThrow(NoSuchElementException::new);
        WichtelUser participant = userService.findById(participantId);
        event.getParticipants().add(new WichtelParticipant(participant, InvitationStatus.PENDING,"",""));
        repo.save(event);
    }

    public void updateParticipant(String eventId,String participantId,InvitationStatus newStatus, String newWishList, String newAddress){
        WichtelEvent event = repo.findById(eventId).orElseThrow(NoSuchElementException::new);
        WichtelUser user = userService.findById(participantId);
        WichtelParticipant oldParticipant = event.getParticipants().stream()
                .filter(participant -> !Objects.equals(participant.getParticipant().getId(),participantId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        event.getParticipants().remove(oldParticipant);
        event.getParticipants().add(new WichtelParticipant(user,newStatus,newWishList,newAddress));
    }
}
