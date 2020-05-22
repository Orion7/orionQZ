package com.example.qz.repositories;

import com.example.qz.entities.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long> {
}
