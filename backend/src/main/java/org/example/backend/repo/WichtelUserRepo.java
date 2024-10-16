package org.example.backend.repo;

import org.example.backend.model.WichtelUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WichtelUserRepo extends MongoRepository<WichtelUser,String> {
}
