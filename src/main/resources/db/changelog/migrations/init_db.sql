--liquibase formatted sql

--changeset rinat-s:add persons

DROP TABLE IF EXISTS users;
CREATE TABLE users( id SERIAL, name VARCHAR(255), score integer default 0);
insert into users (name) values ('user'), ('huuser');

drop table if exists question;
create table question(id serial, description text, cost integer default 0);
insert into question (description) values ('В чем смысл жизни?');