package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.WichtelUser;
import org.example.backend.model.WichtelUserDTO;
import org.example.backend.repo.WichtelUserRepo;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Objects.requireNonNullElse;
import static org.example.backend.util.DTOConverter.fromDTO;

@Service
@RequiredArgsConstructor
public class WichtelUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final WichtelUserRepo repo;
    private final IdService idService;

    public WichtelUser findById(String id) {
        return repo.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public WichtelUser save(WichtelUserDTO dto) {
        return repo.save(fromDTO(dto, idService.generateId()));
    }

    public void deleteById(String id){
        repo.deleteById(id);
    }

    public WichtelUser update(WichtelUserDTO dto,String id){
        if(repo.findById(id).isEmpty()){
            throw new NoSuchElementException();
        }
        return repo.save(fromDTO(dto,id));
    }

    public List<WichtelUser> findAll(){
        return repo.findAll();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String,Object> attr = oAuth2User.getAttributes();
        String providerId = attr.get("id").toString();

        String email = requireNonNullElse(attr.get("email"),"").toString();
        String name = requireNonNullElse(attr.get("login"),"").toString();

        Optional<WichtelUser> userOptional = repo.findByOauthProviderAndOauthId(provider,providerId);
        if(userOptional.isEmpty()) {
            WichtelUser user = WichtelUser.builder()
                    .email(email)
                    .name(name)
                    .oauthProvider(provider)
                    .oauthId(providerId)
                    .build();
            repo.save(user);
        }

        return new DefaultOAuth2User(
                null,
                oAuth2User.getAttributes(),
                "id"
        );
    }
}
