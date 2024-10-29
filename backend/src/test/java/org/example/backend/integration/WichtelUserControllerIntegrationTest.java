package org.example.backend.integration;

import org.example.backend.model.WichtelUser;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WichtelUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WichtelUserRepo repo;

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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @DirtiesContext
    @Test
    void findAll_getsUsers_ifUsersInDB() throws Exception {
        repo.save(WichtelUser.builder().id("1").name("name").email("email").build());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                            {
                                "id": "1",
                                "name": "name",
                                "email": "email"
                            }
                        ]
                        """));
    }

    @DirtiesContext
    @Test
    void findMe_shouldThrow_ifNotInDB() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
                        .with(oidcLogin().userInfoToken(token -> token
                                .claim("id", "1"))))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void findMe_shouldThrow_ifNotLoggedIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me"))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void findById_shouldReturnUser_ifInDB() throws Exception {
        repo.save(WichtelUser.builder().id("1").name("name").email("email").oauthId("githubid").oauthProvider("github").build());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                            {
                                "id": "1",
                                "name": "name",
                                "email": "email"
                            }
                        """));
    }

    @DirtiesContext
    @Test
    void delete_shouldDeleteUser() throws Exception {
        repo.save(WichtelUser.builder().id("1").name("name").email("email").oauthProvider("github").oauthId("githubid").build());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/1")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isOk());

        assertTrue(repo.findById("1").isEmpty());
    }

    @DirtiesContext
    @Test
    void delete_shouldThrow_ifDeletedUserNotLoggedIn() throws Exception {
        repo.save(WichtelUser.builder().id("1").name("name").email("email").oauthProvider("github").oauthId("githubid").build());
        repo.save(WichtelUser.builder().id("2").name("name").email("email").oauthProvider("github").oauthId("githubid2").build());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/2")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration)))
                .andExpect(status().isUnauthorized());
    }


    @DirtiesContext
    @Test
    void update_shouldUpdate_ifUserInDB() throws Exception {
        repo.save(WichtelUser.builder().id("1").name("name").email("email").oauthId("githubid").oauthProvider("github").build());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "name",
                                  "email": "email2"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "name": "name",
                          "email": "email2"
                        }
                        """));
        Optional<WichtelUser> updatedUser = repo.findById("1");
        assertTrue(updatedUser.isPresent());
        assertEquals("email2", updatedUser.get().getEmail());
    }

    @DirtiesContext
    @Test
    void update_shouldThrow_ifUserNotInDB() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user")
                        .with(oidcLogin()
                                .userInfoToken(token -> token.claim("id", "githubid"))
                                .clientRegistration(dummyRegistration))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "name",
                                  "email": "email2"
                                }
                                """))
                .andExpect(status().isNotFound());
        assertTrue(repo.findById("1").isEmpty());


    }


}
