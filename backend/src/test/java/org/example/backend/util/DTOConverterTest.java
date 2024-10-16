package org.example.backend.util;

import org.example.backend.model.WichtelUser;
import org.example.backend.model.WichtelUserDTO;
import org.junit.jupiter.api.Test;

import static org.example.backend.util.DTOConverter.fromDTO;
import static org.example.backend.util.DTOConverter.toDTO;
import static org.junit.jupiter.api.Assertions.*;

class DTOConverterTest {

    @Test
    void wichtelUserConverter() {
        WichtelUser expected = new WichtelUser("1","name","email");
        WichtelUserDTO dto = toDTO(expected);
        WichtelUser actual = fromDTO(dto,"1");
        assertEquals(expected,actual);
    }

}