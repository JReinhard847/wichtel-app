package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.WichtelUser;
import org.example.backend.model.WichtelUserDTO;
import org.example.backend.repo.WichtelUserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

import static org.example.backend.util.DTOConverter.fromDTO;

@Service
@RequiredArgsConstructor
public class WichtelUserService {

    private final WichtelUserRepo repo;
    private final IdService idService;

    public WichtelUser findById(String id) {
        return repo.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public WichtelUser save(WichtelUserDTO dto, String email) {
        return repo.save(fromDTO(dto,idService.generateId(),email));
    }

    public WichtelUser updateName(String name, String id){
        WichtelUser user = findById(id);
        return repo.save(user.withName(name));
    }

    public WichtelUser updateEmail(String email,String id){
        WichtelUser user = findById(id);
        return repo.save(user.withEmail(email));
    }

    public void deleteById(String id){
        repo.deleteById(id);
    }

    public List<WichtelUser> findAll(){
        return repo.findAll();
    }

}
