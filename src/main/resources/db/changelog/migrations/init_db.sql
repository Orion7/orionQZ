--liquibase formatted sql

--changeset rinat-s:add persons
CREATE TABLE users( id SERIAL, name VARCHAR(255), score integer default 0);
insert into users (name) values ('user'), ('huuser');

create table question(id serial, description text, cost integer default 0);

CREATE SEQUENCE IF NOT EXISTS ready_counter MINVALUE 0 START 0;

CREATE TABLE game (id serial, title VARCHAR(255));