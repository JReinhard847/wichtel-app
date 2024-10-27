package org.example.backend.integration;

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
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
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

    private final ClientRegistration dummyRegistration = ClientRegistration
            .withRegistrationId("github")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("test")
            .clientSecret("")
            .clientName("")
            .redirectUri("test")
            .authorizationUri("test")
            .tokenUri("test")
            .build();

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
        WichtelEvent event = new WichtelEvent("id", WichtelUser.builder().id("1").name("name").email("email").build(), "test title", "", "", "", null, null, Collections.emptyList(), new HashMap<>());

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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/event")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration))
                )
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void createEvent_createsAnEvent_ifCreatingUserExists() throws Exception {
        userRepo.save(WichtelUser.builder().id("1").name("name").email("email").oauthId("githubid").oauthProvider("github").build());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/event")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
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
        WichtelEvent event = new WichtelEvent("id", WichtelUser.builder().id("1").name("name").email("email").build(), "test title", "", "", "", null, null, Collections.emptyList(), new HashMap<>());
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
    void delete_shouldDeleteEvent_ifCalledByOrganizer() throws Exception {
        WichtelUser organizer = WichtelUser.builder().id("1").name("name").email("email").oauthId("githubid").oauthProvider("github").build();
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(organizer)
                .build();
        repo.save(event);

        userRepo.save(organizer);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/event/id")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isOk());

        assertTrue(repo.findById("id").isEmpty());
    }

    @DirtiesContext
    @Test
    void delete_shouldThrow_ifCalledByNonOrganizer() throws Exception {
        WichtelUser organizer = WichtelUser.builder().id("1").name("name").email("email").oauthId("githubid").oauthProvider("github").build();
        WichtelUser fakeOrganizer = WichtelUser.builder().id("2").name("name").email("email").oauthId("githubid2").oauthProvider("github").build();
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(organizer)
                .build();
        repo.save(event);

        userRepo.save(organizer);
        userRepo.save(fakeOrganizer);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/event/id")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid2"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isUnauthorized());

        assertTrue(repo.findById("id").isPresent());
    }


    @DirtiesContext
    @Test
    void update_shouldThrow_ifEventDoesntExist() throws Exception {
        WichtelUser organizer = WichtelUser.builder().id("1").name("name").email("email").oauthId("githubid").oauthProvider("github").build();
        userRepo.save(organizer);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration))
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
    void update_shouldThrow_ifCalledByNonOrganizer() throws Exception {
        WichtelUser organizer = WichtelUser.builder().id("1").name("name").email("email").oauthId("githubid").oauthProvider("github").build();
        WichtelUser fakeOrganizer = WichtelUser.builder().id("2").name("name").email("email").oauthId("githubid2").oauthProvider("github").build();
        userRepo.save(fakeOrganizer);
        userRepo.save(organizer);

        WichtelEvent event = WichtelEvent.builder()
                .organizer(organizer)
                .id("id")
                .build();
        repo.save(event);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid2"))
                                .clientRegistration(dummyRegistration))
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
                .andExpect(status().isUnauthorized());
    }

    @DirtiesContext
    @Test
    void update_shouldUpdate_ifEventInDB() throws Exception {
        WichtelUser organizer = WichtelUser.builder().id("1").name("name").email("email").oauthId("githubid").oauthProvider("github").build();
        userRepo.save(organizer);

        WichtelEvent event = WichtelEvent.builder()
                .organizer(organizer)
                .id("id")
                .participants(Collections.emptyList())
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration))
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
    void addParticipant_shouldThrow_NotCalledByUserOrOrganizer() throws Exception {
        WichtelUser user = WichtelUser.builder().id("2").oauthProvider("github").oauthId("githubid").build();
        userRepo.save(user);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(WichtelUser.builder().id("1").build())
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isUnauthorized());
    }

    @DirtiesContext
    @Test
    void addParticipant_shouldThrow_ifEventDoesntExistInDB() throws Exception {
        userRepo.save(WichtelUser.builder().id("1").oauthProvider("github").oauthId("githubid").build());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid2"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void addParticipant_shouldThrow_ifAlreadyParticipating() throws Exception {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").oauthId("githubid").oauthProvider("github").build();
        userRepo.save(user);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user)
                .participants(new ArrayList<>(List.of(WichtelParticipant.builder().participant(user).build())))
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isConflict());
    }

    @DirtiesContext
    @Test
    void addParticipant_shouldAdd_ifRequestValid() throws Exception {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").oauthId("githubid").oauthProvider("github").build();
        userRepo.save(user);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user)
                .participants(new ArrayList<>())
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                              "participants": [{
                                                "participant": { "name": "name","email":"email"},
                                                "invitationStatus": "PENDING"
                                                }
                                                ]
                        }"""));
    }

    @DirtiesContext
    @Test
    void updateParticipant_shouldThrow_NotCalledByUser() throws Exception {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").build();
        userRepo.save(user);
        WichtelUser fakeUser = WichtelUser.builder().id("2").oauthId("githubid").oauthProvider("github").build();
        userRepo.save(fakeUser);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user)
                .participants(new ArrayList<>(List.of(WichtelParticipant.builder().participant(user).build())))
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                      "participant": { "id": 1,"name": "name","email":"email"},
                                      "invitationStatus": "PENDING",
                                      "wishList": "",
                                      "address": ""
                                }"""))
                .andExpect(status().isUnauthorized());
    }

    @DirtiesContext
    @Test
    void updateParticipant_shouldThrow_ifEventDoesntExist() throws Exception {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").build();
        userRepo.save(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration))
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
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").oauthProvider("github").oauthId("githubid").build();
        userRepo.save(user);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user)
                .participants(new ArrayList<>())
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration))
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
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").oauthId("githubid").oauthProvider("github").build();
        userRepo.save(user);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user)
                .participants(new ArrayList<>(List.of(WichtelParticipant.builder().participant(user).build())))
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration))
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
    void deleteParticipant_shouldThrow_NotCalledByUserOrOrganizer() throws Exception {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").build();
        userRepo.save(user);
        WichtelUser fakeUser = WichtelUser.builder().id("2").oauthProvider("github").oauthId("githubid").build();
        userRepo.save(fakeUser);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user)
                .participants(new ArrayList<>(List.of(WichtelParticipant.builder().participant(user).build())))
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isUnauthorized());
    }

    @DirtiesContext
    @Test
    void deleteParticipant_shouldThrow_ifEventDoesntExist() throws Exception {
        WichtelUser user = WichtelUser.builder().id("1").name("name").email("email").oauthProvider("github").oauthId("githubid").build();
        userRepo.save(user);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void deleteParticipant_shouldDelete_ifRequestValid() throws Exception {
        WichtelUser user = WichtelUser.builder().id("1").oauthId("githubid").oauthProvider("github").build();
        userRepo.save(user);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user)
                .participants(new ArrayList<>(List.of(WichtelParticipant.builder().participant(user).build())))
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                              "participants": [ ]}"""));
    }

    @DirtiesContext
    @Test
    void generatePairings_throws_ifNotCalledByOrganizer() throws Exception {
        WichtelUser user = WichtelUser.builder().id("1").build();
        WichtelUser fakeUser = WichtelUser.builder().id("2").oauthProvider("github").oauthId("githubid").build();
        userRepo.save(fakeUser);

        userRepo.save(user);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user)
                .participants(new ArrayList<>())
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/pairings/id")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isUnauthorized());

    }

    @DirtiesContext
    @Test
    void generatePairings_throws_ifNotEnoughParticipants() throws Exception {
        WichtelUser user = WichtelUser.builder().id("1").oauthProvider("github").oauthId("githubid").build();
        userRepo.save(user);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user)
                .participants(new ArrayList<>(List.of(WichtelParticipant.builder().participant(user).build())))
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/pairings/id")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isPreconditionRequired());

    }

    @DirtiesContext
    @Test
    void generatePairings_generates_ifRequestValid() throws Exception {
        WichtelUser user1 = WichtelUser.builder().id("1").name("name").email("email").oauthId("githubid").oauthProvider("github").build();
        WichtelUser user2 = WichtelUser.builder().id("2").name("name").email("email").build();
        userRepo.save(user1);
        userRepo.save(user2);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user1)
                .pairings(new HashMap<>())
                .participants(List.of(WichtelParticipant.builder().participant(user1).build(),
                        WichtelParticipant.builder().participant(user2).build()))
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/event/pairings/id")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isOk());
    }

    @DirtiesContext
    @Test
    void getMyPairing_throws_IfNoPairingGenerated() throws Exception {
        WichtelUser user1 = WichtelUser.builder().id("1").name("name").email("email").oauthProvider("github").oauthId("githubid").build();
        WichtelUser user2 = WichtelUser.builder().id("2").name("name").email("email").build();
        userRepo.save(user1);
        userRepo.save(user2);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user1)
                .pairings(new HashMap<>())
                .participants(List.of(WichtelParticipant.builder().participant(user1).build(),
                        WichtelParticipant.builder().participant(user2).build()))
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isPreconditionRequired());
    }

    @DirtiesContext
    @Test
    void getMyPairing_getsPairing_ifRequestValid() throws Exception {
        WichtelUser user1 = WichtelUser.builder().id("1").name("name").email("email").oauthProvider("github").oauthId("githubid").build();
        WichtelUser user2 = WichtelUser.builder().id("2").name("name").email("email").build();
        userRepo.save(user1);
        userRepo.save(user2);
        WichtelEvent event = WichtelEvent.builder()
                .id("id")
                .organizer(user1)
                .pairings(Map.ofEntries(
                        Map.entry("1", WichtelParticipant.builder().participant(user2).wishList("pony").build()),
                        Map.entry("2", WichtelParticipant.builder().participant(user1).build())
                ))
                .participants(List.of(WichtelParticipant.builder().participant(user1).build(),
                        WichtelParticipant.builder().participant(user2).build()))
                .build();
        repo.save(event);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/event/id/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "wishList": "pony"
                        }"""));
    }

}
