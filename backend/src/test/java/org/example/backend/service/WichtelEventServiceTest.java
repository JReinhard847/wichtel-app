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
    private final WichtelEventService service = new WichtelEventService(repo, idService, userService,null);

    @Test
    void createEmptyEvent_createsEvent() {
        when(userService.findById("1")).thenReturn(WichtelUser.builder().id("1").name("name").email("email").build());
        when(idService.generateId()).thenReturn("id");
        WichtelEvent expected = new WichtelEvent("id", WichtelUser.builder().id("1").name("name").email("email").build(), "", "", "", "", null, null, Collections.emptyList(), new HashMap<>());
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
        WichtelEvent expected = new WichtelEvent("id", WichtelUser.builder().id("1").name("name").email("email").build(), "", "", "", "", null, null, Collections.emptyList(), new HashMap<>());
        when(repo.findById("id")).thenReturn(Optional.of(expected));
        WichtelEventDTO actual = service.findById("id");
        verify(repo).findById("id");
        assertEquals(toDTO(expected), actual);
    }

    @Test
    void update() {
        WichtelEvent previous = new WichtelEvent("id", WichtelUser.builder().id("1").name("name").email("email").build(), "", "", "", "", null, null, Collections.emptyList(), new HashMap<>());
        WichtelUser organizer = WichtelUser.builder().id("1").name("name").email("email").build();
        WichtelEventDTO updated = new WichtelEventDTO("id",
                toDTO(organizer),
                "title",
                "description",
                "budged",
                "image",
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptyList(),
                false);
        when(repo.findById("id")).thenReturn(Optional.of(previous));
        when(repo.save(any(WichtelEvent.class))).thenAnswer(input -> input.getArgument(0));
        WichtelEventDTO actual = service.update(updated, "id");
        verify(repo).save(any(WichtelEvent.class));
        assertEquals(updated, actual);
    }

    @Test
    void addParticipant_throws_ifAlreadyInEvent() {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").build();
        WichtelEvent event = new WichtelEvent("id", WichtelUser.builder().id("1").name("name").email("email").build(), "", "", "", "", null, null, List.of(new WichtelParticipant(user, null, null, null)), new HashMap<>());
        when(repo.findById("id")).thenReturn(Optional.of(event));
        assertThrows(IllegalArgumentException.class, () -> service.addParticipant("id", "1"));
    }

    @Test
    void addParticipant_addsUserToEvent_ifNotAlreadyPresent() {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").build();
        WichtelUser secondUser = WichtelUser.builder().id("2").name("name").email("email").build();
        WichtelEvent event = new WichtelEvent("id", WichtelUser.builder().id("1").name("name").email("email").build(), "", "", "", "", null, null, new ArrayList<>(Arrays.asList(new WichtelParticipant(user, null, null, null))), new HashMap<>());
        when(repo.findById("id")).thenReturn(Optional.of(event));
        when(userService.findById("2")).thenReturn(secondUser);
        when(repo.save(any(WichtelEvent.class))).thenAnswer(input -> input.getArgument(0));
        WichtelEventDTO actual = service.addParticipant("id", "2");
        verify(repo).save(any(WichtelEvent.class));
        assertEquals(2, actual.getParticipants().size());
    }

    @Test
    void updateParticipant_throws_ifUserIsNotInEvent() {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").build();
        WichtelEvent event = new WichtelEvent("id", WichtelUser.builder().id("1").name("name").email("email").build(), "", "", "", "", null, null, List.of(new WichtelParticipant(user, null, null, null)), new HashMap<>());
        when(repo.findById("id")).thenReturn(Optional.of(event));
        assertThrows(IllegalArgumentException.class, () -> service.updateParticipant("id", new WichtelParticipant(null, null, null, null), "2"));
    }

    @Test
    void updateParticipant_updates_ifUserIsInEvent() {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").build();
        WichtelEvent event = new WichtelEvent("id", WichtelUser.builder().id("1").name("name").email("email").build(), "", "", "", "", null, null, new ArrayList<>(Arrays.asList(new WichtelParticipant(user, InvitationStatus.PENDING, "", ""))), new HashMap<>());
        when(repo.findById("id")).thenReturn(Optional.of(event));
        when(userService.findById("1")).thenReturn(user);
        when(repo.save(any(WichtelEvent.class))).thenAnswer(input -> input.getArgument(0));
        WichtelParticipant updated = new WichtelParticipant(user, InvitationStatus.ACCEPTED, "", "");
        WichtelEventDTO actual = service.updateParticipant("id", updated, "1");
        assertEquals(toDTO(updated), actual.getParticipants().getFirst());
    }

    @Test
    void deleteParticipant_throws_ifUserDoesntExist() {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").build();
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .participants(new ArrayList<>(List.of(new WichtelParticipant(user, InvitationStatus.PENDING, "", ""))))
                .build();
        when(userService.findById("1")).thenThrow(new NoSuchElementException());
        when(repo.findById("id")).thenReturn(Optional.of(event));
        assertThrows(NoSuchElementException.class, () -> service.deleteParticipant("id", "1"));
    }

    @Test
    void deleteParticipant_deletes_ifRequestValid() {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").build();
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .participants(new ArrayList<>(List.of(new WichtelParticipant(user, InvitationStatus.PENDING, "", ""))))
                .organizer(user)
                .build();
        when(userService.findById("1")).thenReturn(user);
        when(repo.findById("id")).thenReturn(Optional.of(event));
        when(repo.save(any(WichtelEvent.class))).thenAnswer(input -> input.getArgument(0));
        WichtelEventDTO result = service.deleteParticipant("id","1");
        assertEquals(0,result.getParticipants().size());
    }
}