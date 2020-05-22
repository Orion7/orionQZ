package com.example.qz.repositories;

import com.example.qz.dto.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Long> {
    @Query(value = "SELECT nextval('ready_counter')", nativeQuery = true)
    Integer getCurrent();
}
