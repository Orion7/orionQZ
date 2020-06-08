package com.example.qz.repositories;

import java.util.List;
import java.util.Optional;

import com.example.qz.entities.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Long> {
    @Query(value = "SELECT nextval('ready_counter')", nativeQuery = true)
    Integer getCurrent();

    List<Question> findByGameId(Long gameId);

    Optional<Question> findByActive(Boolean active);
}
