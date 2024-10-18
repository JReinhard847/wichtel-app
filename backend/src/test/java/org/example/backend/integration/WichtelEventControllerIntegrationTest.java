package org.example.backend.integration;

import org.example.backend.model.InvitationStatus;
import org.example.backend.model.WichtelEvent;
import org.example.backend.model.WichtelParticipant;
import org.example.backend.model.WichtelUser;
import org.example.backend.repo.WichtelEventRepo;
import org.example.backend.repo.WichtelUserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WichtelEventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WichtelEventRepo repo;

    @Autowired
    private WichtelUserRepo userRepo;

    @DirtiesContext
    @Test
    void findAll_returnsEmpty_ifDBEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/event"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @DirtiesContext
    @Test
    void findAll_getsEvent_ifEventInDB() throws Exception {
        WichtelEvent event = new WichtelEvent("id", new WichtelUser("1", "name", "email"), "test title", "", "", "", null, null, Collections.emptyList(), new HashMap<>());

        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/event"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                            {
                                "organizer": {
                                                "name": "name",
                                                "email": "email"
                                              },
                                "title": "test title",
                                "description":"",
                                "budget":"",
                                "image":"",
                                "drawDate":null,
                                "giftExchangeDate":null,
                                "participants":[]
                        
                            }
                        ]
                        """));
    }

    @DirtiesContext
    @Test
    void createEvent_throws_ifCreatingUserDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/1"))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void createEvent_createsAnEvent_ifCreatingUserExists() throws Exception {
        userRepo.save(new WichtelUser("1", "name", "email"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/1"))
                .andExpect(status().isOk());
        assertEquals(1, repo.count());
    }

    @DirtiesContext
    @Test
    void findById_shouldThrow_onEmptyDB() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/event/1"))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void findById_shouldReturnUser_ifInDB() throws Exception {
        WichtelEvent event = new WichtelEvent("id", new WichtelUser("1", "name", "email"), "test title", "", "", "", null, null, Collections.emptyList(), new HashMap<>());
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/event/id"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                            {
                            "organizer": {
                                            "name": "name",
                                            "email": "email"
                                          },
                            "title": "test title",
                            "description":"",
                            "budget":"",
                            "image":"",
                            "drawDate":null,
                            "giftExchangeDate":null,
                            "participants":[]
                            }
                        """));
    }

    @DirtiesContext
    @Test
    void delete_shouldDeleteEvent() throws Exception {
        WichtelEvent event = new WichtelEvent("id", new WichtelUser("1", "name", "email"), "test title", "", "", "", null, null, Collections.emptyList(), new HashMap<>());
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/event/id"))
                .andExpect(status().isOk());

        assertTrue(repo.findById("id").isEmpty());
    }

    @DirtiesContext
    @Test
    void update_shouldThrow_ifEventDoesntExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "organizer": {
                                                "name": "name",
                                                "email": "email"
                                              },
                                "title": "test title",
                                "description":"",
                                "budget":"",
                                "image":"",
                                "drawDate":null,
                                "giftExchangeDate":null,
                                "participants":[]
                                }"""))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void update_shouldUpdate_ifEventInDB() throws Exception {
        WichtelEvent event = new WichtelEvent("id", new WichtelUser("1", "name", "email"), "", "", "", "", null, null, Collections.emptyList(), new HashMap<>());
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "organizer": {
                                                "name": "name",
                                                "email": "email"
                                              },
                                "title": "test title",
                                "description":"",
                                "budget":"",
                                "image":"",
                                "drawDate":null,
                                "giftExchangeDate":null,
                                "participants":[]
                                }"""))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                        "organizer": {
                                        "name": "name",
                                        "email": "email"
                                      },
                        "title": "test title",
                        "description":"",
                        "budget":"",
                        "image":"",
                        "drawDate":null,
                        "giftExchangeDate":null,
                        "participants":[]
                        }"""));

        assertEquals("test title", repo.findById("id").orElseThrow().getTitle());
    }

    @DirtiesContext
    @Test
    void addParticipant_shouldThrow_ifUserDoesntExistInDB() throws Exception {
        WichtelEvent event = new WichtelEvent("id", new WichtelUser("1", "name", "email"), "", "", "", "", null, null, new ArrayList<>(), new HashMap<>());
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/id/1"))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void addParticipant_shouldThrow_ifEventDoesntExistInDB() throws Exception {
        userRepo.save(new WichtelUser("1", "name", "email"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/id/1"))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void addParticipant_shouldThrow_ifAlreadyParticipating() throws Exception {
        WichtelUser user = new WichtelUser("1", "name", "email");
        userRepo.save(user);
        WichtelEvent event = new WichtelEvent("id", user, "", "", "", "", null, null, new ArrayList<>(List.of(new WichtelParticipant(user, InvitationStatus.PENDING, "", ""))), new HashMap<>());
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/id/1"))
                .andExpect(status().isConflict());
    }

    @DirtiesContext
    @Test
    void addParticipant_shouldAdd_ifRequestValid() throws Exception {
        WichtelUser user = new WichtelUser("1", "name", "email");
        userRepo.save(user);
        WichtelEvent event = new WichtelEvent("id", user, "", "", "", "", null, null, new ArrayList<>(), new HashMap<>());
        repo.save(event);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/id/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                        "participants": [{"participant":{"name":"name","email":"email"},"invitationStatus":"PENDING"}]
                        }
                        """));
    }

    @DirtiesContext
    @Test
    void updateParticipant_shouldThrow_ifUserDoesntExistInDB() throws Exception {
        WichtelUser user = new WichtelUser("1", "name", "email");
        WichtelEvent event = new WichtelEvent("id", user, "", "", "", "", null, null, new ArrayList<>(List.of(new WichtelParticipant(user, InvitationStatus.PENDING, "", ""))), new HashMap<>());
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                      "participant": { "id": 1,"name": "name","email":"email"},
                                      "invitationStatus": "PENDING",
                                      "wishList": "",
                                      "address": ""
                                }"""))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void updateParticipant_shouldThrow_ifEventDoesntExist() throws Exception {
        WichtelUser user = new WichtelUser("1", "name", "email");
        userRepo.save(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                      "participant": { "id": 1,"name": "name","email":"email"},
                                      "invitationStatus": "PENDING",
                                      "wishList": "",
                                      "address": ""
                                }"""))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void updateParticipant_shouldThrow_ifUserIsNotParticipating() throws Exception {
        WichtelUser user = new WichtelUser("1", "name", "email");
        userRepo.save(user);
        WichtelEvent event = new WichtelEvent("id", user, "", "", "", "", null, null, new ArrayList<>(), new HashMap<>());
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                      "participant": { "id": 1,"name": "name","email":"email"},
                                      "invitationStatus": "PENDING",
                                      "wishList": "",
                                      "address": ""
                                }"""))
                .andExpect(status().isConflict());
    }

    @DirtiesContext
    @Test
    void updateParticipant_shouldUpdate_ifRequestValid() throws Exception {
        WichtelUser user = new WichtelUser("1", "name", "email");
        userRepo.save(user);
        WichtelEvent event = new WichtelEvent("id", user, "", "", "", "", null, null, new ArrayList<>(List.of(new WichtelParticipant(user, InvitationStatus.PENDING, "", ""))), new HashMap<>());
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                      "participant": { "id": 1,"name": "name","email":"email"},
                                      "invitationStatus": "ACCEPTED",
                                      "wishList": "",
                                      "address": ""
                                }"""))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                              "participants": [{
                                                "participant": { "name": "name","email":"email"},
                                                "invitationStatus": "ACCEPTED"}
                                                ]}"""));
    }

    @DirtiesContext
    @Test
    void deleteParticipant_shouldThrow_ifUserDoesntExistInDB() throws Exception {
        WichtelUser user = new WichtelUser("1", "name", "email");
        WichtelEvent event = new WichtelEvent("id", user, "", "", "", "", null, null, new ArrayList<>(List.of(new WichtelParticipant(user, InvitationStatus.PENDING, "", ""))), new HashMap<>());
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/event/id/1"))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void deleteParticipant_shouldThrow_ifEventDoesntExist() throws Exception {
        WichtelUser user = new WichtelUser("1", "name", "email");
        userRepo.save(user);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/event/id/1"))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void deleteParticipant_shouldDelete_ifRequestValid() throws Exception {
        WichtelUser user = new WichtelUser("1", "name", "email");
        userRepo.save(user);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user)
                .participants(new ArrayList<>(List.of(new WichtelParticipant(user, InvitationStatus.PENDING, "", ""))))
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/event/id/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                              "participants": [ ]}"""));
    }

}
