package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.WichtelUser;
import org.example.backend.model.WichtelUserDTO;
import org.example.backend.service.WichtelUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class WichtelUserController {

    private final WichtelUserService service;

    @GetMapping("/{id}")
    WichtelUser findById(@PathVariable String id){
        return service.findById(id);
    }

    @GetMapping
    List<WichtelUser> findAll(){
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    void deleteById(@PathVariable String id){
        service.deleteById(id);
    }

    @PostMapping
    WichtelUser createUser(@RequestBody WichtelUserDTO dto){
        return service.save(dto);
    }

    @PutMapping("/{id}")
    WichtelUser update(@RequestBody WichtelUserDTO dto,@PathVariable String id){
        return service.update(dto,id);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NoSuchElementException exception) {
        return exception.getMessage();
    }
}
