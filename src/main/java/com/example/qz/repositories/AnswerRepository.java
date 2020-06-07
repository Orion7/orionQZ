package com.example.qz.repositories;

import java.util.List;

import com.example.qz.entities.Answer;
import org.springframework.data.repository.CrudRepository;

public interface AnswerRepository extends CrudRepository<Answer, Long> {
    List<Answer> findByProcessed(Boolean processed);
}
