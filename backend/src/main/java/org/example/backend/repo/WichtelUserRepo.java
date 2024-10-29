package org.example.backend.repo;

import org.example.backend.model.WichtelUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WichtelUserRepo extends MongoRepository<WichtelUser,String> {
    Optional<WichtelUser> findByOauthProviderAndOauthId(String oauthProvider,String oauthId);
}
