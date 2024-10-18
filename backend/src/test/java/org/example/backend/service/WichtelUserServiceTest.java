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
        WichtelUser expected = new WichtelUser("1","name","email");
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
        WichtelUserDTO dto = new WichtelUserDTO("name","email");
        WichtelUser expected = new WichtelUser("1","name","email");

        when(idService.generateId()).thenReturn("1");
        when(repo.save(expected)).thenReturn(expected);

        WichtelUser actual = service.save(dto);
        verify(repo).save(any(WichtelUser.class));
        assertEquals(expected,actual);
    }

    @Test
    void update() {
        WichtelUser previous = new WichtelUser("1","name","email");
        WichtelUserDTO updatedDTO = new WichtelUserDTO("name2","email");
        WichtelUser expected = new WichtelUser("1","name2","email");
        when(repo.findById("1")).thenReturn(Optional.of(previous));
        when(repo.save(fromDTO(updatedDTO,"1"))).thenReturn(expected);

        WichtelUser actual = service.update(updatedDTO,"1");
        verify(repo).save(any(WichtelUser.class));
        assertEquals(expected,actual);


    }


    @Test
    void findAll_findsUsersInDB() {
        List<WichtelUser> expected = List.of(new WichtelUser("1","name","email"));
        when(repo.findAll()).thenReturn(expected);
        List<WichtelUser> actual = service.findAll();
        verify(repo).findAll();
        assertEquals(expected,actual);

    }
}