package org.example.backend.util;

import org.example.backend.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.*;

import static org.example.backend.util.DTOConverter.fromDTO;
import static org.example.backend.util.DTOConverter.toDTO;
import static org.junit.jupiter.api.Assertions.*;

class DTOConverterTest {

    @Test
    void wichtelUserConverter() {
        WichtelUser expected = WichtelUser.builder().id("1").name("name").email("email").build();
        WichtelUserDTO dto = toDTO(expected);
        WichtelUser actual = fromDTO(dto,"1");
        assertEquals(expected,actual);
    }

    @Test
    void wichtelParticipantConverter() {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").build();
        WichtelParticipant expected = new WichtelParticipant(user, InvitationStatus.PENDING,"wishList","address");
        WichtelParticipantDTO dto = toDTO(expected);
        WichtelParticipant actual = fromDTO(dto,user.getId(),"wishList","address");
        assertEquals(expected,actual);
    }

    @Test
    void wichtelEventConverter() {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").build();
        WichtelEvent expected = new WichtelEvent("id",user,
                "title",
                "description",
                "budget",
                "image",
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of(new WichtelParticipant(user,InvitationStatus.PENDING,"wishList","address")),
                new HashMap<>());
        WichtelEventDTO dto = toDTO(expected);
        WichtelEvent actual = fromDTO(dto,"id",user,List.of(new WichtelParticipant(user,InvitationStatus.PENDING,"wishList","address")),new HashMap<>());
        assertEquals(expected,actual);
    }

    public void updateIgnoringNulls(Object update,Object original){
        final BeanWrapper src = new BeanWrapperImpl(update);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> nullAttributes = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) nullAttributes.add(pd.getName());
        }
        BeanUtils.copyProperties(update,original,nullAttributes.toArray(new String[0]));
    }


    @Test
    void testNonNullUpdater(){
        WichtelUser originalUser = WichtelUser.builder().name("old name").email("old email").build();
        WichtelUser updatedUser = WichtelUser.builder().name("new name").build();

        updateIgnoringNulls(updatedUser,originalUser);

        assertEquals("new name",originalUser.getName());
        assertEquals("old email",originalUser.getEmail());
    }
}