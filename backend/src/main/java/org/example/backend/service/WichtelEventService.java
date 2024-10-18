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

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public WichtelEventDTO addParticipant(String eventId, String participantId) {
        WichtelEvent event = repo.findById(eventId).orElseThrow(NoSuchElementException::new);
        if (!event.getParticipants().stream().filter(p -> p.getParticipant().getId().equals(participantId)).toList().isEmpty()) {
            throw new IllegalArgumentException("User with id " + participantId + " is already part of event " + eventId);
        }
        WichtelUser participant = userService.findById(participantId);
        event.getParticipants().add(new WichtelParticipant(participant, InvitationStatus.PENDING, "", ""));
        return toDTO(repo.save(event));
    }

    public WichtelEventDTO updateParticipant(String eventId, WichtelParticipant participant, String participantId) {
        WichtelEvent event = repo.findById(eventId).orElseThrow(NoSuchElementException::new);
        WichtelUser user = userService.findById(participantId);
        WichtelParticipant oldParticipant = event.getParticipants().stream()
                .filter(p -> Objects.equals(p.getParticipant().getId(), participantId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        event.getParticipants().remove(oldParticipant);
        event.getParticipants().add(new WichtelParticipant(user, participant.getInvitationStatus(), participant.getWishList(), participant.getAddress()));
        return toDTO(repo.save(event));
    }
}
