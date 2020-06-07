package com.example.qz.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String answer;

    @ManyToOne
    @JoinColumn(name="question_id", nullable=false)
    private Question question;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    private LocalDateTime date;

    private Boolean processed;
}
