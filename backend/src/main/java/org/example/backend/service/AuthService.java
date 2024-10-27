package org.example.backend.service;


import lombok.RequiredArgsConstructor;
import org.example.backend.model.WichtelUser;
import org.example.backend.repo.WichtelUserRepo;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final WichtelUserRepo repo;

    public WichtelUser getUserFromAuthToken(OAuth2AuthenticationToken authentication){
        OAuth2User oAuth2User = authentication.getPrincipal();

        String provider = authentication.getAuthorizedClientRegistrationId();
        String providerId = Objects.requireNonNull(oAuth2User.getAttribute("id")).toString();
        return repo.findByOauthProviderAndOauthId(provider,providerId).orElseThrow(NoSuchElementException::new);
    }

    public boolean loggedInUserHasId(OAuth2AuthenticationToken authentication,String id){
        WichtelUser user = getUserFromAuthToken(authentication);
        if(!user.getId().equals(id)){
            throw new IllegalCallerException();
        }
        return true;
    }

}
