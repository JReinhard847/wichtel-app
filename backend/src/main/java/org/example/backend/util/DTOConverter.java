package org.example.backend.util;

import org.example.backend.model.*;


public class DTOConverter {

    private DTOConverter(){
        throw new UnsupportedOperationException();
    }

    public static WichtelUserDTO toDTO(WichtelUser user) {
        return new WichtelUserDTO(user.getName(), user.getEmail());
    }

    public static WichtelUser fromDTO(WichtelUserDTO dto, String id) {
        return new WichtelUser(id, dto.getName(), dto.getEmail());
    }

}
