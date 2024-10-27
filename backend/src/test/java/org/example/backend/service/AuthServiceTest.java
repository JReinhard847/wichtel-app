package org.example.backend.service;

import org.example.backend.model.WichtelUser;
import org.example.backend.repo.WichtelEventRepo;
import org.example.backend.repo.WichtelUserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Test
    void loadUser_savesUser_ifNotAlreadySaved() {
        WichtelUserRepo userRepo = mock(WichtelUserRepo.class);
        WichtelEventRepo eventRepo = mock(WichtelEventRepo.class);
        OAuth2UserRequest request = mock(OAuth2UserRequest.class);
        OAuth2User user = mock(OAuth2User.class);
        ClientRegistration registration = mock(ClientRegistration.class);
        OAuth2UserService<OAuth2UserRequest, OAuth2User> mockDelegate = (OAuth2UserService<OAuth2UserRequest, OAuth2User>) mock(OAuth2UserService.class);
        AuthService service = new AuthService(userRepo,eventRepo);
        when(mockDelegate.loadUser(any())).thenReturn(user);
        when(request.getClientRegistration()).thenReturn(registration);
        when(registration.getRegistrationId()).thenReturn("github");
        when(user.getAttribute(any())).thenReturn("githubid");
        when(user.getAttributes()).thenReturn(Map.of("id","githubid"));
        when(userRepo.findByOauthProviderAndOauthId(any(),any())).thenReturn(Optional.empty());
        service.delegate = mockDelegate;
        service.loadUser(request);
        verify(userRepo).save(any());
    }
    @Test
    void loadUser_DoesntSaveUser_ifAlreadySaved() {
        WichtelUserRepo userRepo = mock(WichtelUserRepo.class);
        WichtelEventRepo eventRepo = mock(WichtelEventRepo.class);
        OAuth2UserRequest request = mock(OAuth2UserRequest.class);
        OAuth2User user = mock(OAuth2User.class);
        ClientRegistration registration = mock(ClientRegistration.class);
        OAuth2UserService<OAuth2UserRequest, OAuth2User> mockDelegate = (OAuth2UserService<OAuth2UserRequest, OAuth2User>) mock(OAuth2UserService.class);
        AuthService service = new AuthService(userRepo,eventRepo);
        when(mockDelegate.loadUser(any())).thenReturn(user);
        when(request.getClientRegistration()).thenReturn(registration);
        when(registration.getRegistrationId()).thenReturn("github");
        when(user.getAttribute(any())).thenReturn("githubid");
        when(user.getAttributes()).thenReturn(Map.of("id","githubid"));
        when(userRepo.findByOauthProviderAndOauthId(any(),any())).thenReturn(Optional.of(WichtelUser.builder().build()));
        service.delegate = mockDelegate;
        service.loadUser(request);
        verify(userRepo,never()).save(any());
    }
}