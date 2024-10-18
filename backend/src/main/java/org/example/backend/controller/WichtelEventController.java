package org.example.backend.controller;


import lombok.RequiredArgsConstructor;
import org.example.backend.model.WichtelEventDTO;
import org.example.backend.model.WichtelParticipant;
import org.example.backend.service.WichtelEventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RequestMapping("/api/event")
@RestController
@RequiredArgsConstructor
public class WichtelEventController {

    private final WichtelEventService service;

    @PostMapping("/{organizerId}")
    String createEvent(@PathVariable String organizerId){
        return service.createEmptyEvent(organizerId);
    }

    @GetMapping
    List<WichtelEventDTO> findAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    WichtelEventDTO findById(@PathVariable String id){
        return service.findById(id);
    }

    @PutMapping("/{id}")
    WichtelEventDTO update(@RequestBody WichtelEventDTO dto, @PathVariable String id){
        return service.update(dto,id);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable String id){
        service.deleteById(id);
    }

    @PostMapping("/{eventId}/{participantId}")
    WichtelEventDTO addParticipant(@PathVariable String eventId,@PathVariable String participantId){
        return service.addParticipant(eventId,participantId);
    }

    @PutMapping("/{eventId}/{participantId}")
    WichtelEventDTO updateParticipant(@PathVariable String eventId, @RequestBody WichtelParticipant participant,@PathVariable String participantId){
        return service.updateParticipant(eventId,participant,participantId);
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



}
