package com.example.qz.repositories;

import com.example.qz.dto.Question;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Long> {
}
