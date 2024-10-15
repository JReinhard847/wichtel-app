package org.example.backend.repo;

import org.example.backend.model.WichtelEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WichtelEventRepo extends MongoRepository<WichtelEvent,String> {
}
