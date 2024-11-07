package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.WichtelUser;
import org.example.backend.model.WichtelUserDTO;
import org.example.backend.repo.WichtelUserRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.example.backend.util.DTOConverter.fromDTO;
import static org.example.backend.util.UpdateUtil.updateIgnoringNulls;

@Service
@RequiredArgsConstructor
public class WichtelUserService{

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
        WichtelUser user = repo.findById(id).orElseThrow(NoSuchElementException::new);
        updateIgnoringNulls(fromDTO(dto,id),user);
        return repo.save(user);
    }

    public List<WichtelUser> findAll(){
        return repo.findAll();
    }



}
