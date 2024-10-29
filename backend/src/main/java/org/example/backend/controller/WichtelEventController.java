package org.example.backend.controller;


import lombok.RequiredArgsConstructor;
import org.example.backend.model.WichtelEventDTO;
import org.example.backend.model.WichtelParticipant;
import org.example.backend.service.AuthService;
import org.example.backend.service.WichtelEventService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RequestMapping("/api/event")
@RestController
@RequiredArgsConstructor
public class WichtelEventController {

    private final WichtelEventService service;
    private final AuthService authService;

    @PostMapping
    String createEvent(OAuth2AuthenticationToken authentication) {
        String organizerId = authService.getUserFromAuthToken(authentication).getId();
        return service.createEmptyEvent(organizerId);
    }

    @GetMapping
    List<WichtelEventDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    WichtelEventDTO findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    WichtelEventDTO update(@RequestBody WichtelEventDTO dto, @PathVariable String id, OAuth2AuthenticationToken authentication) {
        if (authService.isOrganizerOfEvent(authentication, id)) {
            return service.update(dto, id);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable String id, OAuth2AuthenticationToken authentication) {
        if (authService.isOrganizerOfEvent(authentication, id)) {
            service.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/{eventId}/{participantId}")
    WichtelEventDTO addParticipant(@PathVariable String eventId, @PathVariable String participantId, OAuth2AuthenticationToken authentication) {
        if (authService.isOrganizerOfEvent(authentication, eventId) || authService.loggedInUserHasId(authentication, participantId)) {
            return service.addParticipant(eventId, participantId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{eventId}/{participantId}")
    WichtelEventDTO updateParticipant(@PathVariable String eventId, @RequestBody WichtelParticipant participant, @PathVariable String participantId, OAuth2AuthenticationToken authentication) {
        if (authService.loggedInUserHasId(authentication, participantId)) {
            return service.updateParticipant(eventId, participant, participantId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{eventId}/{participantId}")
    WichtelEventDTO deleteParticipant(@PathVariable String eventId, @PathVariable String participantId, OAuth2AuthenticationToken authentication) {
        if (authService.isOrganizerOfEvent(authentication, eventId) || authService.loggedInUserHasId(authentication, participantId)) {
            return service.deleteParticipant(eventId, participantId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

    }

    @PostMapping("/pairings/{eventId}")
    WichtelEventDTO generatePairings(@PathVariable String eventId, OAuth2AuthenticationToken authentication) {
        if (authService.isOrganizerOfEvent(authentication, eventId)) {
            return service.generatePairings(eventId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

    }

    @GetMapping("/{eventId}/{participantId}")
    WichtelParticipant getMyPairing(@PathVariable String eventId, @PathVariable String participantId, OAuth2AuthenticationToken authentication) {
        if (authService.loggedInUserHasId(authentication, participantId)) {
            return service.getPairingOfUser(eventId, participantId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NoSuchElementException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleIllegalArgumentException(IllegalArgumentException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    public String handleIllegalStateException(IllegalStateException exception) {
        return exception.getMessage();
    }
}
