package org.example.backend.service;

import org.example.backend.model.*;
import org.example.backend.repo.WichtelEventRepo;
import org.example.backend.repo.WichtelUserRepo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.example.backend.util.DTOConverter.fromDTO;
import static org.example.backend.util.DTOConverter.toDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WichtelEventServiceTest {
    private final WichtelEventRepo repo = mock(WichtelEventRepo.class);
    private final IdService idService = mock(IdService.class);
    private final WichtelUserService userService = mock(WichtelUserService.class);
    private final WichtelEventService service = new WichtelEventService(repo, idService, userService);

    @Test
    void createEmptyEvent_createsEvent() {
        when(userService.findById("1")).thenReturn(new WichtelUser("1", "name", "email"));
        when(idService.generateId()).thenReturn("id");
        WichtelEvent expected = new WichtelEvent("id", new WichtelUser("1", "name", "email"), "", "", "", "", null, null, Collections.emptyList(), new HashMap<>());
        when(repo.save(any(WichtelEvent.class))).thenReturn(expected);
        String actual = service.createEmptyEvent("1");
        assertEquals(expected.getId(), actual);
    }

    @Test
    void findById_throws_ifEventNotInDB() {
        when(repo.findById("1")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.findById("1"));
        verify(repo).findById("1");
    }

    @Test
    void findById_findsEvent_ifInDB() {
        WichtelEvent expected = new WichtelEvent("id", new WichtelUser("1", "name", "email"), "", "", "", "", null, null, Collections.emptyList(), new HashMap<>());
        when(repo.findById("id")).thenReturn(Optional.of(expected));
        WichtelEventDTO actual = service.findById("id");
        verify(repo).findById("id");
        assertEquals(toDTO(expected), actual);
    }

    @Test
    void update() {
        WichtelEvent previous = new WichtelEvent("id", new WichtelUser("1", "name", "email"), "", "", "", "", null, null, Collections.emptyList(), new HashMap<>());
        WichtelUser organizer = new WichtelUser("1","name","email");
        WichtelEventDTO updated = new WichtelEventDTO(toDTO(organizer),
                "title",
                "description",
                "budged",
                "image",
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptyList());
        when(repo.findById("id")).thenReturn(Optional.of(previous));
        when(repo.save(any(WichtelEvent.class))).thenAnswer(input -> input.getArgument(0));
        WichtelEventDTO actual = service.update(updated,"id");
        verify(repo).save(any(WichtelEvent.class));
        assertEquals(updated,actual);
    }

    @Test
    void addParticipant_throws_ifAlreadyInEvent() {
        WichtelUser user = new WichtelUser("1","name","email");
        WichtelEvent event = new WichtelEvent("id", new WichtelUser("1", "name", "email"), "", "", "", "", null, null, List.of(new WichtelParticipant(user,null,null,null)), new HashMap<>());
        when(repo.findById("id")).thenReturn(Optional.of(event));
        assertThrows(IllegalArgumentException.class,() -> service.addParticipant("id","1"));
    }

    @Test
    void addParticipant_addsUserToEvent_ifNotAlreadyPresent() {
        WichtelUser user = new WichtelUser("1","name","email");
        WichtelUser secondUser = new WichtelUser("2","name2","email2");
        WichtelEvent event = new WichtelEvent("id", new WichtelUser("1", "name", "email"), "", "", "", "", null, null, new ArrayList<>(Arrays.asList(new WichtelParticipant(user,null,null,null))), new HashMap<>());
        when(repo.findById("id")).thenReturn(Optional.of(event));
        when(userService.findById("2")).thenReturn(secondUser);
        when(repo.save(any(WichtelEvent.class))).thenAnswer(input -> input.getArgument(0));
        WichtelEventDTO actual = service.addParticipant("id","2");
        verify(repo).save(any(WichtelEvent.class));
        assertEquals(2,actual.getParticipants().size());
    }

    @Test
    void updateParticipant_throws_ifUserIsNotInEvent() {
        WichtelUser user = new WichtelUser("1","name","email");
        WichtelEvent event = new WichtelEvent("id", new WichtelUser("1", "name", "email"), "", "", "", "", null, null, List.of(new WichtelParticipant(user,null,null,null)), new HashMap<>());
        when(repo.findById("id")).thenReturn(Optional.of(event));
        assertThrows(IllegalArgumentException.class,() -> service.updateParticipant("id",new WichtelParticipant(null,null,null,null),"2"));
    }

    @Test
    void updateParticipant_updates_ifUserIsInEvent(){
        WichtelUser user = new WichtelUser("1","name","email");
        WichtelEvent event = new WichtelEvent("id", new WichtelUser("1", "name", "email"), "", "", "", "", null, null, new ArrayList<>(Arrays.asList(new WichtelParticipant(user,InvitationStatus.PENDING,"",""))), new HashMap<>());
        when(repo.findById("id")).thenReturn(Optional.of(event));
        when(userService.findById("1")).thenReturn(user);
        when(repo.save(any(WichtelEvent.class))).thenAnswer(input -> input.getArgument(0));
        WichtelParticipant updated = new WichtelParticipant(user,InvitationStatus.ACCEPTED,"","");
        WichtelEventDTO actual = service.updateParticipant("id",updated,"1");
        assertEquals(toDTO(updated),actual.getParticipants().getFirst());
    }
}