package org.example.backend.service;

import org.example.backend.model.WichtelUser;
import org.example.backend.model.WichtelUserDTO;
import org.example.backend.repo.WichtelUserRepo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.example.backend.util.DTOConverter.fromDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class WichtelUserServiceTest {

    private final WichtelUserRepo repo = mock(WichtelUserRepo.class);
    private final IdService idService = mock(IdService.class);
    private final WichtelUserService service = new WichtelUserService(repo,idService);

    @Test
    void findById_findsUser_ifInDB() {
        WichtelUser expected = WichtelUser.builder().id("1").name("name").email("email").build();
        when(repo.findById("1")).thenReturn(Optional.of(expected));
        WichtelUser actual = service.findById("1");
        verify(repo).findById("1");
        assertEquals(expected,actual);
    }

    @Test
    void findById_throws_ifUserNotInDB() {
        when(repo.findById("1")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,() -> service.findById("1"));
        verify(repo).findById("1");
    }

    @Test
    void save() {
        WichtelUserDTO dto = WichtelUserDTO.builder().name("name").email("email").build();
        WichtelUser expected = WichtelUser.builder().id("1").name("name").email("email").build();

        when(idService.generateId()).thenReturn("1");
        when(repo.save(expected)).thenReturn(expected);

        WichtelUser actual = service.save(dto);
        verify(repo).save(any(WichtelUser.class));
        assertEquals(expected,actual);
    }

    @Test
    void update() {
        WichtelUser previous = WichtelUser.builder().id("1").name("name").email("email").build();
        WichtelUserDTO updatedDTO = WichtelUserDTO.builder().name("name2").email("email").build();
        WichtelUser expected = WichtelUser.builder().id("1").name("name").email("email").build();
        when(repo.findById("1")).thenReturn(Optional.of(previous));
        when(repo.save(fromDTO(updatedDTO,"1"))).thenReturn(expected);

        WichtelUser actual = service.update(updatedDTO,"1");
        verify(repo).save(any(WichtelUser.class));
        assertEquals(expected,actual);


    }


    @Test
    void findAll_findsUsersInDB() {
        List<WichtelUser> expected = List.of(WichtelUser.builder().id("1").name("name").email("email").build());
        when(repo.findAll()).thenReturn(expected);
        List<WichtelUser> actual = service.findAll();
        verify(repo).findAll();
        assertEquals(expected,actual);

    }
}