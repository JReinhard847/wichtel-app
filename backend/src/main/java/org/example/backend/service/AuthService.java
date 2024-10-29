package org.example.backend.service;



import lombok.RequiredArgsConstructor;
import org.example.backend.model.WichtelUser;
import org.example.backend.repo.WichtelEventRepo;
import org.example.backend.repo.WichtelUserRepo;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNullElse;

@Service
@RequiredArgsConstructor
public class AuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final WichtelUserRepo userRepo;
    private final WichtelEventRepo eventRepo;
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

    public WichtelUser getUserFromAuthToken(OAuth2AuthenticationToken authentication){
        if(authentication==null){
            throw new NoSuchElementException();
        }
        OAuth2User oAuth2User = authentication.getPrincipal();

        String provider = authentication.getAuthorizedClientRegistrationId();
        String providerId = Objects.requireNonNull(oAuth2User.getAttribute("id")).toString();
        return userRepo.findByOauthProviderAndOauthId(provider,providerId).orElseThrow(NoSuchElementException::new);
    }

    public boolean loggedInUserHasId(OAuth2AuthenticationToken authentication,String id){
        if(authentication==null){
            return false;
        }
        WichtelUser user = getUserFromAuthToken(authentication);
        return user.getId().equals(id);
    }

    public boolean isOrganizerOfEvent(OAuth2AuthenticationToken authentication,String eventId){
        if(authentication==null){
            return false;
        }
        WichtelUser user = getUserFromAuthToken(authentication);
        return eventRepo.findById(eventId).orElseThrow(NoSuchElementException::new).getOrganizer().getId().equals(user.getId());
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = Optional.ofNullable(oAuth2User.getAttribute("id"))
                .map(Object::toString)
                .orElse("");
        String email = requireNonNullElse(oAuth2User.getAttribute("email"),"");
        String name = requireNonNullElse(oAuth2User.getAttribute("login"),"");
        Optional<WichtelUser> userOptional = userRepo.findByOauthProviderAndOauthId(provider,providerId);
        if(userOptional.isEmpty()) {
            WichtelUser user = WichtelUser.builder()
                    .email(email)
                    .name(name)
                    .oauthProvider(provider)
                    .oauthId(providerId)
                    .build();
            userRepo.save(user);
        }
        return new DefaultOAuth2User(null, oAuth2User.getAttributes(), "id");
    }

}