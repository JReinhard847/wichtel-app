package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.WichtelUser;
import org.example.backend.model.WichtelUserDTO;
import org.example.backend.service.AuthService;
import org.example.backend.service.WichtelUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class WichtelUserController {

    private final WichtelUserService service;
    private final AuthService authService;

    @GetMapping("/me")
    WichtelUser findMe(OAuth2AuthenticationToken authentication){
        return authService.getUserFromAuthToken(authentication);
    }

    @GetMapping
    List<WichtelUser> findAll(){
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    void deleteById(@PathVariable String id, OAuth2AuthenticationToken authentication){
        if(authService.loggedInUserHasId(authentication,id)){
            service.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping()
    WichtelUser update(@RequestBody WichtelUserDTO dto,OAuth2AuthenticationToken authentication){
        WichtelUser user = authService.getUserFromAuthToken(authentication);
        return service.update(dto,user.getId());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NoSuchElementException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(IllegalCallerException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleIllegalCallerException(IllegalCallerException exception) {
        return exception.getMessage();
    }
}
