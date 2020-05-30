package com.example.qz.repositories;

import java.util.List;

import com.example.qz.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByName(String name);

    List<User> findByIsLogged(Boolean isLogged);
}
