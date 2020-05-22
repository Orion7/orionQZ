package com.example.qz.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DBRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void resetSequence() {
        jdbcTemplate.execute("ALTER SEQUENCE ready_counter RESTART WITH 0");
    }
}
