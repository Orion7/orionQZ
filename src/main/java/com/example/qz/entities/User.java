package com.example.qz.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String nickname;
    private Integer score;

    @Column(name = "is_logged")
    private Boolean isLogged;

    public void addPoints(Integer points) {
        score += points;
    }

    public void subtractPoints(Integer points) {
        score -= points;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", nickname='" + nickname + '\'' +
            ", score=" + score +
            ", isLogged=" + isLogged +
            '}';
    }
}
